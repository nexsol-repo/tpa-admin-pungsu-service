package com.nexsol.tpa.core.domain;

import java.io.OutputStream;
import java.util.List;

public interface ContractExcel {

    boolean supports(String insuranceCompany);

    void write(List<ContractExcelData> data, OutputStream outputStream);

}
