package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.util.ExcelCellTool;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SamsungContract implements ContractExcel {

    private final ExcelCellTool cellTool;

    private static final String[] HEADERS = { "보험시작일", "보험종기일", "피보험자 사업자명(상호명)", "피보험자 사업자번호", "업종소분류", "우편번호1", "시도",
            "기본주소", "상세주소", "건물급수", "면적", "사업장 지하소재여부", "대표자성함", "전화번호1", "물건구분", "임차여부", "건물기둥구조", "건물지붕구조", "집기비품",
            "시설", "기계", "재고자산", "자기부담금", "생년월일" };

    @Override
    public boolean supports(String insuranceCompany) {
        return "SAMSUNG".equalsIgnoreCase(insuranceCompany) || "삼성".equals(insuranceCompany)
                || "삼성화재".equals(insuranceCompany);
    }

    @Override
    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("삼성화재");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                var loc = data.location();
                var sub = data.subscription();
                // 삼성화재 매핑 로직
                cellTool.setCellValue(row, 0, sub.insuranceStartDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 1, sub.insuranceEndDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 2, loc.companyName());
                cellTool.setCellValue(row, 3, data.insured().businessNumber());
                cellTool.setCellValue(row, 4, loc.category());
                cellTool.setCellValue(row, 5, loc.zipCode());
                cellTool.setCellValue(row, 6, loc.district());
                cellTool.setCellValue(row, 7, loc.address());
                cellTool.setCellValue(row, 8, "");// TODO : address detail 필요
                cellTool.setCellValue(row, 9, loc.bldGrade());
                cellTool.setCellValue(row, 10, ""); // TODO: using_area 면적
                cellTool.setCellValue(row, 11, loc.groundFloorCd());
                cellTool.setCellValue(row, 12, data.insured().name());
                cellTool.setCellValue(row, 13, data.insured().phoneNumber());
                cellTool.setCellValue(row, 14, "");// TODO: 물건구분
                cellTool.setCellValue(row, 15, loc.tenant());
                cellTool.setCellValue(row, 16, loc.mainStrctType());
                cellTool.setCellValue(row, 17, loc.roofStrctType());
                cellTool.setCellValue(row, 18, sub.insuranceCostFcl());
                cellTool.setCellValue(row, 19, sub.insuranceCostMach());
                cellTool.setCellValue(row, 20, sub.insuranceCostInven());
                cellTool.setCellValue(row, 21, sub.insuranceCostDeductible());
                cellTool.setCellValue(row, 22, data.insured().birthDate());

            }
            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("삼성화재 엑셀 생성 실패", e);
        }

    }

}
