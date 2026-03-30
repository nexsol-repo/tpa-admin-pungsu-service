package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DateType;
import com.nexsol.tpa.core.enums.DisplayStatus;
import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.file.core.FileStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuredService {

    private final InsuredContractFinder insuredContractFinder;

    private final InsuredContractorWriter insuredContractorWriter;

    private final InsuredExcelWriter insuredExcelWriter;

    private final FileStorageClient fileStorageClient;

    private final ApplicationEventPublisher eventPublisher;

    private static final int BATCH_SIZE = 50;

    private static final long BATCH_DELAY_MS = 1000L;

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
            InsuredSubscriptionInfo subscription, PaymentInfo payment, String memoContent, Long adminId) {
        List<String> changedSections = insuredContractorWriter.writeAndGetDiff(id, insured, contract, location,
                subscription, payment);
        String token = getJwtToken();

        if (!changedSections.isEmpty()) {
            String systemLogContent = String.join(", ", changedSections) + "가 변경되었습니다.";

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
            InsuredSubscriptionInfo subscription, PaymentInfo payment, String memoContent, Long adminId) {
        Integer contractId = insuredContractorWriter.write(insured, contract, location, subscription, payment);

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
    public void sendRenewalNotifications() {
        List<InsuredContractDetail> targets = insuredContractFinder.findExpiringContracts();

        for (int i = 0; i < targets.size(); i++) {
            InsuredContractDetail detail = targets.get(i);
            String rejoinUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.referIdx();

            try {
                this.send(detail, MailType.REJOIN, rejoinUrl, 0L);
            }
            catch (Exception e) {
                log.error("재가입 알림 발송 실패 contractId={}", detail.id(), e);
            }

            // 배치 단위마다 딜레이
            if ((i + 1) % BATCH_SIZE == 0 && i + 1 < targets.size()) {
                try {
                    Thread.sleep(BATCH_DELAY_MS);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("재가입 알림 배치 발송 중단");
                    return;
                }
            }
        }

        log.info("재가입 알림 발송 완료: 총 {}건", targets.size());
    }

    @Transactional
    public int sendRenewalNotificationsByStartDate(LocalDate startDate) {
        List<InsuredContractDetail> targets = insuredContractFinder.findContractsByStartDate(startDate);

        targets.forEach(detail -> {
            String rejoinUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.referIdx();
            this.send(detail, MailType.REJOIN, rejoinUrl, 0L);
        });

        return targets.size();
    }

    @Transactional(readOnly = true)
    public BulkNotificationPreview getBulkNotificationPreview(DateType dateType, LocalDate startDate,
            LocalDate endDate) {
        return insuredContractFinder.countByStatusForPreview(dateType, startDate, endDate);
    }

    public int sendBulkRenewalNotifications(DateType dateType, LocalDate startDate, LocalDate endDate,
            List<DisplayStatus> statuses, Long adminId) {
        List<InsuredContractDetail> targets = insuredContractFinder.findBulkNotificationTargets(dateType, startDate,
                endDate, statuses);

        for (int i = 0; i < targets.size(); i++) {
            InsuredContractDetail detail = targets.get(i);
            MailType mailType = resolveMailType(detail.displayStatus());
            String rejoinUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.referIdx();

            try {
                this.send(detail, mailType, rejoinUrl, adminId);
            }
            catch (Exception e) {
                log.error("대량 발송 실패 contractId={}", detail.id(), e);
            }

            if ((i + 1) % BATCH_SIZE == 0 && i + 1 < targets.size()) {
                try {
                    Thread.sleep(BATCH_DELAY_MS);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("대량 발송 배치 처리 중단");
                    return i + 1;
                }
            }
        }

        log.info("대량 재가입 안내 발송 완료: 총 {}건", targets.size());
        return targets.size();
    }

    private MailType resolveMailType(DisplayStatus status) {
        return switch (status) {
            case EXPIRING_SOON -> MailType.REJOIN;
            case EXPIRED -> MailType.EXPIRED;
            default -> MailType.REJOIN;
        };
    }

    @Transactional(readOnly = true)
    public void downloadExcel(String insuranceCompany, InsuredSearchCondition condition, OutputStream outputStream) {
        // 1. 기간 필수 체크
        if (condition.startDate() == null || condition.endDate() == null) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        // 2. 전체 데이터 조회 (Finder가 모든 정보를 포함한 ContractExcelData 리스트를 반환)
        List<ContractExcelData> excelDataList = insuredContractFinder.findAll(condition);

        if (excelDataList.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        // 3. 도구 레이어 위임 (이미 모든 요소가 채워진 리스트를 전달)
        insuredExcelWriter.write(insuranceCompany, excelDataList, outputStream);
    }

    @Transactional
    public UpdateCount updateFreeContracts(MultipartFile file) {
        return insuredContractorWriter.confirmFreeContract(file);
    }

    @Transactional
    public UpdateCount updateUnifiedFreeContracts(MultipartFile file) {
        return insuredContractorWriter.confirmUnifiedFreeContract(file);
    }

    public void send(InsuredContractDetail detail, MailType type, String targetUrl, Long adminId) {
        String token = getJwtToken();

        LocalDateTime referenceDate = (type == MailType.CERTIFICATE) ? detail.subscription().applicationDate()
                : detail.subscription().insuranceEndDate();

        InsuredIntegratedNotificationEvent event = InsuredIntegratedNotificationEvent.builder()
            .contractId(detail.id())
            .name(detail.location().companyName())
            .email(detail.insuredInfo().email())
            .phoneNumber(detail.insuredInfo().phoneNumber())
            .type(type)
            .link(targetUrl)
            .writerId(String.valueOf(adminId))
            .token(token)
            .applicationDate(referenceDate)
            .account(detail.subscription().account())
            .payYn(detail.subscription().payYn())
            .policyNumber(detail.subscription().insuranceNumber())
            .insuranceStartDate(detail.subscription().insuranceStartDate())
            .insuranceEndDate(detail.subscription().insuranceEndDate())
            .build();
        eventPublisher.publishEvent(event);

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
