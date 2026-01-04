package com.nexsol.tpa.client.aligo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
        // [수정] HashMap -> LinkedMultiValueMap 변경
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", apiKey);
        params.add("user_id", userId);
        params.add("sender", senderNumber);
        params.add("receiver", phoneNumber);

        String msg = String.format("[TPA KOREA]\n안녕하세요, %s 고객님.\n재가입 신청을 위해 아래 링크를 클릭해주세요.\n\n링크: %s\n\n감사합니다.", name,
                link);
        params.add("msg", msg);

        try {
            // [수정] MultiValueMap을 넘김
            Map<String, Object> response = aligoClient.sendSms(params);

            // (이전 턴에서 수정한 에러 핸들링 로직 유지)
            if (!"1".equals(String.valueOf(response.get("result_code")))) {
                String errorMsg = (String) response.get("message");
                log.error("알리고 SMS 발송 실패: {}", errorMsg);
                throw new RuntimeException("알리고 SMS 발송 실패: " + errorMsg);
            }
        }
        catch (Exception e) {
            log.error("SMS 발송 중 예외 발생: {}", phoneNumber, e);
            throw new RuntimeException("SMS 발송 중 시스템 오류 발생", e);
        }
    }

}