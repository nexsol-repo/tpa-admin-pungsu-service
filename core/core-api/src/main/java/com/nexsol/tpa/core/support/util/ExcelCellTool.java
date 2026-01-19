package com.nexsol.tpa.core.support.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExcelCellTool {

    // 문자열 추출 (Null Safe)
    public String getValueAsString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null)
            return "";

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim(); // 값 앞뒤 공백 제거
    }

    // 숫자(Long) 추출 (콤마 제거 포함)
    public long getValueAsLong(Row row, int colIndex) {
        String value = getValueAsString(row, colIndex);
        if (value.isBlank())
            return 0L;

        try {
            // "13,500" -> "13500" 처리
            return (long) Double.parseDouble(value.replaceAll("[^0-9]", ""));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 숫자(Integer) 쓰기 (Null Safe)
     */
    public void setCellValue(Row row, int colIndex, Integer value) {
        if (value != null) {
            row.createCell(colIndex).setCellValue(value);
        }
    }

    /**
     * 숫자(Long) 쓰기 (Null Safe)
     */
    public void setCellValue(Row row, int colIndex, Long value) {
        if (value != null) {
            row.createCell(colIndex).setCellValue(value);
        }
    }

    /**
     * 문자열 쓰기 (Null Safe)
     */
    public void setCellValue(Row row, int colIndex, String value) {
        if (value != null) {
            row.createCell(colIndex).setCellValue(value);
        }
    }

    /**
     * 날짜(LocalDateTime) 쓰기 (포맷 지정 가능, Null Safe)
     */
    public void setCellValue(Row row, int colIndex, LocalDateTime value, String pattern) {
        if (value != null) {
            String formatted = value.format(DateTimeFormatter.ofPattern(pattern));
            row.createCell(colIndex).setCellValue(formatted);
        }
    }

}