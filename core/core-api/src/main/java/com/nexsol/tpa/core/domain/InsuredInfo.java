package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuredInfo(
        String companyName,
        String businessNumber,
        String birthDate,
        String email,
        String phoneNumber,
        String address,
        String category,
        String tenant,
        String floor,
        String subFloor,
        String endSubFloor,
        String underground,
        String structure,
        String prctrNo



) {
}
