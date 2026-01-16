package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_br_title_info")
@Getter
@NoArgsConstructor
public class BuildingLedgerEntity {

    // 관리건축물대장PK
    @Id
    @Column(name = "mgm_bldrgst_pk")
    private String mgmBldrgstPk;

    // --- 주소 및 식별 정보 ---
    @Column(name = "sigungu_cd")
    private String sigunguCd;

    @Column(name = "bjdong_cd")
    private String bjdongCd;

    @Column(name = "bun")
    private String bun;

    @Column(name = "ji")
    private String ji;

    @Column(name = "plat_gb_cd")
    private String platGbCd; // 대지구분코드

    @Column(name = "plat_plc")
    private String platPlc; // 대지위치 (지번 주소)

    @Column(name = "new_plat_plc")
    private String newPlatPlc; // 도로명 대지위치

    @Column(name = "bld_nm")
    private String buildingName; // 건물명

    @Column(name = "dong_nm")
    private String dongName; // 동명

    // --- 면적 정보 ---
    @Column(name = "plat_area")
    private Double platArea; // 대지면적

    @Column(name = "arch_area")
    private Double archArea; // 건축면적

    @Column(name = "tot_area")
    private Double totArea; // 연면적

    @Column(name = "vl_rat_estm_tot_area")
    private Double vlRatEstmTotArea; // 용적률산정연면적

    @Column(name = "bc_rat")
    private Double bcRat; // 건폐율

    @Column(name = "vl_rat")
    private Double vlRat; // 용적률

    // --- 높이 및 층수 ---
    @Column(name = "heit")
    private Double height; // 높이

    @Column(name = "grnd_flr_cnt")
    private Integer groundFloorCnt; // 지상층수

    @Column(name = "ugrnd_flr_cnt")
    private Integer underGroundFloorCnt; // 지하층수

    // --- 구조 및 용도 ---
    @Column(name = "strct_cd_nm")
    private String strctCdNm; // 구조코드명

    @Column(name = "etc_strct")
    private String etcStrct; // 기타구조

    @Column(name = "main_purps_cd_nm")
    private String mainPurpsCdNm; // 주용도코드명

    @Column(name = "etc_purps")
    private String etcPurps; // 기타용도

    @Column(name = "roof_cd_nm")
    private String roofCdNm; // 지붕코드명

    @Column(name = "etc_roof")
    private String etcRoof; // 기타지붕

    // --- 기타 정보 ---
    @Column(name = "hhld_cnt")
    private Integer householdCnt; // 세대수

    @Column(name = "fmly_cnt")
    private Integer familyCnt; // 가구수

    @Column(name = "ride_use_elvt_cnt")
    private Integer rideUseElvtCnt; // 승용승강기수

    @Column(name = "emgen_use_elvt_cnt")
    private Integer emgenUseElvtCnt; // 비상용승강기수

    @Column(name = "use_apr_day")
    private String useAprDay; // 사용승인일

    @Column(name = "rserthqk_dsgn_apply_yn")
    private String seismicDesignYn; // 내진설계적용여부

    @Column(name = "rserthqk_ablty")
    private String seismicAbility; // 내진능력

}
