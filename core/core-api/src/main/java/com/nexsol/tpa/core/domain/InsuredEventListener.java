package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.MailType;
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

import java.time.format.DateTimeFormatter;

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
                    new CreateMemoRequest(event.memoContent(), ServiceType.PUNGSU), event.writerId());
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
                    new CreateSystemLogRequest(event.content(), ServiceType.PUNGSU), event.writerId());
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
        String message = buildSmsMessage(event);

        // 1. 메일 발송 및 이력 저장
        try {
            emailSender.send(event.email(), event.type(), event.link(), event.name());
            memoClient.recordNotification(cId,
                    new CreateNotificationRequest("MAIL", event.type().getTitleSuffix() + " 발송 완료", ServiceType.PUNGSU),
                    adminId);
        }
        catch (Exception e) {
            log.error("메일 발송/이력저장 실패: {}", event.contractId(), e);
        }

        // 2. 문자 발송 및 이력 저장
        try {
            smsSender.sendSms(event.phoneNumber(), message);
            memoClient.recordNotification(cId, new CreateNotificationRequest("SMS", message, ServiceType.PUNGSU),
                    adminId);
        }
        catch (Exception e) {
            log.error("문자 발송/이력저장 실패: {}", event.contractId(), e);
        }
    }

    private String buildSmsMessage(InsuredIntegratedNotificationEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = event.applicationDate() != null ? event.applicationDate().format(formatter) : "-";

        if (event.type() == MailType.CERTIFICATE) {
            // 가입확인서/증권 템플릿
            String docName = "Y".equals(event.payYn()) ? "증권" : "가입확인서";
            String termsUrl = "https://bit.ly/dsf6MrTermsV3_2"; // 고정 약관 URL

            return """
                    안녕하세요? %s 님
                    TPA KOREA입니다.
                    %s %s를 통해 가입하신 실손보상소상공인풍수해·지진재해보험(Ⅵ) %s 발송드립니다.
                    [증권번호 %s]
                    아래 링크를 통해 해당 %s을 확인 바랍니다.
                    %s
                    약관보기
                    %s
                    기타 궁금하신 사항은 아래 문의처로 연락 바랍니다.

                    ※문의처
                    티피에이코리아 주식회사 고객센터 02-6952-6525
                    (평일 9:00~18:00)
                    """.formatted(event.name(), formattedDate, event.account(), docName, event.policyNumber(), docName,
                    event.link(), termsUrl);

        }
        else if (event.type() == MailType.REJOIN) {
            // 재가입 템플릿
            return """
                    (광고) [TPA KOREA] [보험기간 만기 안내]
                    실손보상소상공인풍수해·지진재해보험(Ⅵ) 보험기간 만료 및 갱신안내
                    %s 님, %s 통해 가입하신 실손보상소상공인풍수해·지진재해보험(Ⅵ)의 보험만료일이 %s까지 입니다.
                    재계약이 필요하시면 아래 가입 바로가기를 눌러 바로 간편하게 가입 가능합니다.

                    ■ 가입 바로가기
                    %s

                    * 보험가입시 알아두실 사항
                    ⊙ 이 보험계약은 예금자보호법에 따라... (생략)
                    * 수신거부 : 티피에이코리아 주식회사 고객센터 02-6952-6525
                    """.formatted(event.name(), event.account(), formattedDate, event.link());
        }
        return "";
    }

}