package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.util.ExcelCellTool;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class KbContractExcel implements ContractExcel {

    private final ExcelCellTool cellTool;

    private static final Pattern ADDR_NUM_PATTERN = Pattern.compile("(\\d+)(?:-(\\d+))?$");

    private static final String[] HEADERS = { "보험시작일", "보험종기일", "피보험자 사업자명(상호명)", "피보험자 사업자번호", "업종소분류", "우편번호1",
            "우편번호2", "기본주소", "상세주소", "도로명 건물주번호", "도로명 건물부번호", "이메일주소", "건물급수", "면적", "사업장 지하소재여부", "대표자성함", "전화번호1",
            "전화번호2", "전화번호3", "물건구분", "임차여부", "건물", "시설", "집기", "기계", "재고자산", "자기부담금" };

    @Override
    public boolean supports(String insuranceCompany) {
        return "KB".equalsIgnoreCase(insuranceCompany) || "KB손해보험".equals(insuranceCompany);
    }

    @Override
    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("KB손해보험");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                var loc = data.location();
                var sub = data.subscription();
                var ins = data.insured();

                cellTool.setCellValue(row, 0, sub.insuranceStartDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 1, sub.insuranceEndDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 2, loc.companyName());
                cellTool.setCellValue(row, 3, data.contract().businessNumber());
                cellTool.setCellValue(row, 4, loc.category());
                cellTool.setCellValue(row, 5, data.contract().businessNumber());
                cellTool.setCellValue(row, 6, zipCodeFirst(loc.zipCode()));
                cellTool.setCellValue(row, 7, zipCodeSecond(loc.zipCode()));
                cellTool.setCellValue(row, 8, loc.address());
                cellTool.setCellValue(row, 9, "");// TODO: address_detail
                cellTool.setCellValue(row, 10, splitAddress(loc.address())[0]);
                cellTool.setCellValue(row, 11, splitAddress(loc.address())[1]);
                cellTool.setCellValue(row, 12, ins.email());
                cellTool.setCellValue(row, 13, "");// TODO: 면적
                cellTool.setCellValue(row, 14, loc.groundFloorCd());
                cellTool.setCellValue(row, 15, ins.name());
                cellTool.setCellValue(row, 16, splitPhoneNumber(ins.phoneNumber())[0]);
                cellTool.setCellValue(row, 17, splitPhoneNumber(ins.phoneNumber())[1]);
                cellTool.setCellValue(row, 18, splitPhoneNumber(ins.phoneNumber())[2]);
                cellTool.setCellValue(row, 19, ""); // TODO: 물건구분
                cellTool.setCellValue(row, 20, loc.tenant());
                cellTool.setCellValue(row, 21, sub.insuranceCostBld());
                cellTool.setCellValue(row, 22, sub.insuranceCostFcl());
                cellTool.setCellValue(row, 23, sub.insuranceCostMach());
                cellTool.setCellValue(row, 24, sub.insuranceCostInven());
                cellTool.setCellValue(row, 25, sub.insuranceCostDeductible());

            }
            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("KB손해보험 엑셀 생성 실패", e);
        }

    }

    private static String zipCodeFirst(String zipCode) {
        return zipCode.substring(0, 2);
    }

    private static String zipCodeSecond(String zipCode) {
        return zipCode.substring(3, 4);
    }

    private static String[] splitAddress(String address) {
        if (address == null || address.isBlank()) {
            return new String[] { "", "" };
        }

        // 1. 괄호 참고항목 제거
        String cleanAddr = address.replaceAll("\\s*\\(.*?\\)$", "").trim();

        // 2. 공백으로 나눠서 가장 마지막 요소(번지 부분) 추출
        String[] parts = cleanAddr.split("\\s+");
        String numberPart = parts[parts.length - 1];

        // 3. 정규식 매칭
        Matcher matcher = ADDR_NUM_PATTERN.matcher(numberPart);

        if (matcher.find()) {
            String main = matcher.group(1); // 주번호
            String sub = matcher.group(2); // 부번호 (없으면 null)
            return new String[] { main, sub != null ? sub : "" };
        }

        return new String[] { "", "" };
    }

    private static String[] splitPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return new String[] { "", "", "" };
        }

        // 1. 숫자를 제외한 모든 문자(하이픈 등) 제거
        String digits = phoneNumber.replaceAll("\\D", "");

        // 2. 010으로 시작하는 경우 앞의 '0' 제거 (010 -> 10)
        if (digits.startsWith("010")) {
            digits = digits.substring(1);
        }

        // 3. 남은 숫자에 따른 분기 처리 (기본적인 한국 번호 체계 기준)
        // 예: 1012345678 (10자리) -> 10 / 1234 / 5678
        int len = digits.length();
        if (len >= 10) {
            String p1 = digits.substring(0, 2); // 10
            String p2 = digits.substring(2, len - 4); // 중간 (3~4자리)
            String p3 = digits.substring(len - 4); // 마지막 4자리
            return new String[] { p1, p2, p3 };
        }

        return new String[] { digits, "", "" }; // 예외 케이스 처리

    }

}
