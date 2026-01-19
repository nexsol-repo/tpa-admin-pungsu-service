package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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

        // Controller 등록 및 RestDocs 설정 적용
        mockMvc = MockMvcBuilders.standaloneSetup(new BuildingLedgerController(buildingLedgerService))
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("건축물대장 상세 조회 (총괄+표제부) API 문서화")
    void searchBuildingLedger() throws Exception {
        // Given (총괄표제부 Mock)
        BuildingLedgerRecap mockRecap = BuildingLedgerRecap.builder()
            .mgmBldrgstPk("11110-99999")
            .buildingName("타워팰리스 총괄")
            .totArea(50000.0)
            .useAprDay("20021001")
            .build();

        // Given (표제부 상세 Mock - 이전과 동일하게 상세 필드 포함)
        BuildingLedger mockLedger = BuildingLedger.builder()
            .mgmBldrgstPk("11110-100002")
            .regstrGbCdNm("일반")
            .regstrKindCdNm("표제부")
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
            .atchBldArea(12.0)
            .totalDongArea(24.0)
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
            // 주차/허가 정보 (생략 가능하나 완전성을 위해 일부 포함)
            .indrMechUtcnt(10)
            .pmsDay("20000101")
            .build();

        // Service가 Overview 객체를 반환하도록 설정
        BuildingLedgerOverview mockOverview = new BuildingLedgerOverview(mockRecap, List.of(mockLedger));

        given(buildingLedgerService.searchBuildingLedgerOverview(anyString(), anyString(), anyString(), anyString()))
            .willReturn(mockOverview);

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

                            // [1] 총괄표제부 (Recap)
                            fieldWithPath("data.recap").type(JsonFieldType.OBJECT)
                                .description("총괄표제부 정보 (없을 경우 null)")
                                .optional(),
                            fieldWithPath("data.recap.mgmBldrgstPk").type(JsonFieldType.STRING)
                                .description("총괄 관리PK")
                                .optional(),
                            fieldWithPath("data.recap.buildingName").type(JsonFieldType.STRING)
                                .description("총괄 건물명")
                                .optional(),
                            fieldWithPath("data.recap.totArea").type(JsonFieldType.NUMBER)
                                .description("총괄 연면적")
                                .optional(),
                            fieldWithPath("data.recap.useAprDay").type(JsonFieldType.STRING)
                                .description("총괄 사용승인일")
                                .optional(),

                            // [2] 표제부 목록 (Ledgers)
                            fieldWithPath("data.ledgers").type(JsonFieldType.ARRAY).description("건축물대장(표제부) 목록"),

                            // 아래 필드들은 data.ledgers[].~ 로 시작해야 함
                            fieldWithPath("data.ledgers[].mgmBldrgstPk").type(JsonFieldType.STRING)
                                .description("관리건축물대장PK"),
                            fieldWithPath("data.ledgers[].regstrGbCdNm").type(JsonFieldType.STRING)
                                .description("대장구분코드명")
                                .optional(),
                            fieldWithPath("data.ledgers[].regstrKindCdNm").type(JsonFieldType.STRING)
                                .description("대장종류코드명")
                                .optional(),
                            fieldWithPath("data.ledgers[].buildingName").type(JsonFieldType.STRING)
                                .description("건물명")
                                .optional(),
                            fieldWithPath("data.ledgers[].dongName").type(JsonFieldType.STRING)
                                .description("동명")
                                .optional(),
                            fieldWithPath("data.ledgers[].platPlc").type(JsonFieldType.STRING)
                                .description("대지위치")
                                .optional(),
                            fieldWithPath("data.ledgers[].newPlatPlc").type(JsonFieldType.STRING)
                                .description("도로명주소")
                                .optional(),
                            fieldWithPath("data.ledgers[].sigunguCd").type(JsonFieldType.STRING)
                                .description("시군구코드")
                                .optional(),
                            fieldWithPath("data.ledgers[].bjdongCd").type(JsonFieldType.STRING)
                                .description("법정동코드")
                                .optional(),
                            fieldWithPath("data.ledgers[].bun").type(JsonFieldType.STRING).description("번").optional(),
                            fieldWithPath("data.ledgers[].ji").type(JsonFieldType.STRING).description("지").optional(),

                            fieldWithPath("data.ledgers[].platArea").type(JsonFieldType.NUMBER)
                                .description("대지면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].archArea").type(JsonFieldType.NUMBER)
                                .description("건축면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].totArea").type(JsonFieldType.NUMBER)
                                .description("연면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].vlRatEstmTotArea").type(JsonFieldType.NUMBER)
                                .description("용적률산정연면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].bcRat").type(JsonFieldType.NUMBER)
                                .description("건폐율")
                                .optional(),
                            fieldWithPath("data.ledgers[].vlRat").type(JsonFieldType.NUMBER)
                                .description("용적률")
                                .optional(),
                            fieldWithPath("data.ledgers[].atchBldArea").type(JsonFieldType.NUMBER)
                                .description("부속건축물면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].totalDongArea").type(JsonFieldType.NUMBER)
                                .description("총동연면적")
                                .optional(),

                            fieldWithPath("data.ledgers[].height").type(JsonFieldType.NUMBER)
                                .description("높이")
                                .optional(),
                            fieldWithPath("data.ledgers[].groundFloorCnt").type(JsonFieldType.NUMBER)
                                .description("지상층수")
                                .optional(),
                            fieldWithPath("data.ledgers[].underGroundFloorCnt").type(JsonFieldType.NUMBER)
                                .description("지하층수")
                                .optional(),

                            fieldWithPath("data.ledgers[].strctCdNm").type(JsonFieldType.STRING)
                                .description("구조코드명")
                                .optional(),
                            fieldWithPath("data.ledgers[].etcStrct").type(JsonFieldType.STRING)
                                .description("기타구조")
                                .optional(),
                            fieldWithPath("data.ledgers[].mainPurpsCdNm").type(JsonFieldType.STRING)
                                .description("주용도코드명")
                                .optional(),
                            fieldWithPath("data.ledgers[].etcPurps").type(JsonFieldType.STRING)
                                .description("기타용도")
                                .optional(),
                            fieldWithPath("data.ledgers[].roofCdNm").type(JsonFieldType.STRING)
                                .description("지붕코드명")
                                .optional(),
                            fieldWithPath("data.ledgers[].etcRoof").type(JsonFieldType.STRING)
                                .description("기타지붕")
                                .optional(),

                            fieldWithPath("data.ledgers[].householdCnt").type(JsonFieldType.NUMBER)
                                .description("세대수")
                                .optional(),
                            fieldWithPath("data.ledgers[].familyCnt").type(JsonFieldType.NUMBER)
                                .description("가구수")
                                .optional(),
                            fieldWithPath("data.ledgers[].rideUseElvtCnt").type(JsonFieldType.NUMBER)
                                .description("승용승강기수")
                                .optional(),
                            fieldWithPath("data.ledgers[].emgenUseElvtCnt").type(JsonFieldType.NUMBER)
                                .description("비상용승강기수")
                                .optional(),
                            fieldWithPath("data.ledgers[].useAprDay").type(JsonFieldType.STRING)
                                .description("사용승인일")
                                .optional(),
                            fieldWithPath("data.ledgers[].seismicDesignYn").type(JsonFieldType.STRING)
                                .description("내진설계여부")
                                .optional(),
                            fieldWithPath("data.ledgers[].seismicAbility").type(JsonFieldType.STRING)
                                .description("내진능력")
                                .optional(),

                            // 주차 관련 필드 문서화
                            fieldWithPath("data.ledgers[].indrMechUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥내기계식대수")
                                .optional(),
                            fieldWithPath("data.ledgers[].indrMechArea").type(JsonFieldType.NUMBER)
                                .description("옥내기계식면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].oudrMechUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥외기계식대수")
                                .optional(),
                            fieldWithPath("data.ledgers[].oudrMechArea").type(JsonFieldType.NUMBER)
                                .description("옥외기계식면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].indrAutoUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥내자주식대수")
                                .optional(),
                            fieldWithPath("data.ledgers[].indrAutoArea").type(JsonFieldType.NUMBER)
                                .description("옥내자주식면적")
                                .optional(),
                            fieldWithPath("data.ledgers[].oudrAutoUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥외자주식대수")
                                .optional(),
                            fieldWithPath("data.ledgers[].oudrAutoArea").type(JsonFieldType.NUMBER)
                                .description("옥외자주식면적")
                                .optional(),

                            // 허가/인증 관련 필드 문서화
                            fieldWithPath("data.ledgers[].pmsDay").type(JsonFieldType.STRING)
                                .description("허가일")
                                .optional(),
                            fieldWithPath("data.ledgers[].stcnsDay").type(JsonFieldType.STRING)
                                .description("착공일")
                                .optional(),
                            fieldWithPath("data.ledgers[].pmsnoYear").type(JsonFieldType.STRING)
                                .description("허가번호년")
                                .optional(),
                            fieldWithPath("data.ledgers[].pmsnoKikCdNm").type(JsonFieldType.STRING)
                                .description("허가기관")
                                .optional(),
                            fieldWithPath("data.ledgers[].pmsnoGbCdNm").type(JsonFieldType.STRING)
                                .description("허가번호구분")
                                .optional(),
                            fieldWithPath("data.ledgers[].hoCnt").type(JsonFieldType.NUMBER)
                                .description("호수")
                                .optional(),
                            fieldWithPath("data.ledgers[].engrGrade").type(JsonFieldType.STRING)
                                .description("에너지효율등급")
                                .optional(),
                            fieldWithPath("data.ledgers[].engrRat").type(JsonFieldType.NUMBER)
                                .description("에너지절감율")
                                .optional(),
                            fieldWithPath("data.ledgers[].engrEpi").type(JsonFieldType.NUMBER)
                                .description("EPI점수")
                                .optional(),
                            fieldWithPath("data.ledgers[].gnBldGrade").type(JsonFieldType.STRING)
                                .description("친환경건축물등급")
                                .optional(),
                            fieldWithPath("data.ledgers[].gnBldCert").type(JsonFieldType.NUMBER)
                                .description("친환경건축물인증점수")
                                .optional(),
                            fieldWithPath("data.ledgers[].itgBldGrade").type(JsonFieldType.STRING)
                                .description("지능형건축물등급")
                                .optional(),
                            fieldWithPath("data.ledgers[].itgBldCert").type(JsonFieldType.NUMBER)
                                .description("지능형건축물인증점수")
                                .optional(),
                            fieldWithPath("data.ledgers[].crtnDay").type(JsonFieldType.STRING)
                                .description("생성일자")
                                .optional(),

                            fieldWithPath("error").description("에러 정보").optional())));
    }

}