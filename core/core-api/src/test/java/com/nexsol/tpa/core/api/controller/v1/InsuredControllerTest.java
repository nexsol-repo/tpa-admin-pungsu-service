package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuredRegisterRequest;
import com.nexsol.tpa.core.domain.*;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.test.api.RestDocsTest;
import com.nexsol.tpa.web.auth.AdminUserProvider;
import com.nexsol.tpa.web.auth.LoginAdmin;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InsuredControllerTest extends RestDocsTest {

    private InsuredService insuredService;

    private MeritzService meritzService;

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
                return new AdminUserProvider(1L, "MASTER", Collections.emptySet());
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
        // given
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
            .joinCk("Y")
            .account("TPA KOREA")
            .path("TPA KOREA")
            .payYn("Y")
            .referIdx("20251217144520zmhadj")
            .build();
        DomainPage<InsuredContract> mockPage = new DomainPage<>(List.of(mockContract), true);

        given(insuredService.getList(any(InsuredSearchCondition.class), any(OffsetLimit.class))).willReturn(mockPage);

        // when & then
        mockMvc
            .perform(get("/v1/admin/pungsu/contract").contentType(MediaType.APPLICATION_JSON)
                .param("payYn", "Y")
                .param("status", "Y")
                .param("path", "TPA KOREA")
                .param("account", "TPA KOREA")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-12-31")
                .param("insuranceCompany", "메리츠")
                .param("keyword", "테스트")
                .param("offset", "0")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-contract-list", queryParameters(
                    parameterWithName("payYn").description("가입유형 ? Y 유료 : 무료 ").optional(),
                    parameterWithName("status").description(
                            "계약 진행상태(W:가입진행, N:보온접수완료, R: 보험사 접수, Y:가입완료(유효), D:가입반려(보험사 중복), E:가입반려(주소오류), F:결제실패(보험사), X:보험만료)")
                        .optional(),
                    parameterWithName("path").description("채널").optional(),
                    parameterWithName("account").description("제휴사").optional(),
                    parameterWithName("insuranceCompany").description("보험사").optional(),
                    parameterWithName("startDate").description("조회 시작일 (yyyy-MM-dd, 선택)").optional(),
                    parameterWithName("endDate").description("조회 종료일 (yyyy-MM-dd, 선택)").optional(),
                    parameterWithName("keyword").description("검색어 (사업자번호/명/연락처, 선택)").optional(),
                    parameterWithName("offset").description("페이지 오프셋 (기본값 0)").optional(),
                    parameterWithName("limit").description("페이지 크기 (기본값 10)").optional()),
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("가입신청 내역 리스트"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("계약 ID"),
                            fieldWithPath("data.content[].businessNumber").type(JsonFieldType.STRING)
                                .description("사업자 번호"),
                            fieldWithPath("data.content[].companyName").type(JsonFieldType.STRING).description("사업장명"),
                            fieldWithPath("data.content[].address").type(JsonFieldType.STRING).description("사업장 주소"),
                            fieldWithPath("data.content[].phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                            fieldWithPath("data.content[].applicationDate").type(JsonFieldType.STRING)
                                .description("가입일"),
                            fieldWithPath("data.content[].insuranceCompany").type(JsonFieldType.STRING)
                                .description("보험사"),
                            fieldWithPath("data.content[].insuranceStartDate").type(JsonFieldType.STRING)
                                .description("보험 시작일"),
                            fieldWithPath("data.content[].insuranceEndDate").type(JsonFieldType.STRING)
                                .description("보험 종료일"),
                            fieldWithPath("data.content[].isRenewalTarget").type(JsonFieldType.BOOLEAN)
                                .description("갱신 대상 여부"),
                            fieldWithPath("data.content[].joinCk").type(JsonFieldType.STRING).description("상태"),
                            fieldWithPath("data.content[].account").type(JsonFieldType.STRING).description("제휴사"),
                            fieldWithPath("data.content[].path").type(JsonFieldType.STRING).description("채널"),
                            fieldWithPath("data.content[].payYn").type(JsonFieldType.STRING)
                                .description("가입유형 ? Y 유료 : 무료 "),
                            fieldWithPath("data.content[].referIdx").type(JsonFieldType.STRING)
                                .description("referIdx 재가입 url 시 사용"),
                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

    @Test
    @DisplayName("풍수해 계약 상세보기 조회 API 문서화")
    void getDetail() throws Exception {
        // given
        Integer id = 1;
        String mockCertificateUrl = "https://meritz.com/cert/12345";

        InsuredContractDetail response = InsuredContractDetail.builder()
            .id(id)
            .insuredInfo(InsuredInfo.builder()
                .companyName("테스트상호")
                .name("홍길동")
                .businessNumber("1234567890")
                .birthDate("19900101")
                .email("test@nexsol.com")
                .phoneNumber("01012345678")
                .address("서울시 강남구 테헤란로")
                .category("음식점")
                .tenant("임차인")
                .groundFloorCd("0")
                .groundFloor(5)
                .underGroundFloor(1)
                .subFloor("1")
                .endSubFloor("5")
                .structure("철근콘크리트")
                .prctrNo("PRC12345")
                .pnu("24214214124124124")
                .referIdx("20251217144520zmhadj")
                .build())
            .contractInfo(InsuredContractInfo.builder()
                .joinCk("가입완료")
                .payYn("Y")
                .account("TPA KOREA")
                .path("TPA KOREA")
                .isRenewalTarget(false)
                .insuranceStartDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .insuranceEndDate(LocalDateTime.of(2025, 12, 31, 23, 59))
                .insuranceCompany("메리츠화재")
                .insuranceNumber("POL-12345")
                .insuranceCostBld(100000000L)
                .insuranceCostFcl(50000000L)
                .insuranceCostMach(20000000L)
                .insuranceCostInven(30000000L)
                .insuranceCostShopSign(5000000L)
                .insuranceCostDeductible(100000L)
                .totalInsuranceCost(500000L)
                .totalInsuranceMyCost(100000L)
                .totalGovernmentCost(300000L)
                .totalLocalGovernmentCost(100000L)
                .build())
            .build();

        given(insuredService.getDetail(id)).willReturn(response);
        given(meritzService.getLink4("PRC12345")).willReturn(mockCertificateUrl);

        mockMvc.perform(get("/v1/admin/pungsu/{id}", id).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-detail", pathParameters(parameterWithName("id")
                .description("계약 PK ID")), responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("계약 ID"),

                        fieldWithPath("data.insuredInfo.companyName").type(JsonFieldType.STRING).description("상호명"),
                        fieldWithPath("data.insuredInfo.name").type(JsonFieldType.STRING).description("성명"),
                        fieldWithPath("data.insuredInfo.businessNumber").type(JsonFieldType.STRING)
                            .description("사업자 번호"),
                        fieldWithPath("data.insuredInfo.birthDate").type(JsonFieldType.STRING).description("생년월일"),
                        fieldWithPath("data.insuredInfo.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.insuredInfo.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                        fieldWithPath("data.insuredInfo.address").type(JsonFieldType.STRING).description("주소"),
                        fieldWithPath("data.insuredInfo.category").type(JsonFieldType.STRING).description("업종"),
                        fieldWithPath("data.insuredInfo.tenant").type(JsonFieldType.STRING).description("임차여부"),
                        fieldWithPath("data.insuredInfo.groundFloorCd").type(JsonFieldType.STRING)
                            .description("사업장 지하소재여부 코드(0: 지하, 1: 지상(1층), 2: 지상(그외)"),
                        fieldWithPath("data.insuredInfo.groundFloor").type(JsonFieldType.NUMBER)
                            .description("건물 지상 층수 정보"),
                        fieldWithPath("data.insuredInfo.underGroundFloor").type(JsonFieldType.NUMBER)
                            .description("건물 지하 층수 정보"),
                        fieldWithPath("data.insuredInfo.subFloor").type(JsonFieldType.STRING).description("사업장 시작 층수"),
                        fieldWithPath("data.insuredInfo.endSubFloor").type(JsonFieldType.STRING)
                            .description("사업장 끝 층수"),
                        fieldWithPath("data.insuredInfo.structure").type(JsonFieldType.STRING).description("건물구조"),
                        fieldWithPath("data.insuredInfo.prctrNo").type(JsonFieldType.STRING).description("질권번호"),
                        fieldWithPath("data.insuredInfo.pnu").type(JsonFieldType.STRING).description("PNU코드"),
                        fieldWithPath("data.insuredInfo.referIdx").type(JsonFieldType.STRING)
                            .description("재가입 url 시 사용"),

                        fieldWithPath("data.contractInfo.joinCk").type(JsonFieldType.STRING).description("가입 상태"),
                        fieldWithPath("data.contractInfo.payYn").type(JsonFieldType.STRING)
                            .description("가입 유형 ? Y 유료 : 무료"),
                        fieldWithPath("data.contractInfo.account").type(JsonFieldType.STRING).description("제휴사"),
                        fieldWithPath("data.contractInfo.path").type(JsonFieldType.STRING).description("채널"),
                        fieldWithPath("data.contractInfo.isRenewalTarget").type(JsonFieldType.BOOLEAN)
                            .description("갱신 대상 여부"),
                        fieldWithPath("data.contractInfo.insuranceStartDate").type(JsonFieldType.STRING)
                            .description("보험 시작일"),
                        fieldWithPath("data.contractInfo.insuranceEndDate").type(JsonFieldType.STRING)
                            .description("보험 종료일"),
                        fieldWithPath("data.contractInfo.insuranceCompany").type(JsonFieldType.STRING)
                            .description("보험사"),
                        fieldWithPath("data.contractInfo.insuranceNumber").type(JsonFieldType.STRING)
                            .description("증권번호"),
                        fieldWithPath("data.contractInfo.insuranceCostBld").type(JsonFieldType.NUMBER)
                            .description("건물 가입금액"),
                        fieldWithPath("data.contractInfo.insuranceCostFcl").type(JsonFieldType.NUMBER)
                            .description("시설/집기 가입금액"),
                        fieldWithPath("data.contractInfo.insuranceCostMach").type(JsonFieldType.NUMBER)
                            .description("기계 가입금액"),
                        fieldWithPath("data.contractInfo.insuranceCostInven").type(JsonFieldType.NUMBER)
                            .description("재고자산 가입금액"),
                        fieldWithPath("data.contractInfo.insuranceCostShopSign").type(JsonFieldType.NUMBER)
                            .description("야외간판 가입금액"),
                        fieldWithPath("data.contractInfo.insuranceCostDeductible").type(JsonFieldType.NUMBER)
                            .description("개인 부담금"),
                        fieldWithPath("data.contractInfo.totalInsuranceCost").type(JsonFieldType.NUMBER)
                            .description("총 보험료"),
                        fieldWithPath("data.contractInfo.totalInsuranceMyCost").type(JsonFieldType.NUMBER)
                            .description("본인부담 보험료"),
                        fieldWithPath("data.contractInfo.totalGovernmentCost").type(JsonFieldType.NUMBER)
                            .description("정부지원 보험료"),
                        fieldWithPath("data.contractInfo.totalLocalGovernmentCost").type(JsonFieldType.NUMBER)
                            .description("지자체지원 보험료"),

                        fieldWithPath("data.certificateUrl").type(JsonFieldType.STRING)
                            .description("가입확인서 PDF 다운로드 URL")
                            .optional(),

                        fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

    @Test
    @DisplayName("풍수해 계약 수정 API 문서화")
    void modify() throws Exception {
        // given
        Integer id = 1;
        Long adminId = 1L; // 테스트용 Admin ID
        String requestJson = """
                {
                    "insuredInfo": {
                        "companyName": "수정상호",
                        "name": "홍길동",
                        "businessNumber": "1234567890",
                        "birthDate": "19900101",
                        "email": "test@nexsol.com",
                        "phoneNumber": "01012345678",
                        "address": "서울시 강남구 테헤란로",
                        "category": "음식점",
                        "tenant": "임차인",
                        "groundFloorCd": "Y",
                        "groundFloor":5,
                        "underGroundFloor":1,
                        "subFloor":"1",
                        "endSubFloor":"5",
                        "structure": "철근콘크리트",
                        "prctrNo": "PRC12345"
                    },
                    "contractInfo": {
                        "joinCk": "가입완료",
                        "isRenewalTarget": false,
                        "insuranceStartDate": "2025-01-01T00:00:00",
                        "insuranceEndDate": "2025-12-31T23:59:59",
                        "insuranceCompany": "메리츠화재",
                        "insuranceNumber": "POL-12345",
                        "insuranceCostBld": 100000000,
                        "insuranceCostFcl": 50000000,
                        "insuranceCostMach": 20000000,
                        "insuranceCostInven": 30000000,
                        "insuranceCostShopSign": 5000000,
                        "insuranceCostDeductible": 100000,
                        "totalInsuranceCost": 500000,
                        "totalInsuranceMyCost": 100000,
                        "totalGovernmentCost": 300000,
                        "totalLocalGovernmentCost": 100000
                    },
                    "memoContent": "수정 사유: 사업자 요청"
                }
                """;

        // [중요] adminId(1L) 전달 여부 검증
        given(insuredService.modify(eq(id), any(), any(), any(), eq(adminId))).willReturn(id);

        // when & then
        mockMvc.perform(put("/v1/admin/pungsu/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-modify", pathParameters(parameterWithName("id").description("계약 PK ID")),
                    requestFields(
                            // insuredInfo
                            fieldWithPath("insuredInfo.companyName").description("상호명"),
                            fieldWithPath("insuredInfo.name").description("성명"),
                            fieldWithPath("insuredInfo.businessNumber").description("사업자 번호"),
                            fieldWithPath("insuredInfo.birthDate").description("생년월일"),
                            fieldWithPath("insuredInfo.email").description("이메일"),
                            fieldWithPath("insuredInfo.phoneNumber").description("전화번호"),
                            fieldWithPath("insuredInfo.address").description("주소"),
                            fieldWithPath("insuredInfo.category").description("업종"),
                            fieldWithPath("insuredInfo.tenant").description("임차여부"),
                            fieldWithPath("insuredInfo.groundFloorCd")
                                .description("사업장 지하소재여부 코드(0: 지하, 1: 지상(1층), 2: 지상(그외)"),
                            fieldWithPath("insuredInfo.groundFloor").description("건물 지상 층수 정보"),
                            fieldWithPath("insuredInfo.underGroundFloor").description("건물 지하 층수 정보"),
                            fieldWithPath("insuredInfo.subFloor").description("사업장 시작 층수"),
                            fieldWithPath("insuredInfo.endSubFloor").description("사업장 끝 층수"),
                            fieldWithPath("insuredInfo.structure").description("건물구조"),
                            fieldWithPath("insuredInfo.prctrNo").description("질권번호"),
                            // contractInfo
                            fieldWithPath("contractInfo.joinCk").description("가입 상태"),
                            fieldWithPath("contractInfo.isRenewalTarget").description("갱신 대상 여부"),
                            fieldWithPath("contractInfo.insuranceStartDate").description("보험 시작일"),
                            fieldWithPath("contractInfo.insuranceEndDate").description("보험 종료일"),
                            fieldWithPath("contractInfo.insuranceCompany").description("보험사"),
                            fieldWithPath("contractInfo.insuranceNumber").description("증권번호"),
                            fieldWithPath("contractInfo.insuranceCostBld").description("건물 가입금액"),
                            fieldWithPath("contractInfo.insuranceCostFcl").description("시설/집기 가입금액"),
                            fieldWithPath("contractInfo.insuranceCostMach").description("기계 가입금액"),
                            fieldWithPath("contractInfo.insuranceCostInven").description("재고자산 가입금액"),
                            fieldWithPath("contractInfo.insuranceCostShopSign").description("야외간판 가입금액"),
                            fieldWithPath("contractInfo.insuranceCostDeductible").description("개인 부담금"),
                            fieldWithPath("contractInfo.totalInsuranceCost").description("총 보험료"),
                            fieldWithPath("contractInfo.totalInsuranceMyCost").description("본인부담 보험료"),
                            fieldWithPath("contractInfo.totalGovernmentCost").description("정부지원 보험료"),
                            fieldWithPath("contractInfo.totalLocalGovernmentCost").description("지자체지원 보험료"),

                            // [추가] 메모 필드 문서화
                            fieldWithPath("memoContent").description("관리자 메모 내용 (선택)").optional()),
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data").type(JsonFieldType.STRING).description("결과 데이터 (SUCCESS)"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

    @Test
    @DisplayName("풍수해 알림(문자+메일) 발송 API 문서화")
    void sendNotification() throws Exception {
        // given
        Integer id = 1;
        Long adminId = 1L;

        // objectMapper 대신 직접 JSON 문자열 작성
        String requestJson = """
                {
                    "type": "REJOIN"
                }
                """;

        // 1. 가입자 상세 정보 모킹 (referIdx 포함)
        InsuredInfo mockInfo = InsuredInfo.builder()
            .name("홍길동")
            .email("test@nexsol.com")
            .phoneNumber("01012345678")
            .referIdx("REF12345") // 재가입 URL 생성용
            .prctrNo("PRC99999") // 가입확인서 조회용
            .build();

        InsuredContractDetail mockDetail = new InsuredContractDetail(id, mockInfo, null);

        given(insuredService.getDetail(id)).willReturn(mockDetail);

        // when & then
        mockMvc
            .perform(post("/v1/admin/pungsu/{id}/notification", id).contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-notification-send", // 문서 식별자
                    pathParameters(parameterWithName("id").description("계약 PK ID")),
                    requestFields(fieldWithPath("type").description("알림 유형 (REJOIN: 재가입 안내, CERTIFICATE: 가입확인서 안내)")),
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data").type(JsonFieldType.STRING).description("결과 데이터 (SUCCESS)"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

    @Test
    @DisplayName("관리자 직접 등록(신규) 성공")
    void register_success() throws Exception {
        // given
        // 1. 직접 등록 시에는 '가입확인서(joinCk)', '증권번호(insuranceNumber)', '납입내역(payYn)'은 빈
        // 값(null)입니다.
        InsuredRegisterRequest request = InsuredRegisterRequest.builder()
            .insuredInfo(InsuredInfo.builder()
                .name("홍길동")
                .companyName("넥솔")
                .businessNumber("123-45-67890")
                .phoneNumber("010-1234-5678")
                .email("test@nexsol.co.kr")
                .birthDate("19900101")
                // 주소 정보
                .address("부산광역시 해운대구")
                .tenant("N")
                .category("음식점")
                .structure("철근콘크리트")
                .groundFloor(1)
                .underGroundFloor(0)
                .build())
            .contractInfo(InsuredContractInfo.builder()
                // [제외 필드] 직접 등록 시점에는 아직 생성되지 않은 정보들
                // .joinCk(null) // 가입확인서 (제외)
                // .insuranceNumber(null) // 증권번호 (제외)
                // .payYn(null) // 납입내역 (제외)

                // [필수 필드] 계약 기간 및 금액 정보
                .insuranceStartDate(LocalDate.of(2025, 1, 1).atStartOfDay())
                .insuranceEndDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                .totalInsuranceCost(150000L) // 총 보험료
                .totalGovernmentCost(100000L)
                .totalLocalGovernmentCost(30000L)
                .totalInsuranceMyCost(20000L)
                // 보장 내역 (가입금액)
                .insuranceCostBld(100000000L)
                .insuranceCostFcl(50000000L)
                .insuranceCostInven(20000000L)
                .build())
            .memoContent("관리자 직접 등록 건입니다.")
            .build();

        // service.register는 void 반환이므로 doNothing 처리
        doNothing().when(insuredService).register(any(), any(), any(), any());

        // when & then
        mockMvc
            .perform(post("/v1/admin/pungsu/contract").contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                // [수정 완료] 괄호 닫기 위치 수정 : writeValueAsString(request) 뒤에 ) 닫음
                .content(JsonMapper.builder().build().writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("insured-register-direct", requestFields(
                    // InsuredInfo
                    fieldWithPath("insuredInfo.name").description("피보험자 성명 (대표자명)"),
                    fieldWithPath("insuredInfo.companyName").description("상호명").optional(),
                    fieldWithPath("insuredInfo.businessNumber").description("사업자등록번호").optional(),
                    fieldWithPath("insuredInfo.phoneNumber").description("휴대전화번호").optional(),
                    fieldWithPath("insuredInfo.email").description("이메일").optional(),
                    fieldWithPath("insuredInfo.birthDate").description("생년월일").optional(),
                    fieldWithPath("insuredInfo.address").description("주소").optional(),
                    fieldWithPath("insuredInfo.tenant").description("임차여부").optional(),
                    fieldWithPath("insuredInfo.category").description("업종").optional(),
                    fieldWithPath("insuredInfo.structure").description("건물구조").optional(),
                    fieldWithPath("insuredInfo.groundFloor").description("지상층수").optional(),
                    fieldWithPath("insuredInfo.underGroundFloor").description("지하층수").optional(),

                    fieldWithPath("insuredInfo.referIdx").description("관리 번호 (신규 등록 시 null)").ignored(),

                    // 나머지 InsuredInfo 필드들은 무시 (너무 많을 경우)
                    fieldWithPath("insuredInfo.prctrNo").ignored(), fieldWithPath("insuredInfo.pnu").ignored(),
                    fieldWithPath("insuredInfo.groundFloorCd").ignored(),
                    fieldWithPath("insuredInfo.subFloor").ignored(), fieldWithPath("insuredInfo.endSubFloor").ignored(),

                    // ContractInfo
                    // [핵심] 제외된 필드는 description 대신 optional() 혹은 ignored() 처리
                    fieldWithPath("contractInfo.insuranceNumber").description("증권번호 (신규 등록시 null)").optional(),
                    fieldWithPath("contractInfo.joinCk").description("가입확인서 상태 (신규 등록시 null)").optional(),
                    fieldWithPath("contractInfo.payYn").description("납입 상태 (신규 등록시 null)").optional(),

                    // 필수 필드 문서화
                    fieldWithPath("contractInfo.insuranceStartDate").description("보험 시작일"),
                    fieldWithPath("contractInfo.insuranceEndDate").description("보험 종료일"),
                    fieldWithPath("contractInfo.totalInsuranceCost").description("총 보험료"),
                    fieldWithPath("contractInfo.totalGovernmentCost").description("정부지원금").optional(),
                    fieldWithPath("contractInfo.totalLocalGovernmentCost").description("지자체지원금").optional(),
                    fieldWithPath("contractInfo.totalInsuranceMyCost").description("자부담금").optional(),

                    // 보장 내역
                    fieldWithPath("contractInfo.insuranceCostBld").description("건물 가입금액").optional(),
                    fieldWithPath("contractInfo.insuranceCostFcl").description("시설 가입금액").optional(),
                    fieldWithPath("contractInfo.insuranceCostInven").description("재고자산 가입금액").optional(),

                    fieldWithPath("contractInfo.isRenewalTarget").description("갱신 대상 여부 (기본값 false)").ignored(),
                    fieldWithPath("contractInfo.insuranceCompany").description("보험사 코드 (신규 등록 시 미정일 수 있음)").ignored(),

                    // 나머지 ContractInfo 필드 무시
                    fieldWithPath("contractInfo.insuranceCostDeductible").ignored(),
                    fieldWithPath("contractInfo.insuranceCostMach").ignored(),
                    fieldWithPath("contractInfo.insuranceCostShopSign").ignored(),
                    fieldWithPath("contractInfo.account").ignored(), // 필요시 추가
                    fieldWithPath("contractInfo.path").ignored(), // 필요시 추가

                    // Memo
                    fieldWithPath("memoContent").description("관리자 메모")),
                    responseFields(fieldWithPath("result").description("성공 여부 (SUCCESS)"),
                            fieldWithPath("data").description("데이터 (null)").optional(),
                            fieldWithPath("error").description("에러 정보 (null)").optional())));
    }

}