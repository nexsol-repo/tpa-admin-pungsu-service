package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.support.mailer.EmailSender;
import com.nexsol.tpa.client.aligo.SmsSender;
import com.nexsol.tpa.client.memo.CreateMemoRequest;
import com.nexsol.tpa.client.memo.CreateNotificationRequest;
import com.nexsol.tpa.client.memo.CreateSystemLogRequest;
import com.nexsol.tpa.client.memo.MemoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsuredEventListener {

    private final MemoClient memoClient;

    private final EmailSender emailSender;

    private final SmsSender smsSender;

    @Async
    @EventListener
    public void handleInsuredModified(InsuredModifiedEvent event) {
        try {

            // 메모 서비스 호출
            memoClient.registerMemo(Long.valueOf(event.contractId()), // Integer -> Long
                    new CreateMemoRequest(event.memoContent(), ServiceType.PUNGSU), event.writerId(), event.token());
        }
        catch (Exception e) {
            log.error("메모 저장 실패 contractId={}", event.contractId(), e);
            // 데이터 정합성이 중요하다면 예외를 던져서 트랜잭션 롤백
            throw new CoreException(ErrorType.DEFAULT_ERROR, "메모 저장 중 오류가 발생했습니다.");
        }
    }

    @Async
    @EventListener
    public void handleSystemLog(InsuredSystemLogEvent event) {
        try {
            memoClient.registerSystemLog(Long.valueOf(event.contractId()),
                    new CreateSystemLogRequest(event.content(), ServiceType.PUNGSU), event.writerId(), event.token());
        }
        catch (Exception e) {
            log.error("시스템 변경 로그 저장 실패 contractId={}", event.contractId(), e);
        }
    }

    @Async
    @EventListener
    public void handleIntegratedNotification(InsuredIntegratedNotificationEvent event) {
        String token = event.token();
        String adminId = event.writerId();
        Long cId = Long.valueOf(event.contractId());
        String message = """
                [TPA KOREA]
                안녕하세요, %s 고객님.
                %s을 위해 아래 링크를 클릭해주세요.

                링크: %s

                감사합니다.""".formatted(event.name(), event.type().getTitle(), event.link());

        // 1. 메일 발송 및 이력 저장
        try {
            emailSender.send(event.email(), event.type(), event.link(), event.name());
            memoClient.recordNotification(cId,
                    new CreateNotificationRequest("MAIL", event.type().getTitleSuffix() + " 발송 완료", ServiceType.PUNGSU),
                    adminId, token);
        }
        catch (Exception e) {
            log.error("메일 발송/이력저장 실패: {}", event.contractId(), e);
        }

        // 2. 문자 발송 및 이력 저장
        try {
            smsSender.sendSms(event.phoneNumber(), message);
            memoClient.recordNotification(cId,
                    new CreateNotificationRequest("SMS", event.type().getTitle() + " 발송 완료", ServiceType.PUNGSU),
                    adminId, token);
        }
        catch (Exception e) {
            log.error("문자 발송/이력저장 실패: {}", event.contractId(), e);
        }
    }

}