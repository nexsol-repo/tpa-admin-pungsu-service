package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.file.core.FileStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsuredService {

    private final InsuredContractFinder insuredContractFinder;

    private final InsuredContractorWriter insuredContractorWriter;

    private final FileStorageClient fileStorageClient;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public DomainPage<InsuredContract> getList(InsuredSearchCondition condition, OffsetLimit offsetLimit) {
        return insuredContractFinder.find(condition, offsetLimit);
    }

    @Transactional(readOnly = true)
    public InsuredContractDetail getDetail(Integer id) {
        return insuredContractFinder.findDetail(id);
    }

    @Transactional
    public Integer modify(Integer id, InsuredInfo insured, ContractInfo contract, BusinessLocationInfo location,
            InsuredSubscriptionInfo subscription, String memoContent, Long adminId) {

        List<ChangeDetail> diffs = insuredContractorWriter.writeAndGetDiff(id, insured, contract, location,
                subscription);
        String token = getJwtToken();

        if (!diffs.isEmpty()) {
            String systemLogContent = "시스템 변경: "
                    + diffs.stream().map(ChangeDetail::toString).collect(Collectors.joining(", "));

            eventPublisher
                .publishEvent(new InsuredSystemLogEvent(id, systemLogContent, String.valueOf(adminId), token));
        }

        // 3. 관리자가 직접 작성한 메모가 있다면 이벤트 발행
        if (StringUtils.hasText(memoContent)) {
            eventPublisher.publishEvent(new InsuredModifiedEvent(id, memoContent, String.valueOf(adminId), token));
        }

        return id;
    }

    @Transactional
    public void register(InsuredInfo insured, ContractInfo contract, BusinessLocationInfo location,
            InsuredSubscriptionInfo subscription, String memoContent, Long adminId) {
        Integer contractId = insuredContractorWriter.write(insured, contract, location, subscription);

        String token = getJwtToken();

        // 초기 메모가 있다면 이벤트 발행 (기존 이벤트 활용 또는 메모 전용 이벤트)
        if (StringUtils.hasText(memoContent)) {
            eventPublisher
                .publishEvent(new InsuredModifiedEvent(contractId, memoContent, String.valueOf(adminId), token));
        }

        // 신규 등록 시스템 로그 이벤트 발행
        eventPublisher
            .publishEvent(new InsuredSystemLogEvent(contractId, "관리자 직접 등록(신규)", String.valueOf(adminId), token));
    }

    @Transactional
    public void sendRenewalNotifications(int days) {
        // 1. 7일 뒤 만료 대상자 조회 (Implement Layer에 위임)
        List<InsuredContractDetail> targets = insuredContractFinder.findExpiringContracts(days);

        // 2. 비즈니스 흐름 중계
        targets.forEach(detail -> {
            // 재가입 URL 생성 (비즈니스 정책상 필요한 URL 조합)
            String rejoinUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.referIdx();

            // 알림 발송 명령 (기존 send 메서드 재사용하여 이벤트 발행)
            this.send(detail, MailType.REJOIN, rejoinUrl, 0L); // 시스템 자동 발송은 adminId 0 처리
        });
    }

    public void send(InsuredContractDetail detail, MailType type, String targetUrl, Long adminId) {
        String token = getJwtToken();
        eventPublisher.publishEvent(new InsuredIntegratedNotificationEvent(detail.id(), detail.insuredInfo().name(),
                detail.insuredInfo().email(), detail.insuredInfo().phoneNumber(), type, targetUrl,
                String.valueOf(adminId), token));
    }

    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("Authorization");
        }
        return null;
    }

    public File uploadCertificate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "파일이 비어있습니다.");
        }

        // 1. 저장 경로(Key) 생성: pungsu/certificates/yyyyMMdd/UUID_파일명.pdf
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = file.getOriginalFilename();
        String objectKey = String.format("pungsu/certificates/%s/%s_%s", dateDir, UUID.randomUUID(), fileName);

        try {
            // 2. S3 업로드 실행
            fileStorageClient.upload(file.getInputStream(), objectKey, file.getSize(), file.getContentType());

            return new File(objectKey, fileName);
        }
        catch (IOException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR, "파일 읽기 중 오류가 발생했습니다.");
        }
    }

}
