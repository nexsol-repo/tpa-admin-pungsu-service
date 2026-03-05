package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DisplayStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredContract(Integer id, String referIdx, String payYn, // 결제 구분
        String businessNumber, // 사업자번호
        String companyName, // 사업장명
        String address, // 사업장 주소
        String phoneNumber, // 전화번호
        LocalDateTime applicationDate, // 가입일
        String insuranceCompany, // 보험사
        LocalDateTime insuranceStartDate, // 보험기간 시작
        LocalDateTime insuranceEndDate, // 보험기간 종료
        String joinCheck, DisplayStatus displayStatus, // 가입 상태 (가입완료/만기임박/기간만료/임의해지)
        String account, // 제휴사
        String path // 채널

) {

}