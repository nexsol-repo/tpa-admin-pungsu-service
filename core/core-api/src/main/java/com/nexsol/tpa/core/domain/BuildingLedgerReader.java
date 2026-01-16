package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.boon.BuildingLedgerEntity;
import com.nexsol.tpa.storage.db.boon.BuildingLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildingLedgerReader {

    private final BuildingLedgerRepository buildingLedgerRepository;

    public List<BuildingLedger> findAllByAddress(String sigunguCd, String bjdongCd, String bun, String ji) {
        // 1. DB 조회 (동일 번지에 여러 건물이 있을 수 있음 -> List 반환)
        List<BuildingLedgerEntity> entities = buildingLedgerRepository.findAllByAddressKeys(sigunguCd, bjdongCd, bun,
                ji);

        // 2. Entity -> Domain 변환
        return entities.stream().map(this::mapToDomain).toList();
    }

    private BuildingLedger mapToDomain(BuildingLedgerEntity entity) {
        return BuildingLedger.builder()
            .mgmBldrgstPk(entity.getMgmBldrgstPk())
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
            .build();
    }

}
