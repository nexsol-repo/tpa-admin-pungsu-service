package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsuredContractInfo;
import com.nexsol.tpa.core.domain.InsuredInfo;
import lombok.Builder;

@Builder
public record InsuredRegisterRequest(InsuredInfo insuredInfo, InsuredContractInfo contractInfo, String memoContent // 등록
                                                                                                                   // 시
                                                                                                                   // 초기
                                                                                                                   // 메모가
                                                                                                                   // 있다면
                                                                                                                   // 포함
) {
}