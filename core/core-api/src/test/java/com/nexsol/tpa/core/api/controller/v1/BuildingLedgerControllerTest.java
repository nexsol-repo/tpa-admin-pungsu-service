package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.BuildingLedger;
import com.nexsol.tpa.core.domain.BuildingLedgerService;
import com.nexsol.tpa.core.domain.LoginAdmin;
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
    @DisplayName("건축물대장 상세 조회 API 문서화")
    void searchBuildingLedger() throws Exception {
        // Given
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
            .atchBldArea(1000.0)
            .totalDongArea(10000.0)
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
            // [추가] 주차 시설 정보 Mock 데이터
            .indrMechUtcnt(10)
            .indrMechArea(200.0)
            .oudrMechUtcnt(0)
            .oudrMechArea(0.0)
            .indrAutoUtcnt(100)
            .indrAutoArea(1500.0)
            .oudrAutoUtcnt(20)
            .oudrAutoArea(300.0)
            // [추가] 허가 및 인증 정보 Mock 데이터
            .pmsDay("20000101")
            .stcnsDay("20000301")
            .pmsnoYear("2000")
            .pmsnoKikCdNm("강남구청")
            .pmsnoGbCdNm("신축허가")
            .hoCnt(100)
            .engrGrade("1등급")
            .engrRat(15.5)
            .engrEpi(80)
            .gnBldGrade("최우수")
            .gnBldCert(100)
            .itgBldGrade("1등급")
            .itgBldCert(90)
            .crtnDay("20220101")
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
                            fieldWithPath("data[].regstrGbCdNm").type(JsonFieldType.STRING)
                                .description("대장구분코드명")
                                .optional(),
                            fieldWithPath("data[].regstrKindCdNm").type(JsonFieldType.STRING)
                                .description("대장종류코드명")
                                .optional(),
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
                            fieldWithPath("data[].atchBldArea").type(JsonFieldType.NUMBER)
                                .description("부속건축물면적(m^2)")
                                .optional(),
                            fieldWithPath("data[].totalDongArea").type(JsonFieldType.NUMBER)
                                .description("총동연면적 (m^2)")
                                .optional(),
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

                            // [추가] 주차 시설 정보 필드 문서화
                            fieldWithPath("data[].indrMechUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥내기계식대수")
                                .optional(),
                            fieldWithPath("data[].indrMechArea").type(JsonFieldType.NUMBER)
                                .description("옥내기계식면적")
                                .optional(),
                            fieldWithPath("data[].oudrMechUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥외기계식대수")
                                .optional(),
                            fieldWithPath("data[].oudrMechArea").type(JsonFieldType.NUMBER)
                                .description("옥외기계식면적")
                                .optional(),
                            fieldWithPath("data[].indrAutoUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥내자주식대수")
                                .optional(),
                            fieldWithPath("data[].indrAutoArea").type(JsonFieldType.NUMBER)
                                .description("옥내자주식면적")
                                .optional(),
                            fieldWithPath("data[].oudrAutoUtcnt").type(JsonFieldType.NUMBER)
                                .description("옥외자주식대수")
                                .optional(),
                            fieldWithPath("data[].oudrAutoArea").type(JsonFieldType.NUMBER)
                                .description("옥외자주식면적")
                                .optional(),

                            // [추가] 허가 및 인증 정보 필드 문서화
                            fieldWithPath("data[].pmsDay").type(JsonFieldType.STRING).description("허가일").optional(),
                            fieldWithPath("data[].stcnsDay").type(JsonFieldType.STRING).description("착공일").optional(),
                            fieldWithPath("data[].pmsnoYear").type(JsonFieldType.STRING)
                                .description("허가번호년")
                                .optional(),
                            fieldWithPath("data[].pmsnoKikCdNm").type(JsonFieldType.STRING)
                                .description("허가기관")
                                .optional(),
                            fieldWithPath("data[].pmsnoGbCdNm").type(JsonFieldType.STRING)
                                .description("허가번호구분")
                                .optional(),
                            fieldWithPath("data[].hoCnt").type(JsonFieldType.NUMBER).description("호수").optional(),
                            fieldWithPath("data[].engrGrade").type(JsonFieldType.STRING)
                                .description("에너지효율등급")
                                .optional(),
                            fieldWithPath("data[].engrRat").type(JsonFieldType.NUMBER).description("에너지절감율").optional(),
                            fieldWithPath("data[].engrEpi").type(JsonFieldType.NUMBER).description("EPI점수").optional(),
                            fieldWithPath("data[].gnBldGrade").type(JsonFieldType.STRING)
                                .description("친환경건축물등급")
                                .optional(),
                            fieldWithPath("data[].gnBldCert").type(JsonFieldType.NUMBER)
                                .description("친환경건축물인증점수")
                                .optional(),
                            fieldWithPath("data[].itgBldGrade").type(JsonFieldType.STRING)
                                .description("지능형건축물등급")
                                .optional(),
                            fieldWithPath("data[].itgBldCert").type(JsonFieldType.NUMBER)
                                .description("지능형건축물인증점수")
                                .optional(),
                            fieldWithPath("data[].crtnDay").type(JsonFieldType.STRING).description("생성일자").optional(),

                            fieldWithPath("error").description("에러 정보").optional())));
    }

}