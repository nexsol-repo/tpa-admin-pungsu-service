package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.InsuredContract;
import com.nexsol.tpa.core.domain.InsuredSearchCondition;
import com.nexsol.tpa.core.domain.InsuredService;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InsuredControllerTest extends RestDocsTest {

    private InsuredService insuredService;

    @BeforeEach
    public void setUp() {
        insuredService = mock(InsuredService.class);
        mockMvc = mockController(new InsuredController(insuredService));
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
            .build();
        DomainPage<InsuredContract> mockPage = new DomainPage<>(List.of(mockContract), true);

        given(insuredService.getList(any(InsuredSearchCondition.class), any(OffsetLimit.class))).willReturn(mockPage);

        // when & then
        mockMvc
            .perform(get("/v1/admin/pungsu/insured/contract").contentType(MediaType.APPLICATION_JSON)
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

                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                            fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
    }

}