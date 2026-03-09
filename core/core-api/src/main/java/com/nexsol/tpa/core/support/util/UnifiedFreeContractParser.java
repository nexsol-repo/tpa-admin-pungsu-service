package com.nexsol.tpa.core.support.util;

import com.nexsol.tpa.core.domain.FreeContractParser;
import com.nexsol.tpa.core.domain.FreeContractUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UnifiedFreeContractParser implements FreeContractParser {

    private final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        return headers.contains("보험사명") && headers.contains("사업자번호") && headers.contains("상호명")
                && headers.contains("증권번호") && headers.contains("보험시작일") && headers.contains("보험종기일");
    }

    @Override
    public List<FreeContractUpdateInfo> parse(Sheet sheet, Map<String, Integer> headerMap) {
        List<FreeContractUpdateInfo> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            String insuranceCompany = cellTool.getValueAsString(row, headerMap.get("보험사명"));
            String businessNo = cellTool.getValueAsString(row, headerMap.get("사업자번호"));
            String companyName = cellTool.getValueAsString(row, headerMap.get("상호명"));
            String address = headerMap.containsKey("주소") ? cellTool.getValueAsString(row, headerMap.get("주소")) : "";
            String securityNo = cellTool.getValueAsString(row, headerMap.get("증권번호"));

            String startDateStr = cellTool.getValueAsString(row, headerMap.get("보험시작일"));
            String endDateStr = cellTool.getValueAsString(row, headerMap.get("보험종기일"));

            if (startDateStr.isBlank() || endDateStr.isBlank())
                continue;

            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            Long total = cellTool.getValueAsLong(row, headerMap.get("총보험료"));
            Long gov = cellTool.getValueAsLong(row, headerMap.get("국가부담"));
            Long local = cellTool.getValueAsLong(row, headerMap.get("지자체부담"));
            Long owner = cellTool.getValueAsLong(row, headerMap.get("개인부담"));

            String errorReason = headerMap.containsKey("오류사유") ? cellTool.getValueAsString(row, headerMap.get("오류사유"))
                    : "";

            result.add(FreeContractUpdateInfo.builder()
                .businessNo(businessNo)
                .companyName(companyName)
                .address(address)
                .insuranceCompany(insuranceCompany)
                .securityNo(securityNo)
                .insuranceDate(startDate)
                .insuranceEndDate(endDate)
                .totalPremium(total)
                .govPremium(gov)
                .localPremium(local)
                .ownerPremium(owner)
                .errorReason(errorReason)
                .build());
        }
        return result;
    }

}