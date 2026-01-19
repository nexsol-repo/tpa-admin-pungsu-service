package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FreeContractUpdateInfo(String businessNo, String address, String securityNo, // 증권번호
        LocalDate insuranceDate, // 보험시작일
        LocalDate insuranceEndDate, // 보험종료일 (메리츠 등 계산 로직이 적용된 최종 날짜)
        Long totalPremium, // 총보험료
        Long govPremium, // 국가부담
        Long localPremium, // 지자체부담
        Long ownerPremium // 개인부담
) {
}
