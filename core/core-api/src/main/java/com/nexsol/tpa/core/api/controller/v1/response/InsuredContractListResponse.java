package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.support.response.PageResponse;

public record InsuredContractListResponse(PageResponse<InsuredContractResponse> page, long renewalTargetCount) {
}