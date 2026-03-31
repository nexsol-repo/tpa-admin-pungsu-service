package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.client.memo.CreateMemoRequest;
import com.nexsol.tpa.client.memo.CreateSystemLogRequest;
import com.nexsol.tpa.client.memo.MemoClient;
import com.nexsol.tpa.client.memo.SendMailRequest;
import com.nexsol.tpa.client.memo.SendSmsRequest;
import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsuredEventListener {

    private final MemoClient memoClient;

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
        String adminId = event.writerId();
        Long cId = Long.valueOf(event.contractId());
        String smsMessage = buildSmsMessage(event);
        String mailTitle = event.type().getTitle();
        String mailContent = buildMailContent(event);

        // 1. 메일 발송 (memo API에서 발송 + 이력 저장) - 이메일이 없으면 스킵
        if (event.email() != null && !event.email().isBlank()) {
            try {
                memoClient.sendMail(cId,
                        new SendMailRequest(ServiceType.PUNGSU, List.of(event.email()), mailTitle, mailContent), adminId);
            }
            catch (Exception e) {
                log.error("메일 발송 실패: {}", event.contractId(), e);
            }
        } else {
            log.info("이메일 없음 - 메일 발송 스킵: contractId={}", event.contractId());
        }

        // 2. 문자 발송 (memo API에서 발송 + 이력 저장) - 전화번호가 없으면 스킵
        if (event.phoneNumber() != null && !event.phoneNumber().isBlank()) {
            try {
                memoClient.sendSms(cId, new SendSmsRequest(ServiceType.PUNGSU, List.of(event.phoneNumber()),
                        event.type().getTitleSuffix(), smsMessage), adminId);
            }
            catch (Exception e) {
                log.error("문자 발송 실패: {}", event.contractId(), e);
            }
        } else {
            log.info("전화번호 없음 - 문자 발송 스킵: contractId={}", event.contractId());
        }
    }

    private String buildMailContent(InsuredIntegratedNotificationEvent event) {
        String textContent = buildSmsMessage(event);
        String htmlBody = textContent.strip().replace("\n", "<br>\n");
        String title = event.type().getTitle();

        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>TPA KOREA 풍수해</title>
                  <style>
                    body { margin: 0; padding: 0; background-color: #eee; -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                    table { border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; }
                  </style>
                </head>
                <body style="margin: 0; padding: 0; background-color: #eee; font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;">

                <table role="presentation" style="width: 100%%; background-color: #eee; border-collapse: collapse;">
                  <tbody>
                  <tr>
                    <td style="padding: 30px 0; text-align: center;">

                      <table role="presentation" style="width: 600px; margin: 0 auto; background-color: #ffffff; padding: 36px; border-radius: 8px; text-align: left;">
                        <tbody>
                        <tr>
                          <td style="border-bottom: 2px solid #000; padding: 8px 0;">
                            <h1 style="font-size: 28px; margin: 0; color: #333; text-align: left;">%s</h1>
                          </td>
                          <td style="border-bottom: 2px solid #000; text-align: right; font-size: 14px; color: #666; vertical-align: bottom; padding-bottom: 8px;">
                            풍수해6종합보험
                          </td>
                        </tr>

                        <tr>
                          <td colspan="2" style="padding: 30px 0;">
                            <p style="font-size: 15px; line-height: 1.8; margin: 0; color: #333;">
                              %s
                            </p>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table>
                </body>
                </html>
                """
            .formatted(title, htmlBody);
    }

    private String buildSmsMessage(InsuredIntegratedNotificationEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = event.applicationDate() != null ? event.applicationDate().format(formatter) : "-";

        return switch (event.type()) {
            case CERTIFICATE -> {
                String docName = "Y".equals(event.payYn()) ? "증권" : "가입확인서";
                String termsUrl = "https://bit.ly/dsf6MrTermsV3_2";
                yield """
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
                        """.formatted(event.name(), formattedDate, event.account(), docName, event.policyNumber(),
                        docName, event.link(), termsUrl);
            }
            case JOINED -> {
                String docName = "Y".equals(event.payYn()) ? "증권" : "가입확인서";
                String termsUrl = "https://bit.ly/dsf6MrTermsV3_2";
                yield """
                        안녕하세요? %s 님
                        ㈜티피에이코리아입니다.
                        %s %s을/를 통해 신청하신
                        보험 상품명: 실손보상소상공인풍수해·지진재해보험(Ⅵ) 정상 가입 완료 처리되었습니다.

                        [증권번호 %s]

                        아래 URL을 통해 %s을/를 확인해 주시기 바랍니다.
                        ▶ %s 확인하기
                        %s
                        ▶ 약관보기
                        %s

                        기타 문의 사항은 아래 연락처로 문의 바랍니다.
                        ※문의처
                        TPA KOREA 고객센터 02-6952-6525
                        (평일 9시~18시/점심시간 12시~13시, 공휴일 제외)
                        """.formatted(event.name(), formattedDate, event.account(), event.policyNumber(), docName,
                        docName, event.link(), termsUrl);
            }
            case CANCELLED -> {
                String startDate = event.insuranceStartDate() != null ? event.insuranceStartDate().format(formatter)
                        : "-";
                String endDate = event.insuranceEndDate() != null ? event.insuranceEndDate().format(formatter) : "-";
                yield """
                        안녕하세요. %s 님
                        ㈜티피에이코리아입니다.
                        %s을/를 통해 가입하신 보험 상품명: 실손보상소상공인풍수해·지진재해보험(Ⅵ)
                        [증권번호 %s] 보험 기간 %s ~ %s 계약이 최종 해지 처리되었습니다.

                        보험료 환불처리가 완료되면 다시 안내해 드리겠습니다.

                        가입 상태는 아래 URL을 통해 간편하게 확인 가능합니다.
                        ▶ 가입 정보 확인하기
                        %s

                        기타 문의 사항은 아래 연락처로 문의 바랍니다.

                        ※문의처
                        TPA KOREA 고객센터 02-6952-6525
                        (평일 9시~18시/점심시간 12시~13시, 공휴일 제외)
                        """.formatted(event.name(), event.account(), event.policyNumber(), startDate, endDate,
                        event.link());
            }
            case REJOIN ->
                """
                        (광고) [풍수해·지진재해보험 만기 및 갱신 안내]

                        %s 님, 안녕하세요.

                        가입하신 [소상공인 풍수해·지진재해보험(VI)]의 만기가 %s로 다가와 안내드립니다.
                        기존 무상 지원 프로모션은 종료되었으나 정부와 지자체의 60%% 보험료 지원은 계속됩니다.
                        올해는 본인 부담금 40%%만으로 사업장의 예기치 못한 재난 손실에 대비하실 수 있습니다.
                        아래 링크를 통해 간편하게 갱신 및 보험료 확인이 가능합니다.

                        ■ 메리츠화재 간편 재가입 바로가기 ▶ %s

                        보험가입 시 알아두실 사항

                        ⊙ 이 보험계약은 예금자보호법에 따라 해약환급금(또는 만기 시 보험금)에 기타지급금을 합한 금액이 1인당 "1억원까지"(본 보험회사의 여타 보호상품과 합산) 보호됩니다. 이와 별도로 본 보험회사 보호상품의 사고보험금을 합산한 금액이 1인당 "1억원까지" 보호됩니다.
                        (다만, 보험계약자 및 보험료 납부자가 법인인 보험계약의 경우에는 보호되지 않습니다.)
                        ⊙ 보험계약자가 기존 보험계약을 해지하고 새로운 보험계약을 체결할 경우 인수거절, 보험료 인상, 보장내용 축소 등 불이익이 생길 수 있습니다.
                        ⊙ 보험계약 전 가입 시 유의사항, 상품안내 및 약관을 반드시 확인하십시오.
                        ⊙ (주)티피에이코리아 준법감시인 심의필 제2026-광고-230호 (유효기간 2026.02.05 ~ 2027-02-04)

                        수신거부 : 고객센터 02-6952-6525 (평일 09:00~18:00)
                        """
                    .formatted(event.name(), formattedDate, event.link());
            case EXPIRED ->
                """
                        (광고) [풍수해·지진재해보험 만기 및 갱신 안내]

                        %s 님, 안녕하세요.

                        가입하신 [소상공인 풍수해·지진재해보험(VI)]의 보장 유지기간이 %s 만료 되었습니다.
                        기존 무상 지원 프로모션은 종료되었으나 정부와 지자체의 60%% 보험료 지원은 계속됩니다.
                        올해는 본인 부담금 40%%만으로 사업장의 예기치 못한 재난 손실에 대비하실 수 있습니다.
                        재계약이 필요하시면 아래 가입 바로가기를 눌러 바로 간편하게 가입 가능합니다.

                        ■ 메리츠화재 간편 재가입 바로가기 ▶ %s

                        보험가입 시 알아두실 사항

                        ⊙ 이 보험계약은 예금자보호법에 따라 해약환급금(또는 만기 시 보험금)에 기타지급금을 합한 금액이 1인당 "1억원까지"(본 보험회사의 여타 보호상품과 합산) 보호됩니다. 이와 별도로 본 보험회사 보호상품의 사고보험금을 합산한 금액이 1인당 "1억원까지" 보호됩니다.
                        (다만, 보험계약자 및 보험료 납부자가 법인인 보험계약의 경우에는 보호되지 않습니다.)
                        ⊙ 보험계약자가 기존 보험계약을 해지하고 새로운 보험계약을 체결할 경우 인수거절, 보험료 인상, 보장내용 축소 등 불이익이 생길 수 있습니다.
                        ⊙ 보험계약 전 가입 시 유의사항, 상품안내 및 약관을 반드시 확인하십시오.
                        ⊙ (주)티피에이코리아 준법감시인 심의필 제2026-광고-230호 (유효기간 2026.02.05 ~ 2027-02-04)

                        수신거부 : 고객센터 02-6952-6525 (평일 09:00~18:00)
                        """
                    .formatted(event.name(), formattedDate, event.link());
        };
    }

}