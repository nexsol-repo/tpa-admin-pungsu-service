package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuredModifyRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuredRegisterRequest;
import com.nexsol.tpa.core.api.controller.v1.request.NotificationSendRequest;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractDetailResponse;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractResponse;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.response.ApiResponse;

import com.nexsol.tpa.core.support.response.PageResponse;
import com.nexsol.tpa.core.support.response.ResultType;
import com.nexsol.tpa.web.auth.AdminUserProvider;
import com.nexsol.tpa.web.auth.LoginAdmin;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/v1/admin/pungsu")
@RestController
@RequiredArgsConstructor
public class InsuredController {

    private final InsuredService insuredService;

    private final MeritzService meritzService;

    @GetMapping("/contract")
    public ApiResponse<PageResponse<InsuredContractResponse>> getContract(@RequestParam(required = false) String status,
            @RequestParam(required = false) String account, @RequestParam(required = false) String path,
            @RequestParam(required = false) String payYn, @RequestParam(required = false) String insuranceCompany,
            @RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit, @LoginAdmin AdminUserProvider admin) {

        InsuredSearchCondition condition = InsuredSearchCondition.builder()
            .status(status)
            .payYn(payYn)
            .insuranceCompany(insuranceCompany)
            .startDate(startDate)
            .endDate(endDate)
            .keyword(keyword)
            .account(account)
            .path(path)
            .build();

        OffsetLimit offsetLimit = new OffsetLimit(offset, limit);

        DomainPage<InsuredContract> contract = insuredService.getList(condition, offsetLimit);

        List<InsuredContractResponse> responses = contract.content().stream().map(InsuredContractResponse::of).toList();

        return ApiResponse.success(new PageResponse<>(responses, contract.hasNext()));

    }

    @GetMapping("/{id}")
    public ApiResponse<InsuredContractDetailResponse> getDetail(@PathVariable Integer id) {

        InsuredContractDetail detail = insuredService.getDetail(id);

        String certificateUrl = meritzService.getLink4(detail.insuredInfo().prctrNo());

        return ApiResponse.success(InsuredContractDetailResponse.of(detail, certificateUrl));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResultType> modify(@PathVariable Integer id, @RequestBody InsuredModifyRequest request,
            @LoginAdmin AdminUserProvider admin) {
        // 서비스 레이어에 수정을 위임
        // (ID와 함께 가입자/계약정보 Record를 전달)
        insuredService.modify(id, request.insuredInfo(), request.contractInfo(), request.memoContent(), admin.id());

        return ApiResponse.success(ResultType.SUCCESS);
    }

    @PostMapping("/contract")
    public ApiResponse<ResultType> register(@RequestBody InsuredRegisterRequest request,
            @LoginAdmin AdminUserProvider admin) {

        // Service는 비즈니스 흐름만 관장 (등록 -> 로그/이벤트 발행)
        insuredService.register(request.insuredInfo(), request.contractInfo(), request.memoContent(), admin.id());

        return ApiResponse.success(ResultType.SUCCESS);
    }

    @PostMapping("/{id}/notification")
    public ApiResponse<ResultType> sendNotification(@PathVariable Integer id,
            @RequestBody NotificationSendRequest request, @LoginAdmin AdminUserProvider admin) {
        // 1. 계약 상세 정보 조회 (Service 호출)
        InsuredContractDetail detail = insuredService.getDetail(id);

        // 2. 알림 유형에 따른 타겟 URL 결정 (Controller에서 조합)
        String targetUrl = "";
        if (request.type() == MailType.REJOIN) {
            // 재가입 URL: referIdx 활용
            targetUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.insuredInfo().referIdx();
        }
        else if (request.type() == MailType.CERTIFICATE) {
            // 가입확인서 URL: MeritzService를 통해 rltLinkUrl4 조회 (getDetail과 동일 패턴)
            targetUrl = meritzService.getLink4(detail.insuredInfo().prctrNo());
        }

        // 3. 취합된 정보로 알림 발송 명령 (Service 호출)
        insuredService.send(detail, request.type(), targetUrl, admin.id());

        return ApiResponse.success(ResultType.SUCCESS);

    }

}