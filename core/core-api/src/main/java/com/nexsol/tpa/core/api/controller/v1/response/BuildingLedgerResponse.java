package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.BuildingLedger;
import lombok.Builder;

@Builder
public record BuildingLedgerResponse(String mgmBldrgstPk, // 관리건축물대장PK

        String regstrGbCdNm, String regstrKindCdNm, String buildingName, // 건물명
        String dongName, // 동명
        String platPlc, // 대지위치
        String newPlatPlc, // 도로명 대지위치
        String sigunguCd, String bjdongCd, String bun, String ji, Double platArea, // 대지면적
        Double archArea, // 건축면적
        Double totArea, // 연면적
        Double vlRatEstmTotArea, // 용적률산정연면적
        Double bcRat, // 건폐율
        Double vlRat, // 용적률
        Double atchBldArea, Double totalDongArea, Double height, // 높이
        Integer groundFloorCnt, // 지상층수
        Integer underGroundFloorCnt, // 지하층수
        String strctCdNm, // 구조코드명
        String etcStrct, // 기타구조
        String mainPurpsCdNm, // 주용도코드명
        String etcPurps, // 기타용도
        String roofCdNm, // 지붕코드명
        String etcRoof, // 기타지붕
        Integer householdCnt, // 세대수
        Integer familyCnt, // 가구수
        Integer rideUseElvtCnt, // 승용승강기수
        Integer emgenUseElvtCnt, // 비상용승강기수
        String useAprDay, // 사용승인일
        String seismicDesignYn, // 내진설계적용여부
        String seismicAbility, // 내진능력

        // [추가] 주차 시설
        Integer indrMechUtcnt, Double indrMechArea, Integer oudrMechUtcnt, Double oudrMechArea, Integer indrAutoUtcnt,
        Double indrAutoArea, Integer oudrAutoUtcnt, Double oudrAutoArea,

        // [추가] 허가 및 인증
        String pmsDay, String stcnsDay, String pmsnoYear, String pmsnoKikCdNm, String pmsnoGbCdNm, Integer hoCnt,
        String engrGrade, Double engrRat, Integer engrEpi, String gnBldGrade, Integer gnBldCert, String itgBldGrade,
        Integer itgBldCert, String crtnDay) {
    public static BuildingLedgerResponse of(BuildingLedger ledger) {
        return BuildingLedgerResponse.builder()
            .mgmBldrgstPk(ledger.mgmBldrgstPk())
            .regstrGbCdNm(ledger.regstrGbCdNm())
            .regstrKindCdNm(ledger.regstrKindCdNm())
            .buildingName(ledger.buildingName())
            .dongName(ledger.dongName())
            .platPlc(ledger.platPlc())
            .newPlatPlc(ledger.newPlatPlc())
            .sigunguCd(ledger.sigunguCd())
            .bjdongCd(ledger.bjdongCd())
            .bun(ledger.bun())
            .ji(ledger.ji())
            .platArea(ledger.platArea())
            .archArea(ledger.archArea())
            .totArea(ledger.totArea())
            .vlRatEstmTotArea(ledger.vlRatEstmTotArea())
            .bcRat(ledger.bcRat())
            .vlRat(ledger.vlRat())
            .atchBldArea(ledger.atchBldArea())
            .totalDongArea(ledger.totalDongArea())
            .height(ledger.height())
            .groundFloorCnt(ledger.groundFloorCnt())
            .underGroundFloorCnt(ledger.underGroundFloorCnt())
            .strctCdNm(ledger.strctCdNm())
            .etcStrct(ledger.etcStrct())
            .mainPurpsCdNm(ledger.mainPurpsCdNm())
            .etcPurps(ledger.etcPurps())
            .roofCdNm(ledger.roofCdNm())
            .etcRoof(ledger.etcRoof())
            .householdCnt(ledger.householdCnt())
            .familyCnt(ledger.familyCnt())
            .rideUseElvtCnt(ledger.rideUseElvtCnt())
            .emgenUseElvtCnt(ledger.emgenUseElvtCnt())
            .useAprDay(ledger.useAprDay())
            .seismicDesignYn(ledger.seismicDesignYn())
            .seismicAbility(ledger.seismicAbility())

            .indrMechUtcnt(ledger.indrMechUtcnt())
            .indrMechArea(ledger.indrMechArea())
            .oudrMechUtcnt(ledger.oudrMechUtcnt())
            .oudrMechArea(ledger.oudrMechArea())
            .indrAutoUtcnt(ledger.indrAutoUtcnt())
            .indrAutoArea(ledger.indrAutoArea())
            .oudrAutoUtcnt(ledger.oudrAutoUtcnt())
            .oudrAutoArea(ledger.oudrAutoArea())
            .pmsDay(ledger.pmsDay())
            .stcnsDay(ledger.stcnsDay())
            .pmsnoYear(ledger.pmsnoYear())
            .pmsnoKikCdNm(ledger.pmsnoKikCdNm())
            .pmsnoGbCdNm(ledger.pmsnoGbCdNm())
            .hoCnt(ledger.hoCnt())
            .engrGrade(ledger.engrGrade())
            .engrRat(ledger.engrRat())
            .engrEpi(ledger.engrEpi())
            .gnBldGrade(ledger.gnBldGrade())
            .gnBldCert(ledger.gnBldCert())
            .itgBldGrade(ledger.itgBldGrade())
            .itgBldCert(ledger.itgBldCert())
            .crtnDay(ledger.crtnDay())
            .build();
    }
}