package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.BuildingLedgerOverviewResponse;
import com.nexsol.tpa.core.api.controller.v1.response.BuildingLedgerResponse;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/pungsu/building-ledger")
@RequiredArgsConstructor
public class BuildingLedgerController {

    private final BuildingLedgerService buildingLedgerService;

    /**
     * 건축물대장 상세 정보 조회 * @param sigunguCd 시군구코드 (5자리)
     * @param bjdongCd 법정동코드 (5자리)
     * @param bun 번 (4자리)
     * @param ji 지 (4자리)
     * @return 해당 대지의 건축물대장 목록
     */
    @GetMapping
    public ApiResponse<BuildingLedgerOverviewResponse> search(@RequestParam String sigunguCd,
            @RequestParam String bjdongCd, @RequestParam String bun, @RequestParam String ji,
            @LoginAdmin AdminUser admin) {

        // 1. 서비스 호출 (총괄 + 상세 목록 조회)
        BuildingLedgerOverview overview = buildingLedgerService.searchBuildingLedgerOverview(sigunguCd, bjdongCd, bun,
                ji);

        // 2. 응답 DTO 변환
        return ApiResponse.success(BuildingLedgerOverviewResponse.of(overview));
    }

}