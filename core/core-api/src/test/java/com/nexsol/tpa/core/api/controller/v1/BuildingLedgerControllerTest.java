package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.BuildingLedger;
import com.nexsol.tpa.core.domain.BuildingLedgerService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BuildingLedgerControllerTest extends RestDocsTest {

    private BuildingLedgerService buildingLedgerService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        buildingLedgerService = mock(BuildingLedgerService.class);

        // Controller 등록 및 RestDocs 설정 적용
        mockMvc = MockMvcBuilders.standaloneSetup(new BuildingLedgerController(buildingLedgerService))
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("건축물대장 상세 조회 API 문서화")
    void searchBuildingLedger() throws Exception {
        // Given
        BuildingLedger mockLedger = BuildingLedger.builder()
            .mgmBldrgstPk("11110-100002")
            .buildingName("타워팰리스")
            .dongName("A동")
            .platPlc("서울특별시 강남구 도곡동 467")
            .newPlatPlc("서울특별시 강남구 언주로30길 56")
            .sigunguCd("11680")
            .bjdongCd("11800")
            .bun("0467")
            .ji("0000")
            .platArea(1000.0)
            .archArea(500.0)
            .totArea(15000.0)
            .vlRatEstmTotArea(12000.0)
            .bcRat(50.0)
            .vlRat(1200.0)
            .height(200.0)
            .groundFloorCnt(60)
            .underGroundFloorCnt(5)
            .strctCdNm("철근콘크리트구조")
            .etcStrct("철골철근콘크리트")
            .mainPurpsCdNm("공동주택")
            .etcPurps("아파트")
            .roofCdNm("(철근)콘크리트")
            .etcRoof("평슬래브")
            .householdCnt(100)
            .familyCnt(0)
            .rideUseElvtCnt(5)
            .emgenUseElvtCnt(2)
            .useAprDay("20021025")
            .seismicDesignYn("Y")
            .seismicAbility("VII-0.124g")
            .build();

        given(buildingLedgerService.searchBuildingLedgers(anyString(), anyString(), anyString(), anyString()))
            .willReturn(List.of(mockLedger));

        // When & Then
        mockMvc
            .perform(get("/v1/admin/pungsu/building-ledger").param("sigunguCd", "11680")
                .param("bjdongCd", "11800")
                .param("bun", "0467")
                .param("ji", "0000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("admin-building-ledger-search",
                    queryParameters(parameterWithName("sigunguCd").description("시군구코드 (5자리)"),
                            parameterWithName("bjdongCd").description("법정동코드 (5자리)"),
                            parameterWithName("bun").description("번 (4자리)"),
                            parameterWithName("ji").description("지 (4자리)")),
                    responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                            fieldWithPath("data[].mgmBldrgstPk").type(JsonFieldType.STRING).description("관리건축물대장PK"),
                            fieldWithPath("data[].buildingName").type(JsonFieldType.STRING)
                                .description("건물명")
                                .optional(),
                            fieldWithPath("data[].dongName").type(JsonFieldType.STRING).description("동명").optional(),
                            fieldWithPath("data[].platPlc").type(JsonFieldType.STRING).description("대지위치 (지번 주소)"),
                            fieldWithPath("data[].newPlatPlc").type(JsonFieldType.STRING)
                                .description("도로명 대지위치")
                                .optional(),
                            fieldWithPath("data[].sigunguCd").type(JsonFieldType.STRING).description("시군구코드"),
                            fieldWithPath("data[].bjdongCd").type(JsonFieldType.STRING).description("법정동코드"),
                            fieldWithPath("data[].bun").type(JsonFieldType.STRING).description("번"),
                            fieldWithPath("data[].ji").type(JsonFieldType.STRING).description("지"),
                            fieldWithPath("data[].platArea").type(JsonFieldType.NUMBER)
                                .description("대지면적 (㎡)")
                                .optional(),
                            fieldWithPath("data[].archArea").type(JsonFieldType.NUMBER)
                                .description("건축면적 (㎡)")
                                .optional(),
                            fieldWithPath("data[].totArea").type(JsonFieldType.NUMBER)
                                .description("연면적 (㎡)")
                                .optional(),
                            fieldWithPath("data[].vlRatEstmTotArea").type(JsonFieldType.NUMBER)
                                .description("용적률산정연면적 (㎡)")
                                .optional(),
                            fieldWithPath("data[].bcRat").type(JsonFieldType.NUMBER).description("건폐율 (%)").optional(),
                            fieldWithPath("data[].vlRat").type(JsonFieldType.NUMBER).description("용적률 (%)").optional(),
                            fieldWithPath("data[].height").type(JsonFieldType.NUMBER).description("높이 (m)").optional(),
                            fieldWithPath("data[].groundFloorCnt").type(JsonFieldType.NUMBER)
                                .description("지상층수")
                                .optional(),
                            fieldWithPath("data[].underGroundFloorCnt").type(JsonFieldType.NUMBER)
                                .description("지하층수")
                                .optional(),
                            fieldWithPath("data[].strctCdNm").type(JsonFieldType.STRING)
                                .description("구조코드명")
                                .optional(),
                            fieldWithPath("data[].etcStrct").type(JsonFieldType.STRING).description("기타구조").optional(),
                            fieldWithPath("data[].mainPurpsCdNm").type(JsonFieldType.STRING)
                                .description("주용도코드명")
                                .optional(),
                            fieldWithPath("data[].etcPurps").type(JsonFieldType.STRING).description("기타용도").optional(),
                            fieldWithPath("data[].roofCdNm").type(JsonFieldType.STRING).description("지붕코드명").optional(),
                            fieldWithPath("data[].etcRoof").type(JsonFieldType.STRING).description("기타지붕").optional(),
                            fieldWithPath("data[].householdCnt").type(JsonFieldType.NUMBER)
                                .description("세대수")
                                .optional(),
                            fieldWithPath("data[].familyCnt").type(JsonFieldType.NUMBER).description("가구수").optional(),
                            fieldWithPath("data[].rideUseElvtCnt").type(JsonFieldType.NUMBER)
                                .description("승용승강기수")
                                .optional(),
                            fieldWithPath("data[].emgenUseElvtCnt").type(JsonFieldType.NUMBER)
                                .description("비상용승강기수")
                                .optional(),
                            fieldWithPath("data[].useAprDay").type(JsonFieldType.STRING)
                                .description("사용승인일 (YYYYMMDD)")
                                .optional(),
                            fieldWithPath("data[].seismicDesignYn").type(JsonFieldType.STRING)
                                .description("내진설계적용여부 (Y/N)")
                                .optional(),
                            fieldWithPath("data[].seismicAbility").type(JsonFieldType.STRING)
                                .description("내진능력")
                                .optional(),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

}
