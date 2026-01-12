package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsuredSearchCondition;
import com.nexsol.tpa.core.support.OffsetLimit;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InsuredSearchRequest(String status, String payYn, LocalDate startDate, LocalDate endDate, String keyword,
        String account, String path, String insuranceCompany, Integer offset, Integer limit, String sortBy,
        String direction) {

    public InsuredSearchRequest {
        if (offset == null)
            offset = 0;
        if (limit == null)
            limit = 10;
    }

    /**
     * 도메인 레이어의 검색 조건 객체로 변환
     */
    public InsuredSearchCondition toInsuredSearchCondition() {
        return InsuredSearchCondition.builder()
            .status(status)
            .payYn(payYn)
            .startDate(startDate)
            .endDate(endDate)
            .keyword(keyword)
            .account(account)
            .path(path)
            .insuranceCompany(insuranceCompany)
            .build();
    }

    public OffsetLimit toOffsetLimit() {
        return new OffsetLimit(offset, limit, sortBy, direction);
    }
}
