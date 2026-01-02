package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.tpa.nexsol.client.memo.CreateMemoRequest;
import com.tpa.nexsol.client.memo.CreateSystemLogRequest;
import com.tpa.nexsol.client.memo.MemoClient;
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
    public void handleSystemLog(InsuredSystemLogEvent event){
        try {
            memoClient.registerSystemLog(Long.valueOf(event.contractId()),new CreateSystemLogRequest(event.content(),ServiceType.PUNGSU),event.writerId(),event.token());
        }catch (Exception e){
            log.error("시스템 변경 로그 저장 실패 contractId={}", event.contractId(), e);
        }
    }

}