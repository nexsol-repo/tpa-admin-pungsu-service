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
public class SamsungFreeContractParser implements FreeContractParser {

    private final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        // 삼성 양식 식별 키워드 (헤더에 '피보험자 사업자명(상호명)' 등이 포함되는지 확인)
        return headers.contains("피보험자 사업자명(상호명)") && headers.contains("전체보험료");
    }

    @Override
    public List<FreeContractUpdateInfo> parse(Sheet sheet, Map<String, Integer> headerMap) {
        List<FreeContractUpdateInfo> result = new ArrayList<>();

        // 날짜 포맷 (yyyyMMdd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            // 엑셀 데이터 추출
            String businessNo = cellTool.getValueAsString(row, headerMap.get("사업자번호"));
            String address = cellTool.getValueAsString(row, headerMap.get("기본주소"));
            String securityNo = cellTool.getValueAsString(row, headerMap.get("증권번호"));

            // 날짜 파싱 (예: 20251121 -> LocalDate)
            String startDateStr = cellTool.getValueAsString(row, headerMap.get("보험시작일"));
            String endDateStr = cellTool.getValueAsString(row, headerMap.get("보험종기일"));

            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            // 금액 파싱 (콤마 제거 등은 ExcelCellTool.getValueAsLong이 처리)
            Long total = cellTool.getValueAsLong(row, headerMap.get("전체보험료"));
            Long gov = cellTool.getValueAsLong(row, headerMap.get("국가부담보험료"));
            Long local = cellTool.getValueAsLong(row, headerMap.get("지자체부담보험료"));
            Long owner = cellTool.getValueAsLong(row, headerMap.get("주민분\n(자기부담금)")); // 줄바꿈
                                                                                      // 주의

            result.add(FreeContractUpdateInfo.builder()
                .businessNo(businessNo)
                .address(address)
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
