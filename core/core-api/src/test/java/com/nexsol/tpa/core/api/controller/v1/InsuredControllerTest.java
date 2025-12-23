package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuredModifyRequest;
import com.nexsol.tpa.core.domain.*;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InsuredControllerTest extends RestDocsTest {

    private InsuredService insuredService;

    private MeritzService meritzService;

    @BeforeEach
    public void setUp() {
        insuredService = mock(InsuredService.class);
        meritzService = mock(MeritzService.class);
        mockMvc = mockController(new InsuredController(insuredService, meritzService));
    }

    @Test
    @DisplayName("풍수해 가입신청내역 조회 API 문서화")
    void getContracts() throws Exception {
        // given: Mock 데이터 준비 (도메인 객체)
        InsuredContract mockContract = InsuredContract.builder()
            .id(1)
            .payMethod("CARD")
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
            .build();
        DomainPage<InsuredContract> mockPage = new DomainPage<>(List.of(mockContract), true);

        given(insuredService.getList(any(InsuredSearchCondition.class), any(OffsetLimit.class))).willReturn(mockPage);

        // when & then
        mockMvc
            .perform(get("/v1/insured/contract").contentType(MediaType.APPLICATION_JSON)
                .param("payYn", "Y")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-12-31")
                .param("keyword", "테스트")
                .param("offset", "0")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-contract-list",
                    // 1. 쿼리 파라미터 문서화
                    queryParameters(parameterWithName("payYn").description("결제 여부 (Y/N, 선택)").optional(),
                            parameterWithName("startDate").description("조회 시작일 (yyyy-MM-dd, 선택)").optional(),
                            parameterWithName("endDate").description("조회 종료일 (yyyy-MM-dd, 선택)").optional(),
                            parameterWithName("keyword").description("검색어 (사업자번호/명/연락처, 선택)").optional(),
                            parameterWithName("offset").description("페이지 오프셋 (기본값 0)").optional(),
                            parameterWithName("limit").description("페이지 크기 (기본값 10)").optional()),
                    // 2. 응답 필드 문서화
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("가입신청 내역 리스트"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("계약 ID"),
                            fieldWithPath("data.content[].payMethod").type(JsonFieldType.STRING).description("결제 구분"),
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

                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

    @Test
    @DisplayName("풍수해 계약 상세보기 조회 API 문서화")
    void getDetail() throws Exception {
        // given
        Integer id = 1;
        String mockCertificateUrl = "https://meritz.com/cert/12345"; // 테스트용 가입확인서 URL

        // 1. 핵심 도메인 정보(InsuredContractDetail) 준비
        InsuredContractDetail response = InsuredContractDetail.builder()
            .id(id.intValue())
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
                .floor("1층")
                .structure("철근콘크리트")
                .prctrNo("PRC12345") // MeritzService 조회에 사용될 키값
                .pnu("24214214124124124")
                .build())
            .contractInfo(InsuredContractInfo.builder()
                .joinCk("가입완료")
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

        // 2. 각 서비스의 Mocking 설정
        given(insuredService.getDetail(id)).willReturn(response);
        given(meritzService.getLink4("PRC12345")).willReturn(mockCertificateUrl);

        // when & then
        mockMvc.perform(get("/v1/insured/{id}", id).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("admin-insured-detail", pathParameters(parameterWithName("id").description("계약 PK ID")),
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("계약 ID"),

                            // data.insuredInfo 상세 필드
                            fieldWithPath("data.insuredInfo.companyName").type(JsonFieldType.STRING).description("상호명"),
                            fieldWithPath("data.insuredInfo.name").type(JsonFieldType.STRING).description("성명"),
                            fieldWithPath("data.insuredInfo.businessNumber").type(JsonFieldType.STRING)
                                .description("사업자 번호"),
                            fieldWithPath("data.insuredInfo.birthDate").type(JsonFieldType.STRING).description("생년월일"),
                            fieldWithPath("data.insuredInfo.email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("data.insuredInfo.phoneNumber").type(JsonFieldType.STRING)
                                .description("전화번호"),
                            fieldWithPath("data.insuredInfo.address").type(JsonFieldType.STRING).description("주소"),
                            fieldWithPath("data.insuredInfo.category").type(JsonFieldType.STRING).description("업종"),
                            fieldWithPath("data.insuredInfo.tenant").type(JsonFieldType.STRING).description("임차여부"),
                            fieldWithPath("data.insuredInfo.floor").type(JsonFieldType.STRING).description("층수"),
                            fieldWithPath("data.insuredInfo.structure").type(JsonFieldType.STRING).description("건물구조"),
                            fieldWithPath("data.insuredInfo.prctrNo").type(JsonFieldType.STRING).description("질권번호"),
                            fieldWithPath("data.insuredInfo.pnu").type(JsonFieldType.STRING).description("PNU코드"),

                            // data.contractInfo 상세 필드
                            fieldWithPath("data.contractInfo.joinCk").type(JsonFieldType.STRING).description("가입 상태"),
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

                            // 새로 조합되어 추가된 필드
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
                        "floor": "1층",
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
                    }
                }
                """;

        given(insuredService.modify(eq(id), any(), any())).willReturn(id);

        // when & then
        mockMvc.perform(put("/v1/insured/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestJson))
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
                            fieldWithPath("insuredInfo.floor").description("층수"),
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
                            fieldWithPath("contractInfo.totalLocalGovernmentCost").description("지자체지원 보험료")),
                    responseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/FAIL)"),
                            fieldWithPath("data").type(JsonFieldType.STRING).description("결과 데이터 (SUCCESS)"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

}