package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuredModifyRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuredRegisterRequest;
import com.nexsol.tpa.core.api.controller.v1.request.NotificationSendRequest;
import com.nexsol.tpa.core.domain.*;

import com.nexsol.tpa.core.domain.LoginAdmin;
import com.nexsol.tpa.core.enums.MailType;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.test.api.RestDocsTest;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InsuredControllerTest extends RestDocsTest {

    private InsuredService insuredService;

    private MeritzService meritzService;

    private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        insuredService = mock(InsuredService.class);
        meritzService = mock(MeritzService.class);

        HandlerMethodArgumentResolver loginAdminResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(LoginAdmin.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                // 테스트용 관리자 정보 (ID: 1L) 반환
                return new AdminUser(1L, "MASTER");
            }
        };

        // standaloneSetup을 직접 호출하여 customArgumentResolvers 등록
        mockMvc = MockMvcBuilders.standaloneSetup(new InsuredController(insuredService, meritzService))
            .setCustomArgumentResolvers(loginAdminResolver)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("풍수해 가입신청내역 조회 API 문서화")
    void getContracts() throws Exception {
        InsuredContract mockContract = InsuredContract.builder()
            .id(1)
            .businessNumber("123-45-67890")
            .companyName("테스트 사업장")
            .address("서울시 강남구 테헤란로")
            .phoneNumber("010-1234-5678")
            .applicationDate(LocalDateTime.of(2025, 12, 1, 10, 0))
            .insuranceCompany("메리츠화재")
            .insuranceStartDate(LocalDateTime.of(2025, 12, 1, 0, 0))
            .insuranceEndDate(LocalDateTime.of(2026, 11, 30, 23, 59))
            .isRenewalTarget(false)
            .joinCheck("Y")
            .account("TPA KOREA")
            .path("TPA KOREA")
            .payYn("Y")
            .referIdx("20251217144520zmhadj")
            .build();
        DomainPage<InsuredContract> mockPage = new DomainPage<>(List.of(mockContract), true, 20, 2);

        given(insuredService.getList(any(InsuredSearchCondition.class), any(OffsetLimit.class))).willReturn(mockPage);

        mockMvc
            .perform(get("/v1/admin/pungsu/contract").param("payYn", "Y")
                .param("insuranceCompany", "메리츠화재")
                .param("keyword", "테스트")
                .param("offset", "0")
                .param("limit", "10")
                .param("sortBy", "insuranceStartDate")
                .param("direction", "ASC")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andDo(document("admin-insured-contract-list",
                    queryParameters(parameterWithName("payYn").description("가입유형 (Y:유료, N:무료)").optional(),
                            parameterWithName("status").description("계약 상태 코드").optional(),
                            parameterWithName("keyword").description("검색어").optional(),
                            parameterWithName("offset").description("오프셋").optional(),
                            parameterWithName("limit").description("리미트").optional(),
                            parameterWithName("limit").description("리미트").optional(),
                            parameterWithName("limit").description("리미트").optional(),
                            parameterWithName("sortBy").description("예:insuranceStartDate").optional(),
                            parameterWithName("direction").description("ASC 또는 DESC").optional(),
                            parameterWithName("path").description("채널").optional(),
                            parameterWithName("insuranceCompany").description("보험사").optional(),
                            parameterWithName("startDate").description("시작일").optional(),
                            parameterWithName("endDate").description("종료일").optional()),
                    responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                            fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("내역 리스트"),
                            fieldWithPath("data.content[].id").description("ID"),
                            fieldWithPath("data.content[].businessNumber").description("사업자번호"),
                            fieldWithPath("data.content[].companyName").description("사업장명"),
                            fieldWithPath("data.content[].address").description("사업장 주소"),
                            fieldWithPath("data.content[].phoneNumber").description("전화번호"),
                            fieldWithPath("data.content[].applicationDate").description("가입신청일"),
                            fieldWithPath("data.content[].insuranceCompany").description("보험사"),
                            fieldWithPath("data.content[].insuranceStartDate").description("보험시작일"),
                            fieldWithPath("data.content[].insuranceEndDate").description("보험종료일"),
                            fieldWithPath("data.content[].isRenewalTarget").description("갱신대상여부"),
                            fieldWithPath("data.content[].joinCheck").description(
                                    "계약 진행상태(W:가입진행, N:보온접수완료, R: 보험사 접수, Y:가입완료(유효), D:가입반려(보험사 중복), E:가입반려(주소오류), F:결제실패(보험사), X:보험만료)"),
                            fieldWithPath("data.content[].account").description("제휴사"),
                            fieldWithPath("data.content[].path").description("채널"),
                            fieldWithPath("data.content[].payYn").description("결제여부"),
                            fieldWithPath("data.content[].referIdx").description("참조번호"),
                            fieldWithPath("data.content[].createdAt").description("생성일시").optional(),
                            fieldWithPath("data.hasNext").description("다음 페이지 여부"),
                            fieldWithPath("data.totalElements").description("총 item 수"),
                            fieldWithPath("data.totalPages").description("총 page 수"),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

    @Test
    @DisplayName("풍수해 계약 내역 엑셀 다운로드 API 문서화")
    void downloadExcel() throws Exception {
        // 엑셀 다운로드는 void 리턴이며 OutputStream에 데이터를 직접 씀
        doNothing().when(insuredService).downloadExcel(any(), any(), any());

        mockMvc
            .perform(get("/v1/admin/pungsu/contract/excel").param("insuranceCompany", "메리츠화재")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-12-31")
                .accept(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-contract-excel",

                    queryParameters(
                            parameterWithName("insuranceCompany").description("다운로드할 보험사 양식 (메리츠, 삼성, DB 등)")
                                .optional(),
                            parameterWithName("status").description("계약 상태 필터").optional(),
                            parameterWithName("payYn").description("결제 여부 필터").optional(),
                            parameterWithName("startDate").description("시작일").optional(),
                            parameterWithName("endDate").description("종료일").optional(),
                            parameterWithName("keyword").description("검색 키워드").optional(),
                            parameterWithName("account").description("제휴사 필터").optional(),
                            parameterWithName("path").description("채널 필터").optional())));
    }

    @Test
    @DisplayName("풍수해 계약 상세보기 조회 API 문서화")
    void getDetail() throws Exception {
        Integer id = 1;
        InsuredContractDetail response = InsuredContractDetail.builder()
            .id(id)
            .referIdx("20251217144520zmhadj")
            .insuredInfo(new InsuredInfo("홍길동", "1234567890", "19900101", "test@nexsol.com", "01012345678"))
            .contractInfo(new ContractInfo("넥솔", "9876543210", "서울시 강남구"))
            .location(BusinessLocationInfo.builder()
                .companyName("테스트상호")
                .address("서울시 강남구 테헤란로")
                .category("음식점")
                .tenant("임차인")
                .groundFloorCd("1")
                .tmYn("Y")
                .groundFloorYn("N")
                .groundFloor(5)
                .underGroundFloor(1)
                .subFloor("1")
                .endSubFloor("5")
                .pnu("PNU123")
                .prctrNo("PRC12345")
                .build())
            .subscription(InsuredSubscriptionInfo.builder()
                .joinCheck("가입완료")
                .payYn("Y")
                .insuranceCompany("메리츠화재")
                .insuranceNumber("POL-123")
                .account("TPA")
                .path("WEB")
                .isRenewalTarget(true)
                .insuranceStartDate(LocalDateTime.now())
                .insuranceEndDate(LocalDateTime.now().plusYears(1))
                .totalInsuranceCost(500000L)
                .insuranceCostBld(100000000L)
                .insuranceCostFcl(0L)
                .insuranceCostMach(0L)
                .insuranceCostInven(0L)
                .insuranceCostShopSign(0L)
                .insuranceCostDeductible(0L)
                .totalInsuranceMyCost(0L)
                .totalGovernmentCost(0L)
                .totalLocalGovernmentCost(0L)
                .build())
            .build();

        given(insuredService.getDetail(id)).willReturn(response);
        given(meritzService.getLink4(any())).willReturn("https://meritz.com/cert");

        mockMvc.perform(get("/v1/admin/pungsu/{id}", id).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-detail", pathParameters(parameterWithName("id").description("계약 PK ID")),
                    responseFields(flatten(fieldWithPath("result").description("결과"),
                            fieldWithPath("data.id").description("ID"),
                            fieldWithPath("data.referIdx").description("참조번호").optional(),
                            insuredFields("data.insuredInfo."), contractFields("data.contractInfo."),
                            locationFields("data.location."), subscriptionFields("data.subscription."),
                            fieldWithPath("data.certificateUrl").description("증권 URL").optional(),
                            fieldWithPath("error").description("에러").optional()))));
    }

    @Test
    @DisplayName("풍수해 계약 수정 API 문서화")
    void modify() throws Exception {
        Integer id = 1;
        Long adminId = 1L;

        InsuredModifyRequest request = new InsuredModifyRequest(
                new InsuredInfo("홍길동", "1234567890", "19900101", "test@nexsol.com", "01012345678"),
                new ContractInfo("넥솔", "9876543210", "서울시 강남구"),
                BusinessLocationInfo.builder()
                    .companyName("수정상호")
                    .address("주소")
                    .category("음식점")
                    .tenant("N")
                    .groundFloorCd("1")
                    .tmYn("Y")
                    .groundFloorYn("N")
                    .groundFloor(1)
                    .underGroundFloor(0)
                    .subFloor("1")
                    .endSubFloor("1")
                    .pnu("123")
                    .prctrNo("ABC")
                    .build(),
                InsuredSubscriptionInfo.builder()
                    .joinCheck("가입완료")
                    .payYn("Y")
                    .insuranceCompany("메리츠")
                    .insuranceNumber("POL-123")
                    .account("TPA")
                    .path("WEB")
                    .totalInsuranceCost(500000L)
                    .insuranceCostBld(100000000L)
                    .insuranceCostFcl(0L)
                    .insuranceCostMach(0L)
                    .insuranceCostInven(0L)
                    .insuranceCostShopSign(0L)
                    .insuranceCostDeductible(0L)
                    .totalInsuranceMyCost(0L)
                    .totalGovernmentCost(0L)
                    .totalLocalGovernmentCost(0L)
                    .build(),
                "수정 메모");

        given(insuredService.modify(eq(id), any(), any(), any(), any(), any(), eq(adminId))).willReturn(id);

        mockMvc
            .perform(put("/v1/admin/pungsu/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-modify", pathParameters(parameterWithName("id").description("계약 PK ID")),
                    requestFields(flatten(insuredFields("insuredInfo."), contractFields("contract."), // 컨트롤러
                                                                                                      // 필드명
                                                                                                      // 매칭
                            locationFields("location."), // 컨트롤러 필드명 매칭
                            subscriptionFields("subscription."), // 컨트롤러 필드명 매칭
                            fieldWithPath("memoContent").description("메모").optional())),
                    responseFields(fieldWithPath("result").description("성공 여부"),
                            fieldWithPath("data").description("결과 타입"),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

    @Test
    @DisplayName("풍수해 알림(문자+메일) 발송 API 문서화")
    void sendNotification() throws Exception {
        Integer id = 1;
        // 수정됨: NotificationSendRequest 생성자가 MailType type 하나만 받는다고 하셨으므로 수정
        NotificationSendRequest request = new NotificationSendRequest(MailType.REJOIN);

        InsuredContractDetail mockDetail = InsuredContractDetail.builder()
            .id(id)
            .referIdx("REF-123")
            .insuredInfo(new InsuredInfo("홍길동", "123-45-67890", "19900101", "test@co.kr", "010-1234-5678"))
            .build();

        given(insuredService.getDetail(id)).willReturn(mockDetail);

        mockMvc
            .perform(post("/v1/admin/pungsu/{id}/notification", id).contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-notification-send",
                    pathParameters(parameterWithName("id").description("계약 PK ID")),
                    requestFields(fieldWithPath("type").description("알림 유형 (REJOIN, CERTIFICATE)")
                    // record 필드가 type 하나뿐이므로 content, serviceType 제거
                    ),
                    responseFields(fieldWithPath("result").description("성공 여부"),
                            fieldWithPath("data").description("결과 데이터").optional(),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

    @Test
    @DisplayName("관리자 직접 등록(신규) 성공")
    void register_success() throws Exception {
        InsuredRegisterRequest request = InsuredRegisterRequest.builder()
            .insuredInfo(new InsuredInfo("홍길동", "123-45-67890", "19900101", "test@co.kr", "010-1234-5678"))
            .contractInfo(new ContractInfo("계약자명", "111-22-33333", "서울"))
            .location(BusinessLocationInfo.builder()
                .companyName("넥솔")
                .address("부산")
                .tmYn("N")
                .groundFloorYn("Y")
                .groundFloor(1)
                .underGroundFloor(0)
                .tenant("Y")
                .groundFloorCd("1")
                .build())
            .subscription(InsuredSubscriptionInfo.builder()
                .totalInsuranceCost(150000L)
                .insuranceStartDate(LocalDateTime.now())
                .insuranceEndDate(LocalDateTime.now().plusYears(1))
                .insuranceNumber("POL-123")
                .account("TPA")
                .path("ADMIN")
                .insuranceCostBld(100000000L)
                .insuranceCostFcl(0L)
                .insuranceCostMach(0L)
                .insuranceCostInven(0L)
                .insuranceCostShopSign(0L)
                .insuranceCostDeductible(0L)
                .totalInsuranceMyCost(0L)
                .totalGovernmentCost(0L)
                .totalLocalGovernmentCost(0L)
                .build())
            .memoContent("신규 등록")
            .build();

        mockMvc
            .perform(post("/v1/admin/pungsu/contract").contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("insured-register-direct",
                    requestFields(flatten(insuredFields("insuredInfo."), contractFields("contractInfo."),
                            locationFields("location."), subscriptionFields("subscription."),
                            fieldWithPath("memoContent").description("메모")))));

        verify(insuredService).register(any(), any(), any(), any(), any(), any());
    }

    // --- 헬퍼 메서드 ---

    private FieldDescriptor[] locationFields(String prefix) {
        return new FieldDescriptor[] { fieldWithPath(prefix + "companyName").description("상호명"),
                fieldWithPath(prefix + "companyName").description("상호명"),
                fieldWithPath(prefix + "zipCode").description("우편번호").optional(), // 추가
                fieldWithPath(prefix + "address").description("주소"),
                fieldWithPath(prefix + "category").description("업종").optional(),
                fieldWithPath(prefix + "biztype").description("소상인 구분").optional(), // 추가
                fieldWithPath(prefix + "tenant").description("임차여부").optional(),
                fieldWithPath(prefix + "mainStrctType").description("기둥 구조").optional(), // 추가
                fieldWithPath(prefix + "mainStrctGrade").description("기둥 구조 등급").optional(), // 추가
                fieldWithPath(prefix + "roofStrctType").description("지붕 구조").optional(), // 추가
                fieldWithPath(prefix + "roofStrctGrade").description("지붕 구조 등급").optional(), // 추가
                fieldWithPath(prefix + "bldGrade").description("건물급수").optional(), // 추가
                fieldWithPath(prefix + "cityCode").description("시티 코드").optional(), // 추가
                fieldWithPath(prefix + "district").description("시도 구군").optional(), // 추가
                fieldWithPath(prefix + "tmYn").description("전통시장 여부").optional(),
                fieldWithPath(prefix + "groundFloorYn").description("지하/1층 여부").optional(),
                fieldWithPath(prefix + "groundFloorCd").description("지하소재코드").optional(),
                fieldWithPath(prefix + "groundFloor").description("지상층수").optional(),
                fieldWithPath(prefix + "underGroundFloor").description("지하층수").optional(),
                fieldWithPath(prefix + "subFloor").description("시작호수").optional(),
                fieldWithPath(prefix + "endSubFloor").description("종료호수").optional(),
                fieldWithPath(prefix + "pnu").description("PNU").optional(),
                fieldWithPath(prefix + "prctrNo").description("질권번호").optional() };
    }

    private FieldDescriptor[] subscriptionFields(String prefix) {
        return new FieldDescriptor[] { fieldWithPath(prefix + "joinCheck").description(
                "계약 진행상태(W:가입진행, N:보온접수완료, R: 보험사 접수, Y:가입완료(유효), D:가입반려(보험사 중복), E:가입반려(주소오류), F:결제실패(보험사), X:보험만료)")
            .optional(), fieldWithPath(prefix + "insuranceStartDate").description("시작일").optional(),
                fieldWithPath(prefix + "insuranceEndDate").description("종료일").optional(),
                fieldWithPath(prefix + "insuranceCompany").description("보험사").optional(),
                fieldWithPath(prefix + "insuranceNumber").description("증권번호").optional(),
                fieldWithPath(prefix + "createdAt").description("생성일시").optional(), // 추가
                fieldWithPath(prefix + "payYn").description("납입여부").optional(),
                fieldWithPath(prefix + "account").description("제휴사").optional(),
                fieldWithPath(prefix + "path").description("채널").optional(),
                fieldWithPath(prefix + "isRenewalTarget").description("갱신대상여부 (조회전용, 수정불가)")
                    .type(JsonFieldType.BOOLEAN)
                    .optional(),
                fieldWithPath(prefix + "insuranceCostBld").description("건물 가입금액").optional(),
                fieldWithPath(prefix + "insuranceCostFcl").description("시설 가입금액").optional(),
                fieldWithPath(prefix + "insuranceCostMach").description("기계 가입금액").optional(),
                fieldWithPath(prefix + "insuranceCostInven").description("재고 가입금액").optional(),
                fieldWithPath(prefix + "insuranceCostShopSign").description("간판 가입금액").optional(),
                fieldWithPath(prefix + "insuranceCostDeductible").description("자기부담금").optional(),
                fieldWithPath(prefix + "totalInsuranceCost").description("총 보험료").optional(),
                fieldWithPath(prefix + "totalInsuranceMyCost").description("자부담 보험료").optional(),
                fieldWithPath(prefix + "totalGovernmentCost").description("정부지원금").optional(),
                fieldWithPath(prefix + "totalLocalGovernmentCost").description("지자체지원금").optional() };
    }

    private List<FieldDescriptor> flatten(Object... descriptors) {
        List<FieldDescriptor> result = new ArrayList<>();
        for (Object descriptor : descriptors) {
            if (descriptor instanceof FieldDescriptor)
                result.add((FieldDescriptor) descriptor);
            else if (descriptor instanceof FieldDescriptor[])
                result.addAll(Arrays.asList((FieldDescriptor[]) descriptor));
        }
        return result;
    }

    private FieldDescriptor[] insuredFields(String prefix) {
        return new FieldDescriptor[] { fieldWithPath(prefix + "name").description("성명"),
                fieldWithPath(prefix + "businessNumber").description("사업자번호"),
                fieldWithPath(prefix + "birthDate").description("생년월일"),
                fieldWithPath(prefix + "email").description("이메일"),
                fieldWithPath(prefix + "phoneNumber").description("전화번호") };
    }

    private FieldDescriptor[] contractFields(String prefix) {
        return new FieldDescriptor[] { fieldWithPath(prefix + "contractName").description("계약자명"),
                fieldWithPath(prefix + "contractBusinessNumber").description("계약자 번호").optional(),
                fieldWithPath(prefix + "contractAddress").description("계약자 주소").optional() };
    }

}