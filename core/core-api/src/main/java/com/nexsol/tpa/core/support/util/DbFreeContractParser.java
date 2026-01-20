package com.nexsol.tpa.core.support.util;

import com.nexsol.tpa.core.domain.FreeContractParser;
import com.nexsol.tpa.core.domain.FreeContractUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DbFreeContractParser implements FreeContractParser {

    private final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        return headers.contains("피보험자사업자번호") && headers.contains("소재지신주소") && headers.contains("보험일자");
    }

    @Override
    public List<FreeContractUpdateInfo> parse(Sheet sheet, Map<String, Integer> headerMap) {
        List<FreeContractUpdateInfo> result = new ArrayList<>();

        // 날짜 포맷 (예: 2025-03-20 23:59)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            // 1. 식별자 파싱
            String businessNo = cellTool.getValueAsString(row, headerMap.get("피보험자사업자번호"));
            String address = cellTool.getValueAsString(row, headerMap.get("소재지신주소"));
            String securityNo = cellTool.getValueAsString(row, headerMap.get("증권번호"));

            // 2. 날짜 파싱 (특수 포맷: "시작일시~종료일시")
            String dateRange = cellTool.getValueAsString(row, headerMap.get("보험일자"));
            LocalDate startDate = null;
            LocalDate endDate = null;

            if (StringUtils.hasText(dateRange) && dateRange.contains("~")) {
                String[] dates = dateRange.split("~");
                if (dates.length == 2) {
                    // 시간까지 파싱 후 날짜로 변환
                    startDate = LocalDateTime.parse(dates[0].trim(), formatter).toLocalDate();
                    endDate = LocalDateTime.parse(dates[1].trim(), formatter).toLocalDate();
                }
            }

            // 3. 보험료 파싱
            Long total = cellTool.getValueAsLong(row, headerMap.get("총보험료"));
            Long gov = cellTool.getValueAsLong(row, headerMap.get("국고"));
            Long local = cellTool.getValueAsLong(row, headerMap.get("지자체"));
            Long owner = cellTool.getValueAsLong(row, headerMap.get("주민보험료"));

            if (startDate != null && endDate != null) {
                result.add(FreeContractUpdateInfo.builder()
                    .businessNo(businessNo)
                    .address(address)
                    .insuranceCompany("DB손해보험")
                    .securityNo(securityNo)
                    .insuranceDate(startDate)
                    .insuranceEndDate(endDate)
                    .totalPremium(total)
                    .govPremium(gov)
                    .localPremium(local)
                    .ownerPremium(owner)
                    .build());
            }
        }
        return result;

    }

}