package com.nexsol.tpa.core.support.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaidContractExcelTool {

    private final ExcelHeaderTool headerTool;

    private final List<PaidContractParser> parsers;

    public List<PaidConversionTarget> parseFile(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 1. 헤더 추출
            Map<String, Integer> headerMap = headerTool.parseHeaderIndexMap(sheet);
            Set<String> headers = headerMap.keySet();

            // 2. 이 헤더를 처리할 수 있는 파서 찾기 (Strategy Selection)
            PaidContractParser parser = parsers.stream()
                .filter(p -> p.supports(headers))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 엑셀 양식입니다. 헤더를 확인해주세요."));

            log.info("Selected Parser: {}", parser.getClass().getSimpleName());

            // 3. 파싱 위임
            return parser.parse(sheet, headerMap);

        }
        catch (IOException e) {
            throw new RuntimeException("파일 처리 실패", e);
        }
    }

}
