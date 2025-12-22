package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuredModifyRequest;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractResponse;
import com.nexsol.tpa.core.domain.InsuredContract;
import com.nexsol.tpa.core.domain.InsuredContractDetail;
import com.nexsol.tpa.core.domain.InsuredSearchCondition;
import com.nexsol.tpa.core.domain.InsuredService;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.response.ApiResponse;

import com.nexsol.tpa.core.support.response.PageResponse;
import com.nexsol.tpa.core.support.response.ResultType;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/v1/admin/pungsu/insured")
@RestController
@RequiredArgsConstructor
public class InsuredController {

    private final InsuredService insuredService;

    @GetMapping("/contract")
    public ApiResponse<PageResponse<InsuredContractResponse>> getContract(@RequestParam(required = false) String status,
            @RequestParam(required = false) String payYn, @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) {

        InsuredSearchCondition condition = InsuredSearchCondition.builder()
            .status(status)
            .payYn(payYn)
            .startDate(startDate)
            .endDate(endDate)
            .keyword(keyword)
            .build();

        OffsetLimit offsetLimit = new OffsetLimit(offset, limit);

        DomainPage<InsuredContract> contract = insuredService.getList(condition, offsetLimit);

        List<InsuredContractResponse> responses = contract.content().stream().map(InsuredContractResponse::of).toList();

        return ApiResponse.success(new PageResponse<>(responses, contract.hasNext()));

    }

    @GetMapping("/{id}")
    public ApiResponse<InsuredContractDetail> getDetail(@PathVariable Integer id) {
        return ApiResponse.success(insuredService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResultType> modify(@PathVariable Integer id, @RequestBody InsuredModifyRequest request) {
        // 서비스 레이어에 수정을 위임
        // (ID와 함께 가입자/계약정보 Record를 전달)
        insuredService.modify(id, request.insuredInfo(), request.contractInfo());

        return ApiResponse.success(ResultType.SUCCESS);
    }

}
