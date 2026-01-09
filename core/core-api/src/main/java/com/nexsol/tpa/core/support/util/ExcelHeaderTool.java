package com.nexsol.tpa.core.support.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ExcelHeaderTool {

    public Map<String, Integer> parseHeaderIndexMap(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();

        if (headerRow != null) {
            for (Cell cell : headerRow) {
                // 헤더의 앞뒤 공백을 제거하여 Key로 저장
                // 예: " 합계 " -> "합계"
                String headerName = cell.getStringCellValue().trim();
                if (!headerName.isBlank()) {
                    headerMap.put(headerName, cell.getColumnIndex());
                }
            }
        }
        return headerMap;
    }

    // 필수 헤더 검증
    public void validateRequiredHeaders(Map<String, Integer> map, Set<String> requiredHeaders) {
        for (String required : requiredHeaders) {
            if (!map.containsKey(required)) {
                throw new IllegalArgumentException("필수 컬럼이 누락되었습니다: " + required);
            }
        }
    }

}