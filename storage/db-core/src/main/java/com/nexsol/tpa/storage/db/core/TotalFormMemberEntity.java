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

    @Column(name = "ins_number")
    private String insuranceNumber;

    @Column(name = "insured_nm")
    private String name;

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

    @Column(name = "bizcategory")
    private String bizCategory;

    @Column(name = "structure")
    private String structure;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "floor")
    private String floor;

    @Column(name = "subfloor")
    private String subFloor;

    @Column(name = "endsubfloor")
    private String endSubFloor;

    @Column(name = "prctr_no")
    private String prctrNo;

    @Column(name = "account")
    private String account;

    @Column(name = "path")
    private String path;

    @Column(name = "pnu")
    private String pnu;

    @Embedded
    private CoverageAmount coverage;

    @Embedded
    private PremiumAmount premium;

    /**
     * 가입자 기본 인적 사항 변경
     */
    public void applyInsuredBasic(String companyName, String name, String businessNumber, String phoneNumber) {
        this.companyName = companyName;
        this.name = name; // 계약자=피보험자 규칙 강제
        this.businessNumber = businessNumber;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 사업장 정보 변경
     */
    public void applyLocationInfo(String address, String tenant, String category, String structure, String floor,
            String pnu, String prctrNo) {
        this.address = address;
        this.bizCategory = category;
        this.tenant = tenant;
        this.structure = structure;
        this.floor = floor;
        this.prctrNo = prctrNo;
        this.pnu = pnu;
    }

    public void applyContractStatus(String joinCheck, LocalDateTime insuranceStartDate, LocalDateTime insuranceEndDate,
            String insuranceNumber) {
        this.joinCheck = joinCheck;
        this.insuranceStartDate = insuranceStartDate;
        this.insuranceEndDate = insuranceEndDate;
        this.insuranceNumber = insuranceNumber;
    }

    /**
     * 가입금액 전체 수정 (객체 단위로 한 번에)
     */
    public void applyCoverage(CoverageAmount coverage) {
        this.coverage = coverage;
    }

    /**
     * 보험료 전체 수정 (객체 단위로 한 번에)
     */
    public void applyPremium(PremiumAmount premium) {
        this.premium = premium;
    }

}
