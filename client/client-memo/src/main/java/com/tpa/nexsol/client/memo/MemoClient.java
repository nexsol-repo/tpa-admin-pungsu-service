package com.tpa.nexsol.client.memo;

import com.tpa.nexsol.client.memo.config.FeignHeaderConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "memo-service", url = "${external.memo-service.url}", configuration = FeignHeaderConfig.class)
public interface MemoClient {

//    @PostMapping("/v1/admin/memo/{contractId}")
//    void registerMemo(@PathVariable("contractId") Long contractId, @RequestBody CreateMemoRequest request,  @RequestHeader("Authorization") String token );
@PostMapping("/v1/admin/memo/{contractId}")
void registerMemo(@PathVariable("contractId") Long contractId, @RequestBody CreateMemoRequest request);
}
