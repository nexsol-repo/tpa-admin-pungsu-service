package com.nexsol.tpa.core.support.util;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PaidContractParser {

    boolean supports(Set<String> headers);

    List<PaidConversionTarget> parse(Sheet sheet, Map<String, Integer> headerMap);

}
