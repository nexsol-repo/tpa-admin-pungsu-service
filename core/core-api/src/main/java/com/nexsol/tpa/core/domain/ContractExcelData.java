package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ContractExcelData(InsuredContract contract, InsuredInfo insured, BusinessLocationInfo location,
        InsuredSubscriptionInfo subscription) {
}