package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuredContractDetail(Integer id, InsuredInfo insuredInfo, InsuredContractInfo contractInfo) {
}
