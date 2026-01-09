package com.nexsol.tpa.core.domain;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class SamsungContract implements ContractExcel {

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
                // 삼성화재 매핑 로직
                row.createCell(0).setCellValue(data.subscription().insuranceStartDate());
                row.createCell(1).setCellValue(data.subscription().insuranceEndDate());
                row.createCell(2).setCellValue(data.location().companyName());
                row.createCell(3).setCellValue(data.insured().businessNumber());
                row.createCell(4).setCellValue(data.location().category());
                row.createCell(5).setCellValue(data.location().zipCode());
                row.createCell(6).setCellValue(data.location().district());
                row.createCell(7).setCellValue(data.location().address());
                row.createCell(8).setCellValue("");// TODO : address detail 필요
                row.createCell(9).setCellValue(data.location().bldGrade());
                row.createCell(10).setCellValue(""); // TODO: using_area 면적
                row.createCell(11).setCellValue(data.location().groundFloorCd());
                row.createCell(12).setCellValue(data.insured().name());
                row.createCell(13).setCellValue(data.insured().phoneNumber());
                row.createCell(14).setCellValue("");// TODO: 물건구분
                row.createCell(15).setCellValue(data.location().tenant());
                row.createCell(16).setCellValue(data.location().mainStrctType());
                row.createCell(17).setCellValue(data.location().roofStrctType());
                row.createCell(18).setCellValue(data.subscription().insuranceCostFcl());
                row.createCell(19).setCellValue(data.subscription().insuranceCostMach());
                row.createCell(20).setCellValue(data.subscription().insuranceCostInven());
                row.createCell(21).setCellValue(data.subscription().insuranceCostDeductible());
                row.createCell(21).setCellValue(data.insured().birthDate());

                // ...
            }
            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("삼성화재 엑셀 생성 실패", e);
        }

    }

}
