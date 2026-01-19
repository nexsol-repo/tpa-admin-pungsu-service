package com.nexsol.tpa.core.domain;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FreeContractParser {

    /**
     * 해당 파서가 처리할 수 있는 엑셀 헤더인지 판단
     */
    boolean supports(Set<String> headers);

    /**
     * 엑셀 시트를 파싱하여 업데이트 정보 목록 반환
     */
    List<FreeContractUpdateInfo> parse(Sheet sheet, Map<String, Integer> headerMap);

}
