package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.*;
import lombok.Builder;

@Builder
public record InsuredContractDetailResponse(Integer id, String referIdx, InsuredInfo insuredInfo,
        ContractInfo contractInfo, BusinessLocationInfo location, InsuredSubscriptionInfo subscription,
        String certificateUrl) {

    public static InsuredContractDetailResponse of(InsuredContractDetail detail, String certificateUrl) {
        return InsuredContractDetailResponse.builder()
            .id(detail.id())
            .referIdx(detail.referIdx())
            .insuredInfo(detail.insuredInfo())
            .contractInfo(detail.contractInfo())
            .location(detail.location())
            .subscription(detail.subscription())
            .certificateUrl(certificateUrl)
            .build();
    }
}
