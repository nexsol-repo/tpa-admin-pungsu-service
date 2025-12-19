package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "total_formmembers")
@Getter
public class TotalFormMemberEntity {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // --- 식별 정보 ---
    @Column(name = "refer_idx", nullable = false)
    private String referIdx; // 차세대 키값

    // --- 고객/사업자 정보 (검색 대상) ---
    @Column(name = "businessnumber")
    private String businessNumber; // 사업자번호

    @Column(name = "company")
    private String companyName; // 상호명

    @Column(name = "phone")
    private String phoneNumber; // 휴대폰번호

    @Column(name = "name")
    private String applicantName; // 신청자명

    // --- 주소 정보 (리스트 노출) ---
    @Column(name = "address")
    private String address;

    // --- 보험 계약 정보 (정렬/필터 대상) ---
    @Column(name = "ins_com")
    private String insuranceCompany; // 보험사

    @Column(name = "ins_sdate")
    private LocalDateTime insuranceStartDate; // 보험 시작일

    @Column(name = "ins_edate")
    private LocalDateTime insuranceEndDate; // 보험 종료일

    // --- 상태 정보 (필터 대상) ---

    @Column(name = "join_ck")
    private String joinCheck; // 계약 진행상태 (W, N, R, Y, D, E, F, X)

    @Column(name = "pay_yn")
    private String payYn; // 결제 여부 (Y/N)

    @Column(name = "pay_method")
    private String payMethod; // 결제 수단

}
