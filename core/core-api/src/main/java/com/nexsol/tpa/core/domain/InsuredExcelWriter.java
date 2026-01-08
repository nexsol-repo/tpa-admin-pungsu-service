package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.util.InsuredExcelConstants;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class InsuredExcelWriter {

    public void write(List<ContractExcelData> contracts, OutputStream out) throws IOException {
        // try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
        // Sheet sheet = workbook.createSheet("계약리스트");
        //
        // createHeaderRow(sheet);
        //
        // int rowIndex = 1;
        // for (ContractExcelData data : dataList) {
        // createDataRow(sheet, rowIndex++, data);
        // }
        //
        // workbook.write(out);
        // workbook.dispose();
        // }
    }

    private void createHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        String[] headers = InsuredExcelConstants.CONTRACT_LIST_HEADERS;
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    private void createDataRow(Sheet sheet, int rowIndex, ContractExcelData data) {
        Row row = sheet.createRow(rowIndex);
        int c = 0;

        // DTO가 아닌 도메인 객체의 필드를 사용
        setCellValue(row, c++, data.account());
        setCellValue(row, c++, data.insuranceCompany());
        setCellValue(row, c++, data.status());
        setCellValue(row, c++, "Y".equals(data.payYn()) ? "유료" : "무료"); // 포맷팅
        setCellValue(row, c++, "개인"); // 고정값
        setCellValue(row, c++, data.businessNumber());
        setCellValue(row, c++, data.companyName());
        setCellValue(row, c++, data.biztype()); // 소상인 구분
        setCellValue(row, c++, data.category()); // 업종 (필요 시 추가)
        setCellValue(row, c++, data.ceoName());
        setCellValue(row, c++, data.zipCode());
        setCellValue(row, c++, data.address());
        setCellValue(row, c++, data.phoneNumber());
        setCellValue(row, c++, ""); // PNU
        setCellValue(row, c++, ""); // CITY
        setCellValue(row, c++, ""); // 시도구군
        setCellValue(row, c++, ""); // 건물구조
        setCellValue(row, c++, ""); // 기타구조
        setCellValue(row, c++, "임차자");
        setCellValue(row, c++, "아니오");
        setCellValue(row, c++, "1층");
        setCellValue(row, c++, "");

        // 날짜 포맷팅
        // setCellValue(row, c++, data.getApplicationDate() != null ?
        // data.getApplicationDate().format(DATETIME_FMT) : "");
        // setCellValue(row, c++, "풍수해6");
        //
        // String term = "";
        // if (data.getInsuranceStartDate() != null && data.getInsuranceEndDate() != null)
        // {
        // term = data.getInsuranceStartDate().format(DATE_FMT) + " ~ " +
        // data.getInsuranceEndDate().format(DATE_FMT);
        // }
        // setCellValue(row, c++, term);
        //
        // setCellValue(row, c++, data.getPath());
        // setCellValue(row, c++, "재가입");
        //
        // // 금액 포맷팅 (천단위 콤마)
        // setCellValue(row, c++, formatMoney(data.getTotalPremium()));
        // setCellValue(row, c++, formatMoney(data.getDeductibleAmount()));
        // setCellValue(row, c++, formatMoney(data.getLocalShareAmount()));
        // setCellValue(row, c++, formatMoney(data.getStateShareAmount()));
    }

    private void setCellValue(Row row, int cellIndex, String value) {
        row.createCell(cellIndex).setCellValue(value != null ? value : "");
    }

    // private String formatMoney(long amount) {
    // return MONEY_FMT.format(amount);
    // }

}
