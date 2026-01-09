package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TotalContractExcel implements ContractExcel {

    private static final String[] HEADERS = { "거래처", "보험사", "상태", "유·무료", "사업자구분", "사업자번호", "사업장명", "사업자형태", "업종",
            "대표자명", "우편번호", "사업장주소", "전화번호", "PNUCODE", "CITYCODE", "시도구군", "건물기둥구조", "건물지붕구조", "임차여부", "지하소재", "건물층수",
            "사업장층수", "신청일자", "보험종류", "보험가입기간", "가입경로", "가입채널", "총보험료", "자기부담금", "지자체지원 보험료", "정부지원 보험료" };

    @Override
    public boolean supports(String insuranceCompany) {
        return insuranceCompany == null || insuranceCompany.isBlank();
    }

    @Override
    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("전체내역");

            // 1. 헤더 생성
            createHeader(sheet);

            // 2. 데이터 바인딩
            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                bindData(row, data);
            }
            workbook.write(outputStream);
        }
        catch (Exception e) {
            throw new RuntimeException("전체 엑셀 파일 생성 중 오류가 발생했습니다.", e);
        }
    }

    private void bindData(Row row, ContractExcelData data) {
        // 1. 기본 계약 정보
        row.createCell(0).setCellValue(data.contract().account()); // 거래처
        row.createCell(1).setCellValue(data.contract().insuranceCompany()); // 보험사
        row.createCell(2).setCellValue(data.subscription().joinCheck()); // 상태 (Enum 가정)
        row.createCell(3).setCellValue(data.subscription().payYn()); // 유·무료 (boolean 가정)

        // 2. 피보험자 정보
        row.createCell(4).setCellValue(determineBusinessType(data.insured().businessNumber())); // 사업자구분
        row.createCell(5).setCellValue(data.insured().businessNumber()); // 사업자번호
        row.createCell(6).setCellValue(data.location().companyName()); // 사업장명
        row.createCell(7).setCellValue(data.location().biztype()); // 사업자형태
        row.createCell(8).setCellValue(data.location().category()); // 업종
        row.createCell(9).setCellValue(data.insured().name()); // 대표자명
        row.createCell(12).setCellValue(data.insured().phoneNumber()); // 전화번호

        // 3. 소재지 정보
        row.createCell(10).setCellValue(data.location().zipCode()); // 우편번호
        row.createCell(11).setCellValue(data.location().address()); // 사업장주소 (편의 메서드 활용)
        row.createCell(13).setCellValue(data.location().pnu()); // PNUCODE
        row.createCell(14).setCellValue(data.location().cityCode()); // CITYCODE
        row.createCell(15).setCellValue(data.location().district()); // 시도구군
        row.createCell(16).setCellValue(data.location().mainStrctType()); // 건물 기둥 구조
        row.createCell(17).setCellValue(data.location().roofStrctType()); // 건물 지붕 구조
        row.createCell(18).setCellValue(data.location().tenant()); // 임차여부
        row.createCell(19).setCellValue(data.location().groundFloorCd()); // 지하소재 (boolean
                                                                          // 가정)
        row.createCell(20).setCellValue(data.location().groundFloor()); // 건물층수
        row.createCell(21).setCellValue(data.location().subFloor()); // 사업장층수

        // 4. 가입/청약 정보
        row.createCell(22).setCellValue(data.contract().applicationDate()); // 신청일자
        row.createCell(23).setCellValue("풍수해6"); // 보험종류
        row.createCell(24)
            .setCellValue(datetimeFormatter(data.contract().insuranceStartDate(), data.contract().insuranceEndDate())); // 보험가입기간
        row.createCell(25).setCellValue(data.subscription().account()); // 가입경로
        row.createCell(26).setCellValue(data.subscription().path()); // 가입채널
        row.createCell(27).setCellValue(data.subscription().totalInsuranceMyCost()); // 총보험료
        row.createCell(28).setCellValue(data.subscription().totalInsuranceMyCost()); // 자기부담금
        row.createCell(29).setCellValue(data.subscription().totalLocalGovernmentCost()); // 지자체지원
                                                                                         // 보험료
        row.createCell(30).setCellValue(data.subscription().totalGovernmentCost()); // 정부지원
                                                                                    // 보험료
    }

    private void createHeader(SXSSFSheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(HEADERS[i]);
        }
    }

    private static String determineBusinessType(String businessNumber) {
        String cleanedCompanyCode = businessNumber.replaceAll("-", "");

        if (cleanedCompanyCode.length() != 10) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        String middleTwoDigits = cleanedCompanyCode.substring(3, 5);

        int digits = Integer.parseInt(middleTwoDigits);
        String result = switch (digits) {
            case 81, 82, 83, 84, 85, 86, 87, 88 -> "법인";
            default -> "개인";
        };
        return result;

    }

    private static String datetimeFormatter(LocalDateTime startDate, LocalDateTime endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (startDate == null || endDate == null) {
            return "-"; // 혹은 빈 문자열 "" 반환
        }

        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        return start + " ~ " + end;
    }

}
