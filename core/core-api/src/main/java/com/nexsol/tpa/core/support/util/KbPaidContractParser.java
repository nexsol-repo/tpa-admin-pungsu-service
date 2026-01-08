package com.nexsol.tpa.core.support.util;

import com.nexsol.tpa.storage.db.core.PremiumAmount;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class KbPaidContractParser extends AbstractPaidContractParser {

    // KB 양식 헤더 정의
    private static final String H_POLICY_NO = "증권번호";

    private static final String H_TOTAL = "합계 : 총보험료";

    private static final String H_PERSONAL = "합계 : 보험료-계약자"; // 주민부담

    private static final String H_CITY = "합계 : 보험료-지자체";

    private static final String H_STATE = "합계 : 보험료-정부";

    public KbPaidContractParser(ExcelCellTool cellTool) {
        super(cellTool);
    }

    @Override
    public boolean supports(Set<String> headers) {
        // KB만의 고유한 헤더가 있는지 확인
        return headers.contains(H_TOTAL) && headers.contains("청약번호");
    }

    @Override
    protected PaidConversionTarget mapRow(Row row, Map<String, Integer> map) {
        String policyNo = getString(row, map, H_POLICY_NO);
        if (policyNo == null || policyNo.isBlank())
            return null;

        return PaidConversionTarget.builder()
            .policyNumber(policyNo)
            .premium(PremiumAmount.builder()
                .totalInsuranceCost(getLong(row, map, H_TOTAL))
                .totalInsuranceMyCost(getLong(row, map, H_PERSONAL))
                .totalLocalGovernmentCost(getLong(row, map, H_CITY))
                .totalGovernmentCost(getLong(row, map, H_STATE))
                .build())
            .build();
    }

}
