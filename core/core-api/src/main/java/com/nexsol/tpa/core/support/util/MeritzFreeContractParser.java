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
public class MeritzFreeContractParser implements FreeContractParser {

    private final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        // 메리츠 식별: '소유자코드'와 '소재지주소' 헤더가 존재하는지 확인
        return headers.contains("소유자코드") && headers.contains("소재지주소") && headers.contains("보험시작일자")
                && headers.contains("소유자명") && headers.contains("합계");
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
            String businessNo = cellTool.getValueAsString(row, headerMap.get("소유자코드"));
            String companyName = cellTool.getValueAsString(row, headerMap.get("소유자명"));
            String address = cellTool.getValueAsString(row, headerMap.get("소재지주소"));
            String securityNo = cellTool.getValueAsString(row, headerMap.get("증권번호"));

            // 2. 날짜 파싱 (종료일 자동 계산: 시작일 + 1년)
            String startDateStr = cellTool.getValueAsString(row, headerMap.get("보험시작일자"));
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = startDate.plusYears(1); // 요청 사항: +1 year 계산

            // 3. 보험료 파싱 (헤더명 매핑 주의)
            Long total = cellTool.getValueAsLong(row, headerMap.get("합계"));
            Long gov = cellTool.getValueAsLong(row, headerMap.get("국가"));
            Long local = cellTool.getValueAsLong(row, headerMap.get("지자체"));
            Long owner = cellTool.getValueAsLong(row, headerMap.get("개인"));

            result.add(FreeContractUpdateInfo.builder()
                .businessNo(businessNo)
                .companyName(companyName)
                .address(address)
                .insuranceCompany("메리츠")
                .securityNo(securityNo)
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