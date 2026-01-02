package com.nexsol.tpa.client.aligo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AligoSmsSender implements SmsSender {

    private final AligoClient aligoClient;

    @Value("${external.aligo.key}")
    private String apiKey;

    @Value("${external.aligo.user-id}")
    private String userId;

    @Value("${external.aligo.sender}")
    private String senderNumber;

    @Override
    public void sendSms(String phoneNumber, String name, String link) {
        Map<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("user_id", userId);
        params.put("sender", senderNumber);
        params.put("receiver", phoneNumber);

        // 알리고 가이드에 따른 메시지 구성 (LMS 자동 전환 지원)
        String msg = String.format("[TPA KOREA]\n안녕하세요, %s 고객님.\n재가입 신청을 위해 아래 링크를 클릭해주세요.\n\n링크: %s\n\n감사합니다.", name,
                link);
        params.put("msg", msg);

        try {
            Map<String, Object> response = aligoClient.sendSms(params);
            if (!"1".equals(String.valueOf(response.get("result_code")))) {
                log.error("알리고 SMS 발송 실패: {}", response.get("message"));
            }
        }
        catch (Exception e) {
            log.error("SMS 발송 중 예외 발생: {}", phoneNumber, e);
        }
    }

}
