package com.nexsol.tpa.core.support.util;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AbstractPaidContractParser implements PaidContractParser {

    protected final ExcelCellTool cellTool;

    @Override
    public boolean supports(Set<String> headers) {
        return false;
    }

    @Override
    public List<PaidConversionTarget> parse(Sheet sheet, Map<String, Integer> headerMap) {
        return List.of();
    }

    protected PaidConversionTarget mapRow(Row row, Map<String, Integer> headerMap) {
        return null;
    }

    // 유틸: 맵에서 키로 값을 찾아 Long으로 반환 (하위 클래스 편의 제공)
    protected long getLong(Row row, Map<String, Integer> map, String key) {
        return map.containsKey(key) ? cellTool.getValueAsLong(row, map.get(key)) : 0L;
    }

    protected String getString(Row row, Map<String, Integer> map, String key) {
        return map.containsKey(key) ? cellTool.getValueAsString(row, map.get(key)) : null;
    }

}
