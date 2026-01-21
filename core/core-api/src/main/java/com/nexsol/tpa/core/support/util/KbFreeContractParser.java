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
public class KbFreeContractParser implements FreeContractParser {

    private final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        return headers.contains("합계 : 총보험료") && headers.contains("사업자번호") && headers.contains("사업자명")
                && headers.contains("합계 : 총보험료");
    }

    @Override
    public List<FreeContractUpdateInfo> parse(Sheet sheet, Map<String, Integer> headerMap) {
        List<FreeContractUpdateInfo> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            // 1. 식별자 파싱
            String businessNo = cellTool.getValueAsString(row, headerMap.get("사업자번호"));
            String companyName = cellTool.getValueAsString(row, headerMap.get("사업자명"));
            String securityNo = cellTool.getValueAsString(row, headerMap.get("증권번호"));

            // KB 양식에는 명시적인 주소 컬럼이 없는 경우가 많아, 소재지나 주소 키워드를 찾음
            // (없을 경우 빈 문자열 처리하여 Writer에서 사업자번호로만 찾도록 유도하거나, 양식 수정 필요)
            String address = "";
            if (headerMap.containsKey("소재지")) {
                address = cellTool.getValueAsString(row, headerMap.get("소재지"));
            }
            else if (headerMap.containsKey("주소")) {
                address = cellTool.getValueAsString(row, headerMap.get("주소"));
            }

            // 2. 날짜 파싱
            String startDateStr = cellTool.getValueAsString(row, headerMap.get("보험시작일"));
            String endDateStr = cellTool.getValueAsString(row, headerMap.get("보험종기일"));

            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            // 3. 보험료 파싱 (KB 특유의 헤더명)
            Long total = cellTool.getValueAsLong(row, headerMap.get("합계 : 총보험료"));
            Long gov = cellTool.getValueAsLong(row, headerMap.get("합계 : 보험료-정부"));
            Long local = cellTool.getValueAsLong(row, headerMap.get("합계 : 보험료-지자체"));
            Long owner = cellTool.getValueAsLong(row, headerMap.get("합계 : 보험료-계약자"));

            result.add(FreeContractUpdateInfo.builder()
                .businessNo(businessNo)
                .companyName(companyName)
                .address(address)
                .securityNo(securityNo)
                .insuranceCompany("KB손해보험")
                .insuranceDate(startDate)
                .insuranceEndDate(endDate)
                .totalPremium(total)
                .govPremium(gov)
                .localPremium(local)
                .ownerPremium(owner)
                .build());
        }
        return result;

    }

}