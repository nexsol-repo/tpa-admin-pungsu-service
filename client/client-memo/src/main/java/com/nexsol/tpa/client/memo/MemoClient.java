package com.nexsol.tpa.client.memo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "memo-service", url = "${external.memo-service.url}")
public interface MemoClient {

    @PostMapping("/v1/admin/memo/{contractId}")
    void registerMemo(@PathVariable("contractId") Long contractId, @RequestBody CreateMemoRequest request,
            @RequestHeader("X-User-Id") String userId);

    @PostMapping("/v1/admin/memo/{contractId}/system-log")
    void registerSystemLog(@PathVariable("contractId") Long contractId, @RequestBody CreateSystemLogRequest request,
            @RequestHeader("X-User-Id") String userId);

    @PostMapping("/v1/admin/memo/{contractId}/notification")
    void recordNotification(@PathVariable("contractId") Long contractId, @RequestBody CreateNotificationRequest request,
            @RequestHeader("X-User-Id") String userId);

}
