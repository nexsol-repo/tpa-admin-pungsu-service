package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuredContractDetail(Integer id, String referIdx, String prctrNo, InsuredInfo insuredInfo,
        ContractInfo contractInfo, BusinessLocationInfo location, InsuredSubscriptionInfo subscription) {
}
