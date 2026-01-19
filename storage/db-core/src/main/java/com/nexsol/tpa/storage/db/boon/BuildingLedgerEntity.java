package com.nexsol.tpa.storage.db.boon;

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

    @Column(name = "regstr_gb_cd_nm")
    private String regstrGbCdNm; // 대장구분코드명 (예: 일반)

    @Column(name = "regstr_kind_cd_nm")
    private String regstrKindCdNm; // 대장종류코드명 (예: 표제부, 전유부)

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

    // 주차 시설 정보
    @Column(name = "indr_mech_utcnt")
    private Integer indrMechUtcnt; // 옥내기계식대수

    @Column(name = "indr_mech_area")
    private Double indrMechArea; // 옥내기계식면적

    @Column(name = "oudr_mech_utcnt")
    private Integer oudrMechUtcnt; // 옥외기계식대수

    @Column(name = "oudr_mech_area")
    private Double oudrMechArea; // 옥외기계식면적

    @Column(name = "indr_auto_utcnt")
    private Integer indrAutoUtcnt; // 옥내자주식대수

    @Column(name = "indr_auto_area")
    private Double indrAutoArea; // 옥내자주식면적

    @Column(name = "oudr_auto_utcnt")
    private Integer oudrAutoUtcnt; // 옥외자주식대수

    @Column(name = "oudr_auto_area")
    private Double oudrAutoArea; // 옥외자주식면적

    // 허가 및 인증 정보
    @Column(name = "pms_day")
    private String pmsDay; // 허가일

    @Column(name = "stcns_day")
    private String stcnsDay; // 착공일

    @Column(name = "pmsno_year")
    private String pmsnoYear; // 허가번호년

    @Column(name = "pmsno_kik_cd_nm")
    private String pmsnoKikCdNm; // 허가기관

    @Column(name = "pmsno_gb_cd_nm")
    private String pmsnoGbCdNm; // 허가번호구분

    @Column(name = "ho_cnt")
    private Integer hoCnt; // 호수

    @Column(name = "engr_grade")
    private String engrGrade; // 에너지효율등급

    @Column(name = "engr_rat")
    private Double engrRat; // 에너지절감율

    @Column(name = "engr_epi")
    private Integer engrEpi; // EPI점수

    @Column(name = "gn_bld_grade")
    private String gnBldGrade; // 친환경건축물등급

    @Column(name = "gn_bld_cert")
    private Integer gnBldCert; // 친환경건축물인증점수

    @Column(name = "itg_bld_grade")
    private String itgBldGrade; // 지능형건축물등급

    @Column(name = "itg_bld_cert")
    private Integer itgBldCert; // 지능형건축물인증점수

    @Column(name = "crtn_day")
    private String crtnDay; // 생성일자

}
