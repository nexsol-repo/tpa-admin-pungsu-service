package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.File;

public record CertificateUploadResponse(String fileKey, String fileName) {

    public static CertificateUploadResponse of(File file) {
        return new CertificateUploadResponse(file.fileKey(), file.fileName());
    }
}