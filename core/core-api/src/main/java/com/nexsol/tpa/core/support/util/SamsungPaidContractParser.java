package com.nexsol.tpa.core.support.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SamsungPaidContractParser extends AbstractPaidContractParser {

    private static final String H_POLICY_NO = "증권번호";

    private static final String H_TOTAL = "전체보험료";

    private static final String H_PERSONAL = "주민분\n(자기부담금)"; // 파일 내용 그대로

    // 혹은 ExcelHeaderTool에서 공백/개행 제거 후 비교했다면 그에 맞게 설정

    public SamsungPaidContractParser(ExcelCellTool cellTool) {
        super(cellTool);
    }

    @Override
    public boolean supports(Set<String> headers) {
        return headers.contains(H_TOTAL) && headers.contains("피보험자 사업자명(상호명)");
    }

}
