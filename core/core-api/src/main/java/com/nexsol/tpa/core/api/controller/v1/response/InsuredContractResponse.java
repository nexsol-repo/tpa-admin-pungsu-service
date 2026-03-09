package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.InsuredContract;
import com.nexsol.tpa.core.enums.DisplayStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredContractResponse(Integer id, String businessNumber, String companyName, String address,
        String phoneNumber, LocalDateTime applicationDate, String insuranceCompany, LocalDateTime insuranceStartDate,
        LocalDateTime insuranceEndDate, DisplayStatus displayStatus, String joinCheck, String account, String path,
        String payYn, String referIdx) {

    public static InsuredContractResponse of(InsuredContract contract) {
        return InsuredContractResponse.builder()
            .id(contract.id())
            .businessNumber(contract.businessNumber())
            .companyName(contract.companyName())
            .address(contract.address())
            .phoneNumber(contract.phoneNumber())
            .applicationDate(contract.applicationDate())
            .insuranceCompany(contract.insuranceCompany())
            .insuranceStartDate(contract.insuranceStartDate())
            .insuranceEndDate(contract.insuranceEndDate())
            .payYn(contract.payYn())
            .displayStatus(contract.displayStatus())
            .joinCheck(contract.joinCheck())
            .account(contract.account())
            .path(contract.path())
            .referIdx(contract.referIdx())
            .build();
    }
}