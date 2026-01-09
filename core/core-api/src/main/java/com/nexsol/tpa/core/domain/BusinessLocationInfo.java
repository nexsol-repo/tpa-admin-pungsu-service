package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record BusinessLocationInfo(String companyName, String zipCode, String address, String category, // 업종
        String biztype, // 소상인 구분
        String tenant, // 임차여부
        String structure, // 건물구조
        String pnu, String prctrNo, // 질권번호
        String groundFloorCd, // 지하소재여부 (Y/N 또는 코드)
        String groundFloorYn, // 지하층/1층 여부 (Y: 지하/1층, N: 2층이상),
        String mainStrctType, String roofStrctType, int groundFloor, int underGroundFloor, String subFloor,
        String endSubFloor, String tmYn) {
}
