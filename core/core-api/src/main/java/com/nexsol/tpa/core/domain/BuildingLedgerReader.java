package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.boon.BuildingLedgerEntity;
import com.nexsol.tpa.storage.db.boon.BuildingLedgerRecapEntity;
import com.nexsol.tpa.storage.db.boon.BuildingLedgerRecapRepository;
import com.nexsol.tpa.storage.db.boon.BuildingLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuildingLedgerReader {

    private final BuildingLedgerRepository buildingLedgerRepository;

    private final BuildingLedgerRecapRepository recapRepository;

    public List<BuildingLedger> findAllByAddress(String sigunguCd, String bjdongCd, String bun, String ji) {
        // 1. DB 조회 (동일 번지에 여러 건물이 있을 수 있음 -> List 반환)
        List<BuildingLedgerEntity> entities = buildingLedgerRepository.findAllByAddressKeys(sigunguCd, bjdongCd, bun,
                ji);

        // 2. Entity -> Domain 변환
        return entities.stream().map(this::mapToDomain).toList();
    }

    public Optional<BuildingLedgerRecap> findRecapByAddress(String sigunguCd, String bjdongCd, String bun, String ji) {
        return recapRepository.findFirstByAddressKeys(sigunguCd, bjdongCd, bun, ji).map(this::mapRecapToDomain);
    }

    private BuildingLedger mapToDomain(BuildingLedgerEntity entity) {
        return BuildingLedger.builder()
            .mgmBldrgstPk(entity.getMgmBldrgstPk())

            .regstrGbCdNm(entity.getRegstrGbCdNm())
            .regstrKindCdNm(entity.getRegstrKindCdNm())
            .buildingName(entity.getBuildingName())
            .dongName(entity.getDongName())
            .platPlc(entity.getPlatPlc())
            .newPlatPlc(entity.getNewPlatPlc())
            .sigunguCd(entity.getSigunguCd())
            .bjdongCd(entity.getBjdongCd())
            .bun(entity.getBun())
            .ji(entity.getJi())
            .platArea(entity.getPlatArea())
            .archArea(entity.getArchArea())
            .totArea(entity.getTotArea())
            .vlRatEstmTotArea(entity.getVlRatEstmTotArea())
            .bcRat(entity.getBcRat())
            .vlRat(entity.getVlRat())
            .atchBldArea(entity.getAtchBldArea())
            .totalDongArea(entity.getTotalDongArea())
            .height(entity.getHeight())
            .groundFloorCnt(entity.getGroundFloorCnt())
            .underGroundFloorCnt(entity.getUnderGroundFloorCnt())
            .strctCdNm(entity.getStrctCdNm())
            .etcStrct(entity.getEtcStrct())
            .mainPurpsCdNm(entity.getMainPurpsCdNm())
            .etcPurps(entity.getEtcPurps())
            .roofCdNm(entity.getRoofCdNm())
            .etcRoof(entity.getEtcRoof())
            .householdCnt(entity.getHouseholdCnt())
            .familyCnt(entity.getFamilyCnt())
            .rideUseElvtCnt(entity.getRideUseElvtCnt())
            .emgenUseElvtCnt(entity.getEmgenUseElvtCnt())
            .useAprDay(entity.getUseAprDay())
            .seismicDesignYn(entity.getSeismicDesignYn())
            .seismicAbility(entity.getSeismicAbility())

            // [추가] 주차
            .indrMechUtcnt(entity.getIndrMechUtcnt())
            .indrMechArea(entity.getIndrMechArea())
            .oudrMechUtcnt(entity.getOudrMechUtcnt())
            .oudrMechArea(entity.getOudrMechArea())
            .indrAutoUtcnt(entity.getIndrAutoUtcnt())
            .indrAutoArea(entity.getIndrAutoArea())
            .oudrAutoUtcnt(entity.getOudrAutoUtcnt())
            .oudrAutoArea(entity.getOudrAutoArea())

            // [추가] 허가 및 인증
            .pmsDay(entity.getPmsDay())
            .stcnsDay(entity.getStcnsDay())
            .pmsnoYear(entity.getPmsnoYear())
            .pmsnoKikCdNm(entity.getPmsnoKikCdNm())
            .pmsnoGbCdNm(entity.getPmsnoGbCdNm())
            .hoCnt(entity.getHoCnt())
            .engrGrade(entity.getEngrGrade())
            .engrRat(entity.getEngrRat())
            .engrEpi(entity.getEngrEpi())
            .gnBldGrade(entity.getGnBldGrade())
            .gnBldCert(entity.getGnBldCert())
            .itgBldGrade(entity.getItgBldGrade())
            .itgBldCert(entity.getItgBldCert())
            .crtnDay(entity.getCrtnDay())
            .build();
    }

    private BuildingLedgerRecap mapRecapToDomain(BuildingLedgerRecapEntity entity) {
        return BuildingLedgerRecap.builder()
            .mgmBldrgstPk(entity.getMgmBldrgstPk())
            .buildingName(entity.getBuildingName())
            .totArea(entity.getTotArea())
            .useAprDay(entity.getUseAprDay())
            .build();
    }

}
