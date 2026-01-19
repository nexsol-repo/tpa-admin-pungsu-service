package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record BuildingLedgerRecap(String mgmBldrgstPk, String buildingName, Double totArea, String useAprDay) {
}