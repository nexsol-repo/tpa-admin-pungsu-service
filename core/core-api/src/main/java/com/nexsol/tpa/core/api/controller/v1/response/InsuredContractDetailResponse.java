package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.InsuredContractDetail;
import com.nexsol.tpa.core.domain.InsuredContractInfo;
import com.nexsol.tpa.core.domain.InsuredInfo;
import lombok.Builder;

@Builder
public record InsuredContractDetailResponse(Integer id, InsuredInfo insuredInfo, InsuredContractInfo contractInfo,
        String certificateUrl) {

    public static InsuredContractDetailResponse of(InsuredContractDetail detail, String certificateUrl) {
        return InsuredContractDetailResponse.builder()
            .id(detail.id())
            .insuredInfo(detail.insuredInfo())
            .contractInfo(detail.contractInfo())
            .certificateUrl(certificateUrl)
            .build();
    }
}
