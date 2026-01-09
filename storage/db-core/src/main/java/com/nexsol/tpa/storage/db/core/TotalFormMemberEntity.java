package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.Builder;
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

    @Column(name = "contract_name")
    private String contractName;

    @Column(name = "contract_business_number")
    private String contractBusinessNumber;

    @Column(name = "contract_address")
    private String contractAddress;

    @Column(name = "insured_email")
    private String email;

    @Column(name = "insured_rr_no")
    private String birthDate;

    @Column(name = "phone")
    private String phoneNumber; // 휴대폰번호

    // --- 주소 정보 (리스트 노출) ---
    @Column(name = "address")
    private String address;

    @Column(name = "zonecode")
    private String zipCode;

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

    @Column(name = "bizcategory")
    private String bizCategory;

    @Column(name = "biztype")
    private String biztype;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "ground_floor_cd")
    private String groundFloorCd;

    @Column(name = "ground_floor_yn")
    private String groundFloorYn;

    @Column(name = "grnd_flr_cnt")
    private int groundFloor;

    @Column(name = "ugrnd_flr_cnt")
    private int underGroundFloor;

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

    @Column(name = "tm_yn")
    private String tmYn;

    @Column(name = "entry_div")
    private String entryDiv;

    @Column(name = "main_strct_type")
    private String mainStrctType;

    @Column(name = "roof_strct_type")
    private String roofStrctType;

    @Column(name = "bld_grade")
    private Integer bldGrade;

    @Column(name = "citycode")
    private String cityCode; // 지역 코드

    @Column(name = "city_text_1")
    private String cityText1; // 시도

    @Column(name = "city_text_2")
    private String cityText2; // 시군구

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Embedded
    private CoverageAmount coverage;

    @Embedded
    private PremiumAmount premium;

    /**
     * 가입자 기본 인적 사항 변경
     */
    public void applyInsuredBasic(String name, String businessNumber, String phoneNumber, String email,
            String birthDate) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;

    }

    public void applyContractInfo(String contractName, String contractBusinessNumber, String contractAddress) {
        this.contractName = contractName;
        this.contractBusinessNumber = contractBusinessNumber;
        this.contractAddress = contractAddress;
    }

    /**
     * 사업장 정보 변경
     */

    @Builder
    public void applyLocationInfo(String companyName, String zipCode, String address, String category, String biztype,
            String tenant, String pnu, String prctrNo, String groundFloorCd, int groundFloor, int underGroundFloor,
            String subFloor, String endSubFloor, String tmYn, String groundFloorYn, String mainStrctType,
            String roofStrctType) {
        this.companyName = companyName;
        this.zipCode = zipCode;
        this.address = address;
        this.bizCategory = category;
        this.biztype = biztype;
        this.tenant = tenant;
        this.groundFloorCd = groundFloorCd;
        this.groundFloor = groundFloor;
        this.underGroundFloor = underGroundFloor;
        this.subFloor = subFloor;
        this.endSubFloor = endSubFloor;
        this.prctrNo = prctrNo;
        this.pnu = pnu;
        this.tmYn = tmYn;
        this.groundFloorYn = groundFloorYn;
        this.mainStrctType = mainStrctType;
        this.roofStrctType = roofStrctType;
    }

    public void applyContractStatus(String joinCheck, LocalDateTime createdAt, LocalDateTime insuranceStartDate,
            LocalDateTime insuranceEndDate, String insuranceNumber, String payYn, String insuranceCompany) {
        this.joinCheck = joinCheck;
        this.insuranceCompany = insuranceCompany;
        this.createdAt = createdAt;
        this.insuranceStartDate = insuranceStartDate;
        this.insuranceEndDate = insuranceEndDate;
        this.insuranceNumber = insuranceNumber;
        this.payYn = payYn;
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

    public void assignReferIdx(String referIdx) {
        if (this.referIdx != null) {
            throw new IllegalStateException("이미 식별자가 존재합니다.");
        }
        this.referIdx = referIdx;
    }

    // 채널 정보 할당 (ContractInfo에 포함된 경우)
    public void applyChannelInfo(String account, String path) {
        this.account = account;
        this.path = path;
    }

    public void applyEntryDiv(String entryDiv) {
        this.entryDiv = entryDiv;
    }

    public void applyBuildingStructure(String mainStrctType, String roofStrctType, Integer bldGrade) {
        this.mainStrctType = mainStrctType;
        this.roofStrctType = roofStrctType;
        this.bldGrade = bldGrade;
    }

    public void applyCityInfo(String cityCode, String cityText1, String cityText2) {
        this.cityCode = cityCode;
        this.cityText1 = cityText1;
        this.cityText2 = cityText2;
    }

}
