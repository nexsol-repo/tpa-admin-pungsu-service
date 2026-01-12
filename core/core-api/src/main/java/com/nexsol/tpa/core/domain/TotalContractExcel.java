package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.core.support.util.ExcelCellTool;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TotalContractExcel implements ContractExcel {
    private final ExcelCellTool cellTool;

    private static final String[] HEADERS = { "거래처", "보험사", "상태", "유·무료", "사업자구분", "사업자번호", "사업장명", "사업자형태", "업종",
            "대표자명", "우편번호", "사업장주소", "전화번호", "PNUCODE", "CITYCODE", "시도구군", "건물기둥구조", "건물지붕구조", "임차여부", "지하소재", "건물층수",
            "사업장층수", "신청일자", "보험종류", "보험가입기간", "가입경로", "가입채널", "총보험료", "자기부담금", "지자체지원 보험료", "정부지원 보험료" };

    @Override
    public boolean supports(String insuranceCompany) {
        return insuranceCompany == null || insuranceCompany.isBlank();
    }

    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("전체내역");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                var cont = data.contract();
                var sub = data.subscription();
                var loc = data.location();
                var ins = data.insured();

                cellTool.setCellValue(row, 0, cont.account());
                cellTool.setCellValue(row, 1, cont.insuranceCompany());
                cellTool.setCellValue(row, 2, sub.joinCheck());
                cellTool.setCellValue(row, 3, sub.payYn());
                cellTool.setCellValue(row, 4, determineBusinessType(ins.businessNumber()));
                cellTool.setCellValue(row, 5, ins.businessNumber());
                cellTool.setCellValue(row, 6, loc.companyName());
                cellTool.setCellValue(row, 7, loc.biztype());
                cellTool.setCellValue(row, 8, loc.category());
                cellTool.setCellValue(row, 9, ins.name());
                cellTool.setCellValue(row, 12, ins.phoneNumber());
                cellTool.setCellValue(row, 10, loc.zipCode());
                cellTool.setCellValue(row, 11, loc.address());
                cellTool.setCellValue(row, 13, loc.pnu());
                cellTool.setCellValue(row, 14, loc.cityCode());
                cellTool.setCellValue(row, 15, loc.district());
                cellTool.setCellValue(row, 16, loc.mainStrctType());
                cellTool.setCellValue(row, 17, loc.roofStrctType());
                cellTool.setCellValue(row, 18, loc.tenant());
                cellTool.setCellValue(row, 19, loc.groundFloorCd());
                cellTool.setCellValue(row, 20, loc.groundFloor()); // NPE 방지
                cellTool.setCellValue(row, 21, loc.subFloor());
                cellTool.setCellValue(row, 22, cont.applicationDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 23, "풍수해6");
                cellTool.setCellValue(row, 24,datetimeFormatter(cont.insuranceStartDate(),cont.insuranceEndDate()) );
                cellTool.setCellValue(row, 25, sub.account());
                cellTool.setCellValue(row, 26, sub.path());
                cellTool.setCellValue(row, 27, sub.totalInsuranceMyCost());
                cellTool.setCellValue(row, 28, sub.totalInsuranceMyCost());
                cellTool.setCellValue(row, 29, sub.totalLocalGovernmentCost());
                cellTool.setCellValue(row, 30, sub.totalGovernmentCost());
            }
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("전체 엑셀 파일 생성 중 오류가 발생했습니다.", e);
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
