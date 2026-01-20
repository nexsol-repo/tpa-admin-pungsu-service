package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.UpdateCount;
import lombok.Builder;

@Builder
public record FreeContractUploadResponse(int totalCount, int successCount, int failureCount) {

    public static FreeContractUploadResponse of(UpdateCount stats) {
        return FreeContractUploadResponse.builder()
            .totalCount(stats.totalCount())
            .successCount(stats.successCount())
            .failureCount(stats.failCount())
            .build();
    }
}