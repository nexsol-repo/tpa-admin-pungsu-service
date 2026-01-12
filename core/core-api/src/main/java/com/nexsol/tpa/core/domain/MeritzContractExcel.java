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
public class MeritzContractExcel implements ContractExcel {

    private final ExcelCellTool cellTool;

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
                var loc = data.location();
                var sub = data.subscription();
                // 메리츠 매핑 로직
                cellTool.setCellValue(row, 0, sub.insuranceStartDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 1, loc.district());
                cellTool.setCellValue(row, 2, loc.cityCode());
                cellTool.setCellValue(row, 3, loc.address());
                cellTool.setCellValue(row, 4, data.insured().name());
                cellTool.setCellValue(row, 5, data.insured().businessNumber());
                cellTool.setCellValue(row, 6, 2); // TODO: 내진설계 확인필요
                cellTool.setCellValue(row, 7, ""); // TODO: 건물 세부 코드
                cellTool.setCellValue(row, 8, loc.bldGrade());
                cellTool.setCellValue(row, 9, loc.biztype());
                cellTool.setCellValue(row, 10, sub.insuranceCostBld());
                cellTool.setCellValue(row, 11, sub.insuranceCostFcl());
                cellTool.setCellValue(row, 12, sub.insuranceCostMach());
                cellTool.setCellValue(row, 13, sub.insuranceCostInven());
                cellTool.setCellValue(row, 14, sub.insuranceCostDeductible());
                cellTool.setCellValue(row, 15, loc.groundFloorCd());
                cellTool.setCellValue(row, 16, pungsuGivenCode(sub.payYn()));
                cellTool.setCellValue(row, 17, pungsuPlaceCode(loc.tmYn(), loc.groundFloorYn()));
                cellTool.setCellValue(row, 18, "");// TODO: using_area 면적
                cellTool.setCellValue(row, 19, ""); // TODO: using_flr_nm_list이 있어야
                // 전체층가입여부 확인가능
                cellTool.setCellValue(row, 20, loc.groundFloor());
                cellTool.setCellValue(row, 21, loc.underGroundFloor());
                cellTool.setCellValue(row, 22, "");// TODO: using_flr_nm_list 가입층수
                cellTool.setCellValue(row, 23, data.contract().applicationDate(), "yyyy-MM-dd");
                cellTool.setCellValue(row, 24, companyType(loc.biztype()));

            }

            workbook.write(outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException("메리츠 엑셀 생성 실패", e);
        }
    }

    private String pungsuGivenCode(String payYn) {
        return "Y".equals(payYn) ? "01" : "05";
    }

    private String pungsuPlaceCode(String tmYn, String groundFloorYn) {

        if ("Y".equals(tmYn)) {
            return "01";
        }

        if ("Y".equals(groundFloorYn)) {
            return "02";
        }

        return "03";
    }

    private String companyType(String bizType) {
        // bizType이 null일 경우를 대비해 문자열을 앞에 두고 비교
        return "소상인(일반)".equals(bizType) ? "일반" : "공장";
    }

}
