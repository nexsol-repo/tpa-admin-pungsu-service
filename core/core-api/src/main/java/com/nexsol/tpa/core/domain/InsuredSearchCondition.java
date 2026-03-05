package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DisplayStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InsuredSearchCondition(DisplayStatus status, String payYn, LocalDate startDate, LocalDate endDate,
        String keyword, String account, String path, String insuranceCompany) {
    public static InsuredSearchCondition empty() {
        return InsuredSearchCondition.builder()
            .status(null)
            .payYn(null)
            .startDate(null)
            .insuranceCompany(null)
            .endDate(null)
            .keyword(null)
            .path(null)
            .account(null)
            .build();
    }
}