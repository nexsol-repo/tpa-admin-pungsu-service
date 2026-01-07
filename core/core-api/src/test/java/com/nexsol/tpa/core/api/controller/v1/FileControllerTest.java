package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.File;
import com.nexsol.tpa.core.domain.FileService;
import com.nexsol.tpa.core.domain.LoginAdmin;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileControllerTest extends RestDocsTest {

    private FileService fileService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        fileService = mock(FileService.class);

        // @LoginAdmin 어노테이션 처리를 위한 Mock Resolver 설정
        HandlerMethodArgumentResolver loginAdminResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(LoginAdmin.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return new AdminUser(1L, "MASTER");
            }
        };

        // 통합된 /v1/admin/pungsu 경로를 사용하는 Controller 등록
        mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService))
            .setCustomArgumentResolvers(loginAdminResolver)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("가입확인서 파일 업로드 API 문서화")
    void upload() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("file", "certificate_sample.pdf",
                MediaType.APPLICATION_PDF_VALUE, "PDF content".getBytes());

        File domainFile = new File("pungsu/certificates/20260107/uuid_sample.pdf", "certificate_sample.pdf");
        given(fileService.upload(any())).willReturn(domainFile);

        // When & Then
        mockMvc
            .perform(
                    multipart("/v1/admin/pungsu/file/upload").file(mockFile).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andDo(document("file-upload", requestParts(partWithName("file").description("업로드할 가입확인서 파일 (PDF)")),
                    responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                            fieldWithPath("data.fileKey").type(JsonFieldType.STRING).description("저장소 내 파일 경로(Key)"),
                            fieldWithPath("data.fileName").type(JsonFieldType.STRING).description("원본 파일명"),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

    @Test
    @DisplayName("가입확인서 다운로드 URL 조회 API 문서화")
    void getPresignedUrl() throws Exception {
        // Given
        String fileKey = "pungsu/certificates/20260107/uuid_sample.pdf";
        String presignedUrl = "https://seaweed-fs.example.com/tpa-admin-pungsu/file.pdf?X-Amz-Algorithm=...";
        given(fileService.getPresignedUrl(fileKey)).willReturn(presignedUrl);

        // When & Then
        mockMvc
            .perform(get("/v1/admin/pungsu/file/presigned-url").param("fileKey", fileKey)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("file-presigned-url",
                    queryParameters(parameterWithName("fileKey").description("조회할 파일의 저장 경로(Key)")),
                    responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                            fieldWithPath("data").type(JsonFieldType.STRING).description("10분간 유효한 임시 다운로드 URL"),
                            fieldWithPath("error").description("에러 정보").optional())));
    }

}
