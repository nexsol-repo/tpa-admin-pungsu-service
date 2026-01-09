package com.nexsol.tpa.core.domain;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KbContractExcel implements ContractExcel {

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
                // KB 매핑 로직
                row.createCell(0).setCellValue(data.subscription().insuranceStartDate());
                row.createCell(1).setCellValue(data.subscription().insuranceEndDate());
                row.createCell(2).setCellValue(data.location().companyName());
                row.createCell(3).setCellValue(data.contract().businessNumber());
                row.createCell(4).setCellValue(data.location().category());
                row.createCell(5).setCellValue(data.contract().businessNumber());
                row.createCell(6).setCellValue(zipCodeFirst(data.location().zipCode()));
                row.createCell(7).setCellValue(zipCodeSecond(data.location().zipCode()));
                row.createCell(8).setCellValue(data.location().address());
                row.createCell(9).setCellValue(""); // TODO: address_detail
                row.createCell(10).setCellValue(splitAddress(data.location().address())[0]);
                row.createCell(11).setCellValue(splitAddress(data.location().address())[1]);
                row.createCell(12).setCellValue(data.insured().email());
                row.createCell(13).setCellValue("");// TODO: 면적
                row.createCell(14).setCellValue(data.location().groundFloorCd());
                row.createCell(15).setCellValue(data.insured().name());
                row.createCell(16).setCellValue(splitPhoneNumber(data.insured().phoneNumber())[0]);
                row.createCell(17).setCellValue(splitPhoneNumber(data.insured().phoneNumber())[1]);
                row.createCell(18).setCellValue(splitPhoneNumber(data.insured().phoneNumber())[2]);
                row.createCell(19).setCellValue(""); // TODO: 물건구분
                row.createCell(20).setCellValue(data.location().tenant());
                row.createCell(21).setCellValue(data.subscription().insuranceCostBld());
                row.createCell(22).setCellValue(data.subscription().insuranceCostFcl());
                row.createCell(23).setCellValue(data.subscription().insuranceCostMach());
                row.createCell(24).setCellValue(data.subscription().insuranceCostInven());
                row.createCell(25).setCellValue(data.subscription().insuranceCostDeductible());

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
