package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.CertificateUploadResponse;
import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.File;
import com.nexsol.tpa.core.domain.FileService;
import com.nexsol.tpa.core.domain.LoginAdmin;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/admin/pungsu")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 가입확인서 파일 업로드
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CertificateUploadResponse> upload(@RequestPart("file") MultipartFile file,
            @LoginAdmin AdminUser admin) {

        File domainFile = fileService.upload(file);
        return ApiResponse.success(CertificateUploadResponse.of(domainFile));
    }

    /**
     * 가입확인서 조회 (Presigned URL 생성)
     */
    @GetMapping("/file/presigned-url")
    public ApiResponse<String> getDownloadUrl(@RequestParam String fileKey) {
        String url = fileService.getPresignedUrl(fileKey);
        return ApiResponse.success(url);
    }

}