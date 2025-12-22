package com.nexsol.tpa.core.domain;

import java.time.LocalDateTime;

public record InsuredContractInfo(
        String joinCk,
         boolean isRenewalTarget, // 갱신대상 여부
        LocalDateTime insuranceStartDate, // 보험기간 시작
        LocalDateTime insuranceEndDate,
        String insuranceCompany,
        String insuranceNumber,
        // 건물가입금액
        Long insuranceCostBld,
        // 시설/집기 가입금액
        Long insuranceCostFcl,
        // 기계
        Long insuranceCostMach,
        // 재고자산
        Long insuranceCostInven,
        // 야외간판
        Long insuranceCostShopSign,
        //개인 부담금
        Long insuranceCostDeductible,
        Long totalInsuranceCost,
        Long totalInsuranceMyCost,
        Long totalGovernmentCost,
        Long totalLocalGovernmentCost



        ) {


    public boolean isRenewalTarget(LocalDateTime now) {
        if (insuranceEndDate == null) {
            return false;
        }
        LocalDateTime oneMonthLater = now.plusMonths(1);

        // 현재보다는 미래여야 하고(종료 안됨), 1달 뒤보다는 과거여야 함(임박)
        return insuranceEndDate.isAfter(now) && insuranceEndDate.isBefore(oneMonthLater);
    }
}
