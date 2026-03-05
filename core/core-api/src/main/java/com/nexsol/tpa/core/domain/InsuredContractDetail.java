package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DisplayStatus;
import lombok.Builder;

@Builder
public record InsuredContractDetail(Integer id, String referIdx, String prctrNo, DisplayStatus displayStatus,
        InsuredInfo insuredInfo, ContractInfo contractInfo, BusinessLocationInfo location,
        InsuredSubscriptionInfo subscription, PaymentInfo payment) {
}