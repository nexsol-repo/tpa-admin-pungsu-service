package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.BulkNotificationSendRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuredModifyRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuredRegisterRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuredSearchRequest;
import com.nexsol.tpa.core.api.controller.v1.request.NotificationSendRequest;
import com.nexsol.tpa.core.api.controller.v1.response.BulkNotificationSendResponse;
import com.nexsol.tpa.core.api.controller.v1.response.FreeContractUploadResponse;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractDetailResponse;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractListResponse;
import com.nexsol.tpa.core.api.controller.v1.response.InsuredContractResponse;
import com.nexsol.tpa.core.api.controller.v1.response.RenewalHistoryResponse;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.response.ApiResponse;

import com.nexsol.tpa.core.support.response.PageResponse;
import com.nexsol.tpa.core.support.response.ResultType;
import feign.template.UriUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/v1/admin/pungsu")
@RestController
@RequiredArgsConstructor
public class InsuredController {

    private final InsuredService insuredService;

    private final RenewalGroupService renewalGroupService;

    // private final MeritzService meritzService;

    @GetMapping("/contract")
    public ApiResponse<InsuredContractListResponse> getContract(@ModelAttribute InsuredSearchRequest request,
            @LoginAdmin AdminUser admin) {

        InsuredSearchCondition condition = request.toInsuredSearchCondition();

        DomainPage<InsuredContract> contract = insuredService.getList(condition, request.toOffsetLimit());

        long renewalTargetCount = insuredService.getRenewalTargetCount(condition);

        List<InsuredContractResponse> responses = contract.content().stream().map(InsuredContractResponse::of).toList();

        PageResponse<InsuredContractResponse> page = new PageResponse<>(responses, contract.hasNext(),
                contract.totalElements(), contract.totalPages());

        return ApiResponse.success(new InsuredContractListResponse(page, renewalTargetCount));

    }

    @GetMapping("/{id}")
    public ApiResponse<InsuredContractDetailResponse> getDetail(@PathVariable Integer id) {

        InsuredContractDetail detail = insuredService.getDetail(id);

        // String certificateUrl = null;
        // InsuredSubscriptionInfo subscription = detail.subscription();
        // String joinCheck = subscription.joinCheck();
        // String insuranceCompany = subscription.insuranceCompany();
        //
        // // 1. 결제 완료('Y')
        // // 2. 질권번호(prctrNo) 존재
        // // 3. 계약 상태가 가입완료('Y') 또는 보험만료('X')
        // // 4. 보험사가 '메리츠'인 경우
        // if ("Y".equals(subscription.payYn()) && StringUtils.hasText(detail.prctrNo())
        // && ("Y".equals(joinCheck) || "X".equals(joinCheck))
        // && (insuranceCompany != null && insuranceCompany.contains("메리츠"))) {
        //
        // certificateUrl = meritzService.getLink4(detail.prctrNo());
        // }

        return ApiResponse.success(InsuredContractDetailResponse.of(detail));
    }

    @GetMapping("/contract/excel")
    public void downloadExcel(@ModelAttribute InsuredSearchRequest request, HttpServletResponse response,
            @LoginAdmin AdminUser admin) throws IOException {

        InsuredSearchCondition condition = request.toInsuredSearchCondition();

        // 1. 보험사명 처리 (null 또는 빈값일 경우 "전체")
        String insuranceCompanyName = StringUtils.hasText(request.insuranceCompany()) ? request.insuranceCompany()
                : "전체";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String datetimeFormatter = request.startDate().format(formatter) + " ~ " + request.endDate().format(formatter);
        // 2. 파일명 생성 (가입리스트_보험사명_날짜.xlsx)
        String fileName = String.format("가입리스트_%s_%s.xlsx", insuranceCompanyName, datetimeFormatter);

        // 3. 한글 파일명 인코딩 (브라우저 깨짐 방지 및 표준 준수)
        String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // RFC 5987 표준: filename* 속성을 추가하여 UTF-8 파일명을 명시적으로 전달
        String contentDisposition = String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", encodedFileName,
                encodedFileName);
        response.setHeader("Content-Disposition", contentDisposition);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // 4. 서비스 호출
        insuredService.downloadExcel(request.insuranceCompany(), condition, response.getOutputStream());

    }

    @PutMapping("/{id}")
    public ApiResponse<ResultType> modify(@PathVariable Integer id, @RequestBody InsuredModifyRequest request,
            @LoginAdmin AdminUser admin) {
        // 서비스 레이어에 수정을 위임
        // (ID와 함께 가입자/계약정보 Record를 전달)
        insuredService.modify(id, request.insuredInfo(), request.contract(), request.location(), request.subscription(),
                request.payment(), request.memoContent(), admin.userId());

        return ApiResponse.success(ResultType.SUCCESS);
    }

    @PostMapping("/contract")
    public ApiResponse<ResultType> register(@RequestBody InsuredRegisterRequest request, @LoginAdmin AdminUser admin) {

        // Service는 비즈니스 흐름만 관장 (등록 -> 로그/이벤트 발행)
        insuredService.register(request.insuredInfo(), request.contractInfo(), request.location(),
                request.subscription(), request.payment(), request.memoContent(), admin.userId());

        return ApiResponse.success(ResultType.SUCCESS);
    }

    // @PostMapping("/contract/free/upload")
    // public ApiResponse<FreeContractUploadResponse> uploadFreeContract(@RequestPart
    // MultipartFile file,
    // @LoginAdmin AdminUser admin) {
    // UpdateCount stats = insuredService.updateFreeContracts(file);
    // return ApiResponse.success(FreeContractUploadResponse.of(stats));
    // }

    @PostMapping("/contract/free/upload")
    public ApiResponse<FreeContractUploadResponse> uploadUnifiedFreeContract(@RequestPart MultipartFile file,
            @LoginAdmin AdminUser admin) {
        UpdateCount stats = insuredService.updateUnifiedFreeContracts(file);
        return ApiResponse.success(FreeContractUploadResponse.of(stats));
    }

    @PostMapping("/{id}/notification")
    public ApiResponse<ResultType> sendNotification(@PathVariable Integer id,
            @RequestBody NotificationSendRequest request, @LoginAdmin AdminUser admin) {
        // 1. 계약 상세 정보 조회 (Service 호출)
        InsuredContractDetail detail = insuredService.getDetail(id);

        // 2. 알림 유형에 따른 타겟 URL 결정 (Controller에서 조합)
        String targetUrl = "";
        if (request.type() == MailType.REJOIN) {
            // 재가입 URL: referIdx 활용
            targetUrl = "http://pungsu.tpakorea.com/rejoin/feeGuide?idx=" + detail.referIdx();
        }
        else if (request.type() == MailType.CERTIFICATE) {

            // targetUrl = meritzService.getLink4(detail.prctrNo());
            targetUrl = request.certificateUrl();
        }

        // 3. 취합된 정보로 알림 발송 명령 (Service 호출)
        insuredService.send(detail, request.type(), targetUrl, admin.userId());

        return ApiResponse.success(ResultType.SUCCESS);

    }

    // @PostMapping("/bulk-update/paid-conversion")
    // public ApiResponse<String> bulkUpdateToPaid(@RequestPart("file") MultipartFile
    // file) {
    // // 1. 파일 검증 (확장자 등)
    //
    // // 2. 비동기 서비스 호출 (결과를 기다리지 않고 즉시 리턴)
    // insuredService.processBulkUpdateAsync(file);
    //
    // // 3. 즉시 응답 반환
    // return ApiResponse.success("파일이 업로드되었습니다. 처리가 완료되면 알림을 드립니다.");
    // }

    @GetMapping("/bulk-notification/preview")
    public ApiResponse<BulkNotificationPreview> getBulkNotificationPreview(
            @ModelAttribute InsuredSearchRequest request) {
        InsuredSearchCondition condition = request.toInsuredSearchCondition();
        BulkNotificationPreview preview = insuredService.getBulkNotificationPreview(condition);
        return ApiResponse.success(preview);
    }

    @PostMapping("/bulk-notification/send")
    public ApiResponse<BulkNotificationSendResponse> sendBulkNotification(
            @RequestBody BulkNotificationSendRequest request, @LoginAdmin AdminUser admin) {
        InsuredSearchCondition condition = InsuredSearchCondition.builder()
            .dateType(request.dateType())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .account(request.account())
            .path(request.path())
            .insuranceCompany(request.insuranceCompany())
            .keyword(request.keyword())
            .build();
        int totalCount = insuredService.sendBulkRenewalNotifications(condition, request.statuses(), admin.userId());
        return ApiResponse.success(new BulkNotificationSendResponse(totalCount, "발송이 시작되었습니다."));
    }

    @GetMapping("/{id}/renewal-history")
    public ApiResponse<RenewalHistoryResponse> getRenewalHistory(@PathVariable Integer id) {
        List<RenewalHistory> histories = renewalGroupService.getRenewalHistory(id);
        return ApiResponse.success(RenewalHistoryResponse.of(histories));
    }

    // 만기임박 대상 발송 트리거 (테스트용)
    @PostMapping("/trigger-renewal-check")
    public ApiResponse<String> triggerRenewalCheck() {
        insuredService.sendRenewalNotifications();
        return ApiResponse.success("만기임박 대상자 발송 트리거 완료");
    }

    // 3. 보험시작일 기준 일괄 발송 (예: startDate=2023-03-31)
    @PostMapping("/trigger-renewal-by-start-date")
    public ApiResponse<String> triggerByStartDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        int count = insuredService.sendRenewalNotificationsByStartDate(startDate);
        return ApiResponse.success(String.format("보험시작일 %s 대상 %d건 발송 완료", startDate, count));
    }

}
