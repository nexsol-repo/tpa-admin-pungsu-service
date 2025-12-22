package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsuredContractInfo;
import com.nexsol.tpa.core.domain.InsuredInfo;
import lombok.Builder;

@Builder
public record InsuredModifyRequest(InsuredInfo insuredInfo, InsuredContractInfo contractInfo) {
}
