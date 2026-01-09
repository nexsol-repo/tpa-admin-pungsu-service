package com.nexsol.tpa.core.domain;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class DbContractExcel implements ContractExcel {

    private static final String[] HEADERS = { "순번", "취급자코드", "계약자명", "계약자번호 구분", "계약자번호", "계약자사업자번호", "계약자우편번호",
            "계약자신주소", "계약자기타주소", "피보험자명", "피보험자번호구분", "피보험자번호", "피보험자사업자번호", "피보험자우편번호", "피보험자신주소", "기타번지", "갱신구분",
            "전증권번호", "소재지우편번호", "소재지신주소", "소재지기타번지", "계약유형", "영위화재업종코드", "기둥구분코드", "지붕구분코드", "외벽구분코드", "지상층수", "지하층수",
            "면적", "건물유형", "지하소재여부", "풍수해보험타보험가입여부", "목적물코드1", "풍수해가입금액1", "풍수해자가부담금액1", "야외간판금액1", "목적물코드2", "풍수해가입금액2",
            "풍수해자가부담금액2", "목적물코드3", "풍수해가입금액3", "풍수해자가부담금액3", "목적물코드4", "풍수해가입금액4", "풍수해자기부담금액4", "보험료산출기준",
            "기부가입계약유형" };

    @Override
    public boolean supports(String insuranceCompany) {
        return "DB".equalsIgnoreCase(insuranceCompany) || "DB손해보험".equals(insuranceCompany)
                || "동부".equals(insuranceCompany);
    }

    @Override
    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("DB손해보험");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                // DB 매핑 로직 (인덱스 주의)
                row.createCell(0).setCellValue(rowIndex - 1); // 순번
                row.createCell(1).setCellValue(""); // 취급자코드
                row.createCell(2).setCellValue(data.contract().companyName());
                row.createCell(3).setCellValue(2);
                row.createCell(4).setCellValue("");// 계약자번호
                row.createCell(5).setCellValue(data.contract().businessNumber());
                row.createCell(6).setCellValue("");// 계약자 우편코드
                row.createCell(7).setCellValue(data.contract().address());
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue(data.insured().name());
                row.createCell(10).setCellValue(2);
                row.createCell(11).setCellValue("");
                row.createCell(12).setCellValue(data.insured().businessNumber());
                row.createCell(13).setCellValue(data.location().zipCode());
                row.createCell(14).setCellValue(data.location().address());
                row.createCell(15).setCellValue("");// 기타 번지
                row.createCell(16).setCellValue("신규");
                row.createCell(17).setCellValue(""); // 전증권번호
                row.createCell(18).setCellValue(data.location().zipCode());
                row.createCell(19).setCellValue(data.location().address());
                row.createCell(20).setCellValue("");
                row.createCell(21).setCellValue(24);
                row.createCell(22).setCellValue("");// 영위화재업종코드
                row.createCell(23).setCellValue(data.location().mainStrctGrade());// 기둥구분코드
                row.createCell(24).setCellValue(data.location().roofStrctGrade());// 지붕코드
                row.createCell(25).setCellValue("");// 외벽코드
                row.createCell(26).setCellValue(data.location().groundFloor());
                row.createCell(27).setCellValue(data.location().underGroundFloor());
                row.createCell(28).setCellValue("");// 면적
                row.createCell(29).setCellValue(99);// 건물유형
                row.createCell(30).setCellValue(data.location().groundFloorCd());
                row.createCell(31).setCellValue(0);// 풍수해보험타보험가입여부
                row.createCell(32).setCellValue("");// 목적물코드1
                row.createCell(33).setCellValue("");// 풍수해가입금액1
                row.createCell(34).setCellValue("");// 풍수해자가부담금액1
                row.createCell(35).setCellValue("");// 야외간판금액1
                row.createCell(36).setCellValue("");// 목적물코드2
                row.createCell(37).setCellValue("");// 풍수해가입금액2
                row.createCell(38).setCellValue("");// 풍수해자가부담금액2
                row.createCell(39).setCellValue("");// 목적물코드3
                row.createCell(40).setCellValue("");// 풍수해가입금액3
                row.createCell(41).setCellValue("");// 풍수해자가부담금액3
                row.createCell(42).setCellValue("");// 목적물코드4
                row.createCell(43).setCellValue("");// 풍수해가입금액4
                row.createCell(44).setCellValue("");// 풍수해자기부담금액4
                row.createCell(45).setCellValue("");// 보험료산출기준
                row.createCell(46).setCellValue("");// 기부가입계약유형

            }
            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("DB손해보험 엑셀 생성 실패", e);
        }

    }

}
