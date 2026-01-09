package com.nexsol.tpa.core.domain;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class MeritzContractExcel implements ContractExcel {

    private static final String[] HEADERS = { "보험시작일자", "시군구명", "시군구코드", "소재지주소", "소유자명", "사업자번호", "내진설계", "건물세부코드",
            "건물급수", "소유구분(임차자/소유자)", "건물", "시설", "비품/집기", "기계", "재고자산", "자기부담금", "지하소재여부", "풍수해보험료지원대상코드", "풍수해시설유형코드",
            "보험가입면적", "전체층가입여부", "건물지상총층수", "건물지하총층수", "가입층수", "가입신청일", "일반/공장" };

    @Override
    public boolean supports(String insuranceCompany) {
        return "MERITZ".equalsIgnoreCase(insuranceCompany) || "메리츠".equals(insuranceCompany);
    }

    @Override
    public void write(List<ContractExcelData> dataList, OutputStream outputStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            var sheet = workbook.createSheet("메리츠화재");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ContractExcelData data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                // 메리츠 매핑 로직
                row.createCell(0).setCellValue(data.contract().applicationDate());
                row.createCell(1).setCellValue(data.location().district());
                row.createCell(2).setCellValue(data.location().cityCode());
                row.createCell(3).setCellValue(data.location().address());
                row.createCell(4).setCellValue(data.insured().name());
                row.createCell(5).setCellValue(data.insured().businessNumber());
                row.createCell(6).setCellValue(2); // TODO: 내진설계 확인필요
                row.createCell(7).setCellValue("");// TODO: 건물 세부 코드
                row.createCell(8).setCellValue(data.location().bldGrade());
                row.createCell(9).setCellValue(data.location().biztype());
                row.createCell(10).setCellValue(data.subscription().insuranceCostBld());
                row.createCell(11).setCellValue(data.subscription().insuranceCostFcl());
                row.createCell(12).setCellValue(data.subscription().insuranceCostMach());
                row.createCell(13).setCellValue(data.subscription().insuranceCostInven());
                row.createCell(14).setCellValue(data.subscription().insuranceCostDeductible());
                row.createCell(15).setCellValue(data.location().groundFloorCd());
                row.createCell(16).setCellValue(pungsuGivenCode(data.subscription().payYn()));
                row.createCell(17)
                    .setCellValue(pungsuPlaceCode(data.location().tmYn(), data.location().groundFloorYn()));
                row.createCell(18).setCellValue(""); // TODO: using_area 면적
                row.createCell(19).setCellValue(""); // TODO: using_flr_nm_list이 있어야
                                                     // 전체층가입여부 확인가능
                row.createCell(20).setCellValue(data.location().groundFloor());
                row.createCell(21).setCellValue(data.location().underGroundFloor());
                row.createCell(22).setCellValue(""); // TODO: using_flr_nm_list 가입층수
                row.createCell(23).setCellValue(data.contract().applicationDate());
                row.createCell(24).setCellValue(companyType(data.location().biztype()));

            }

            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("메리츠 엑셀 생성 실패", e);
        }
    }

    private static String pungsuGivenCode(String payYn) {
        return payYn == "Y" ? "01" : "05";
    }

    private static String pungsuPlaceCode(String tmYn, String groundFloorYn) {
        return switch (tmYn) {
            // 1순위: tmYn이 "Y"인 경우
            case "Y" -> "01";

            // 2순위: tmYn이 "Y"가 아닐 때 groundFloorYn에 따라 분기
            default -> switch (groundFloorYn) {
                case "Y" -> "02";
                default -> "03";
            };
        };

    }

    private static String companyType(String bizType) {
        return bizType == "소상인(일반)" ? "일반" : "공장";

    }

}
