package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ContractExcelData(String account, // 거래처
        String insuranceCompany, // 보험사
        String status, // 상태
        String payYn, // 유무료 여부 (Y/N)
        String businessNumber, // 사업자번호
        String biztype, // 소상인 구분
        String category, // 업종
        String pnu,

        String companyName, // 사업장명
        String ceoName, // 대표자명
        String zipCode, // 우편번호
        String address, // 주소
        String phoneNumber, // 전화번호
        LocalDateTime applicationDate, // 신청일시
        String path, // 가입경로
        LocalDate insuranceStartDate, // 보험시기
        LocalDate insuranceEndDate, // 보험종기

        // 금액 정보 (계산된 결과값)
        long totalPremium, // 총보험료
        long deductibleAmount, // 자기부담금
        long localShareAmount, // 지자체지원
        long stateShareAmount // 정부지원
) {
}