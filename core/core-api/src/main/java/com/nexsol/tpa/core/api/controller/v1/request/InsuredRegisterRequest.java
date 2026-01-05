package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.BusinessLocationInfo;
import com.nexsol.tpa.core.domain.ContractInfo;
import com.nexsol.tpa.core.domain.InsuredSubscriptionInfo;
import com.nexsol.tpa.core.domain.InsuredInfo;
import lombok.Builder;

@Builder
public record InsuredRegisterRequest(InsuredInfo insuredInfo, ContractInfo contractInfo, BusinessLocationInfo location,
        InsuredSubscriptionInfo subscription, String memoContent) {
}