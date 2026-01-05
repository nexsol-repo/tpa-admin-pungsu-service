package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredSubscriptionInfo(String joinCk, // 진행 상태
        LocalDateTime insuranceStartDate, LocalDateTime insuranceEndDate, String insuranceCompany,
        String insuranceNumber, String payYn, // 결제 여부
        String account, // 제휴사
        String path, // 채널

        // 가입 금액 (Coverage)
        Long insuranceCostBld, Long insuranceCostFcl, Long insuranceCostMach, Long insuranceCostInven,
        Long insuranceCostShopSign, Long insuranceCostDeductible,

        // 보험료 (Premium)
        Long totalInsuranceCost, Long totalInsuranceMyCost, Long totalGovernmentCost, Long totalLocalGovernmentCost,
        boolean isRenewalTarget

) {

    public static boolean calculateRenewalTarget(LocalDateTime endDate, LocalDateTime now) {
        if (endDate == null)
            return false;
        LocalDateTime oneMonthLater = now.plusMonths(1);
        return endDate.isAfter(now) && endDate.isBefore(oneMonthLater);
    }
}
