package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuredInfo(String companyName, String name, String businessNumber, String birthDate, String email,
        String phoneNumber, String address,
        // 업종
        String category,
        // 임차여부
        String tenant, String structure, String prctrNo, String pnu, String groundFloorYn, int groundFloor,
        int underGroundFloor, String subFloor, String endSubFloor

) {
}
