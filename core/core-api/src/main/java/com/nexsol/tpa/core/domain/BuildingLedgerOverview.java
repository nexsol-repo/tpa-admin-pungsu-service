package com.nexsol.tpa.core.domain;

import java.util.List;

/**
 * 건축물대장 전체 조회 결과 (총괄 + 표제부 목록)
 */
public record BuildingLedgerOverview(BuildingLedgerRecap recap, // 총괄표제부 (없을 수 있음)
        List<BuildingLedger> ledgers // 일반 건축물대장 목록 (표제부 리스트)
) {
}