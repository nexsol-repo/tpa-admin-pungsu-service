package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuredExcelWriter {

    private final List<ContractExcel> contractExcels;

    /**
     * @param insuranceCompany 보험사명 (없으면 전체 다운로드)
     * @param data 엑셀용 데이터 목록 (Record List)
     * @param out 출력 스트림
     */
    public void write(String insuranceCompany, List<ContractExcelData> data, OutputStream out) {
        ContractExcel excelImplementation = contractExcels.stream()
            .filter(excel -> excel.supports(insuranceCompany))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 보험사 양식입니다: " + insuranceCompany));

        excelImplementation.write(data, out);
    }

}
