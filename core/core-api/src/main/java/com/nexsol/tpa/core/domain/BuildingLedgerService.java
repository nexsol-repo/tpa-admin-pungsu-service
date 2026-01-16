package com.nexsol.tpa.core.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingLedgerService {

    private final BuildingLedgerReader buildingLedgerReader;

    /**
     * 주소 키값 4종으로 건축물대장 목록을 조회합니다. 하나의 대지(번지)에 여러 동의 건물이 있을 수 있으므로 List로 반환합니다.
     */
    @Transactional
    public List<BuildingLedger> searchBuildingLedgers(String sigunguCd, String bjdongCd, String bun, String ji) {
        // 비즈니스 로직: 유효성 검증 등이 필요하면 여기에 추가

        // 상세 구현(DB 조회 및 매핑)은 Reader에게 위임
        return buildingLedgerReader.findAllByAddress(sigunguCd, bjdongCd, bun, ji);
    }

}
