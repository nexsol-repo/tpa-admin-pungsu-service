package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record BuildingLedger(String mgmBldrgstPk, // 관리건축물대장PK (고유키)

        String regstrGbCdNm, String regstrKindCdNm, String buildingName, // 건물명 (bld_nm)
        String dongName, // 동명 (dong_nm)

        // 주소 정보
        String platPlc, // 대지위치 (plat_plc)
        String newPlatPlc, // 도로명 대지위치 (new_plat_plc)
        String sigunguCd, // 시군구코드
        String bjdongCd, // 법정동코드
        String bun, // 번
        String ji, // 지

        // 면적/규모 정보
        Double platArea, // 대지면적 (plat_area)
        Double archArea, // 건축면적 (arch_area)
        Double totArea, // 연면적 (tot_area)
        Double vlRatEstmTotArea, // 용적률산정연면적 (vl_rat_estm_tot_area)
        Double bcRat, // 건폐율 (bc_rat)
        Double vlRat, // 용적률 (vl_rat)

        // 높이/층수
        Double height, // 높이 (heit)
        Integer groundFloorCnt, // 지상층수 (grnd_flr_cnt)
        Integer underGroundFloorCnt, // 지하층수 (ugrnd_flr_cnt)

        // 구조/용도
        String strctCdNm, // 구조코드명 (strct_cd_nm)
        String etcStrct, // 기타구조 (etc_strct)
        String mainPurpsCdNm, // 주용도코드명 (main_purps_cd_nm)
        String etcPurps, // 기타용도 (etc_purps)
        String roofCdNm, // 지붕코드명 (roof_cd_nm)
        String etcRoof, // 기타지붕 (etc_roof)

        // 기타 정보
        Integer householdCnt, // 세대수 (hhld_cnt)
        Integer familyCnt, // 가구수 (fmly_cnt)
        Integer rideUseElvtCnt, // 승용승강기수 (ride_use_elvt_cnt)
        Integer emgenUseElvtCnt, // 비상용승강기수 (emgen_use_elvt_cnt)
        String useAprDay, // 사용승인일 (use_apr_day)
        String seismicDesignYn, // 내진설계적용여부 (rserthqk_dsgn_apply_yn)
        String seismicAbility, // 내진능력 (rserthqk_ablty)

        // 주차 시설 정보
        Integer indrMechUtcnt, Double indrMechArea, Integer oudrMechUtcnt, Double oudrMechArea, Integer indrAutoUtcnt,
        Double indrAutoArea, Integer oudrAutoUtcnt, Double oudrAutoArea,

        // 허가 및 인증 정보
        String pmsDay, String stcnsDay, String pmsnoYear, String pmsnoKikCdNm, String pmsnoGbCdNm, Integer hoCnt,
        String engrGrade, Double engrRat, Integer engrEpi, String gnBldGrade, Integer gnBldCert, String itgBldGrade,
        Integer itgBldCert, String crtnDay

) {
}
