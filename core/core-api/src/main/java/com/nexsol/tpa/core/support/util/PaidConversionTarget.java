package com.nexsol.tpa.core.support.util;

import com.nexsol.tpa.storage.db.core.PremiumAmount;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PaidConversionTarget(String policyNumber, PremiumAmount premium, // 보험료 (총액,
                                                                               // 자부담, 국비,
                                                                               // 지방비)

        // 추가된 업데이트 대상 필드 (보장 금액)
        Long buildingAmount, // 건물
        Long machineryAmount, // 기계
        Long facilityAmount, // 시설
        Long fixturesAmount, // 비품/집기
        Long inventoryAmount, // 재고자산

        Long deductibleAmount, // 자기부담금

        LocalDate insuranceStartDate, // 보험시기
        LocalDate insuranceEndDate) // 보험종료
{

}
