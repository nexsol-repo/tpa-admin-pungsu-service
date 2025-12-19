package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InsuredSearchCondition(String status, String payYn, LocalDate startDate, LocalDate endDate,
        String keyword) {
    public static InsuredSearchCondition empty() {
        return InsuredSearchCondition.builder()
            .status(null)
            .payYn(null)
            .startDate(null)
            .endDate(null)
            .keyword(null)
            .build();
    }
}
