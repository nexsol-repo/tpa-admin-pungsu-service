package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.support.mailer.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsuredService {

    private final InsuredContractFinder insuredContractFinder;

    private final InsuredContractorWriter insuredContractorWriter;

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
    public Integer modify(Integer id, InsuredInfo info, InsuredContractInfo contract, String memoContent,
            Long adminId) {

        List<ChangeDetail> diffs = insuredContractorWriter.writeAndGetDiff(id, info, contract);
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
    public void register(InsuredInfo info, InsuredContractInfo contract, String memoContent, Long adminId) {
        Integer contractId = insuredContractorWriter.create(info, contract);

        String token = getJwtToken();

        // 초기 메모가 있다면 이벤트 발행 (기존 이벤트 활용 또는 메모 전용 이벤트)
        if (StringUtils.hasText(memoContent)) {
            eventPublisher.publishEvent(new InsuredModifiedEvent(contractId, memoContent, // 또는
                                                                                          // "등록
                                                                                          // 시
                                                                                          // 메모:
                                                                                          // "
                                                                                          // +
                                                                                          // memoContent
                    String.valueOf(adminId), token));
        }

        // 신규 등록 시스템 로그 이벤트 발행
        eventPublisher
            .publishEvent(new InsuredSystemLogEvent(contractId, "관리자 직접 등록(신규)", String.valueOf(adminId), token));
    }

    // @Transactional(readOnly = true)
    // public void sendAllNotifications(Integer contractId, MailType type, Long adminId,
    // String token) {
    // // 1. 필요한 데이터 조회
    // InsuredContractDetail detail = insuredContractFinder.findDetail(contractId);
    // String referIdx = detail.insuredInfo().referIdx();
    //
    // // 2. 비즈니스 로직에 따른 링크 생성 (예: 재가입 URL)
    // String rejoinUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + referIdx;
    //
    // // 3. 통합 이벤트 발행 (리스너에서 문자/메일 동시 처리)
    // eventPublisher.publishEvent(new InsuredIntegratedNotificationEvent(
    // contractId,
    // detail.insuredInfo().name(),
    // detail.insuredInfo().email(),
    // detail.insuredInfo().phoneNumber(),
    // type,
    // rejoinUrl,
    // String.valueOf(adminId),
    // token
    // ));
    // }

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

}
