package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.util.ExcelHeaderTool;
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
public class FreeContractExcelTool {

    private final ExcelHeaderTool headerTool;

    private final List<FreeContractParser> parsers; // 모든 파서 주입 (Strategy Pattern)

    public List<FreeContractUpdateInfo> parseFile(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 1. 헤더 분석
            Map<String, Integer> headerMap = headerTool.parseHeaderIndexMap(sheet);
            Set<String> headers = headerMap.keySet();

            // 2. 지원하는 파서 찾기
            FreeContractParser parser = parsers.stream()
                .filter(p -> p.supports(headers))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 무료계약 엑셀 양식입니다."));

            log.info("Selected FreeContract Parser: {}", parser.getClass().getSimpleName());

            // 3. 파싱 실행
            return parser.parse(sheet, headerMap);

        }
        catch (IOException e) {
            throw new RuntimeException("엑셀 파일 처리 실패", e);
        }
    }

}