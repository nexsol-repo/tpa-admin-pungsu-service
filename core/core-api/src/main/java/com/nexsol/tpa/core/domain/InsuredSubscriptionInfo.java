package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredSubscriptionInfo(String joinCheck, // 진행 상태
        LocalDateTime insuranceStartDate, LocalDateTime insuranceEndDate, String insuranceCompany,
        String insuranceNumber, String payYn, // 결제 여부
        String account, String path, LocalDateTime applicationDate,

        // 가입 금액 (Coverage)
        Long insuranceCostBld, Long insuranceCostFcl, Long insuranceCostMach, Long insuranceCostInven,
        Long insuranceCostShopSign, Long insuranceCostDeductible,

        // 보험료 (Premium)
        Long totalInsuranceCost, Long totalInsuranceMyCost, Long totalGovernmentCost, Long totalLocalGovernmentCost

) {

}
