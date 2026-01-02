package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredContract(Integer id, String payYn, // 결제 구분
        String businessNumber, // 사업자번호
        String companyName, // 사업장명
        String address, // 사업장 주소
        String phoneNumber, // 전화번호
        LocalDateTime applicationDate, // 가입일
        String insuranceCompany, // 보험사
        LocalDateTime insuranceStartDate, // 보험기간 시작
        LocalDateTime insuranceEndDate, // 보험기간 종료
        String joinCk, boolean isRenewalTarget, // 갱신대상 여부,
        String account, // 제휴사
        String path // 채널,

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