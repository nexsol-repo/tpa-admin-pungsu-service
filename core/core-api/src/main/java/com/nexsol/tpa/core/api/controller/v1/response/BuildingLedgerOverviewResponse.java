package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.BuildingLedgerOverview;
import lombok.Builder;

import java.util.List;

@Builder
public record BuildingLedgerOverviewResponse(BuildingLedgerRecapResponse recap, // 총괄표제부
                                                                                // (없으면
                                                                                // null)
        List<BuildingLedgerResponse> ledgers // 표제부 목록
) {
    public static BuildingLedgerOverviewResponse of(BuildingLedgerOverview overview) {
        return BuildingLedgerOverviewResponse.builder()
            .recap(BuildingLedgerRecapResponse.of(overview.recap()))
            .ledgers(overview.ledgers().stream().map(BuildingLedgerResponse::of).toList())
            .build();
    }
}