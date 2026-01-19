package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.BuildingLedgerRecap;
import lombok.Builder;

@Builder
public record BuildingLedgerRecapResponse(String mgmBldrgstPk, String buildingName, Double totArea, String useAprDay) {
    public static BuildingLedgerRecapResponse of(BuildingLedgerRecap recap) {
        if (recap == null) {
            return null;
        }
        return BuildingLedgerRecapResponse.builder()
            .mgmBldrgstPk(recap.mgmBldrgstPk())
            .buildingName(recap.buildingName())
            .totArea(recap.totArea())
            .useAprDay(recap.useAprDay())
            .build();
    }
}