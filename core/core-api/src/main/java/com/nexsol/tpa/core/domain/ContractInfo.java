package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record ContractInfo(String contractName, String contractBusinessNumber, String contractAddress) {

}
