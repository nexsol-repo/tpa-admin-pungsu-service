package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.BuildingLedgerResponse;
import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.BuildingLedger;
import com.nexsol.tpa.core.domain.BuildingLedgerService;
import com.nexsol.tpa.core.domain.LoginAdmin;
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
    public ApiResponse<List<BuildingLedgerResponse>> search(@RequestParam String sigunguCd,
            @RequestParam String bjdongCd, @RequestParam String bun, @RequestParam String ji,
            @LoginAdmin AdminUser admin) {
        // 1. 서비스 호출 (비즈니스 로직 수행)
        List<BuildingLedger> ledgers = buildingLedgerService.searchBuildingLedgers(sigunguCd, bjdongCd, bun, ji);

        // 2. 도메인 -> 응답 DTO 변환
        List<BuildingLedgerResponse> response = ledgers.stream().map(BuildingLedgerResponse::of).toList();

        // 3. 응답 반환
        return ApiResponse.success(response);
    }

}