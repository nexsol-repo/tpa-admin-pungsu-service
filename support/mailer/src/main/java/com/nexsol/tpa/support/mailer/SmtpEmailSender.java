package com.nexsol.tpa.support.mailer;

import com.nexsol.tpa.core.enums.MailType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(String toEmail, MailType mailType, String link, String name) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(mailType.getTitle());

            String html = getHtmlTemplate(mailType, link, name);
            helper.setText(html, true);

            javaMailSender.send(message);
            log.info("메일발송 성공 풍수해6: {}", toEmail);
        }
        catch (Exception e) {
            log.error("메일 발송 실패: {}", toEmail, e);
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }

    private String getHtmlTemplate(MailType type, String link, String name) {
        // role="presentation": 테이블을 레이아웃 용도로만 사용함을 명시 (시맨틱 웹 표준)
        // String.formatted() 사용 시 % 문자는 포맷 지정자로 인식되므로, 단순 문자 %를 표현하려면 %%로 이스케이프해야 합니다.
        // width='100%' -> width='100%%' 로 수정됨
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>TPA KOREA 풍수해</title>
                  <style>
                    /* 기본 초기화 및 웹 표준 스타일 */
                    body { margin: 0; padding: 0; background-color: #eee; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                    table { border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; }
                    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; display: block; }

                    /* 링크 강조 스타일 */
                    .link-box:hover { text-decoration: underline; }
                  </style>
                </head>
                <body style="margin: 0; padding: 0; background-color: #eee; font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;">

                <table role="presentation" style="width: 100%; background-color: #eee; border-collapse: collapse;">
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
                          <td colspan="2" style="padding: 30px 0; border-bottom: 1px solid #e7e9eb;">
                            <p style="font-size: 16px; line-height: 1.6; margin: 0 0 16px 0; color: #333;">
                              안녕하세요, <strong>%s</strong> 고객님.<br />
                              TPA KOREA 풍수해6보험 전담센터입니다.<br />
                              아래 링크를 클릭하시면 <b>%s</b>으로 이동합니다.
                            </p>
                            <div style="margin: 25px 0;">
                              <a href="%s" target="_blank" style="color: #00B855; font-size: 18px; text-decoration: none; font-weight: bold;">
                                링크 : %s
                              </a>
                            </div>
                            <p style="margin: 20px 0 0 0; font-size: 15px; line-height: 1.6; color: #555; letter-spacing: -0.5px;">
                              회원을 등록하지 않은 경우, 이 이메일을 무시하거나<br />
                              TPA KOREA 풍수해6 고객센터(<strong>1644-9664</strong>)로 문의해 주시기 바랍니다.<br />
                              좋은 하루 되시길 바랍니다.<br />
                              감사합니다.
                            </p>
                          </td>
                        </tr>

                        <tr>
                          <td colspan="2" style="padding: 30px 0;">
                            <table role="presentation" style="width: 100%; border-collapse: collapse;">
                              <tbody>
                              <tr>
                                <td colspan="2" style="padding-bottom: 16px;">
                                  <h2 style="font-size: 18px; margin: 0; color: #000; letter-spacing: -0.5px;">
                                    TPA KOREA 풍수해6 고객센터
                                  </h2>
                                </td>
                              </tr>
                              <tr>
                                <td style="width: 90px; padding: 4px 0; font-size: 15px; color: #333;">대표전화</td>
                                <td style="padding: 4px 0; font-size: 15px; color: #333;">: 1644-9664</td>
                              </tr>
                              <tr>
                                <td style="width: 90px; padding: 4px 0; font-size: 15px; color: #333;">상담 시간</td>
                                <td style="padding: 4px 0; font-size: 15px; color: #333;">: 평일 09:00~18:00 (점심 12:00~13:00)</td>
                              </tr>
                              <tr>
                                <td style="width: 90px; padding: 4px 0; font-size: 15px; color: #333;">웹사이트</td>
                                <td style="padding: 4px 0; font-size: 15px; color: #333;">
                                  : <a href="https://pungsu.tpakorea.com/" target="_blank" style="color: #00B855; text-decoration: underline;">공식 홈페이지 바로가기</a>
                                </td>
                              </tr>
                              </tbody>
                            </table>
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
            .formatted(type.name().equals("REJOIN") ? "보험료 안내" : "가입확인서 안내", name, type.getTargetName(), link,
                    type.getLinkText());
    }

}
