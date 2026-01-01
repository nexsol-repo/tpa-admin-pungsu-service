package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.tpa.nexsol.client.memo.CreateMemoRequest;
import com.tpa.nexsol.client.memo.MemoClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsuredEventListener {

    private final MemoClient memoClient;

    @EventListener
    public void handleInsuredModified(InsuredModifiedEvent event) {
        try {
            String token = getJwtToken();
            // 메모 서비스 호출
            memoClient.registerMemo(Long.valueOf(event.contractId()), // Integer -> Long
                    new CreateMemoRequest(event.memoContent(), "PUNGSU"), token);
        }
        catch (Exception e) {
            log.error("메모 저장 실패 contractId={}", event.contractId(), e);
            // 데이터 정합성이 중요하다면 예외를 던져서 트랜잭션 롤백
            throw new CoreException(ErrorType.DEFAULT_ERROR, "메모 저장 중 오류가 발생했습니다.");
        }
    }

    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null) {
                return token;
            }
        }
        // 토큰이 없는 경우(ex: 테스트 코드, 내부 스케줄러 등) 처리 필요
        log.warn("현재 요청에 Authorization 헤더가 없습니다.");
        return ""; // 혹은 예외 발생
    }

}