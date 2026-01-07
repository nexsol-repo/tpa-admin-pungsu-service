package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.file.core.FileStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageClient fileStorageClient;

    public File upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "파일이 비어있습니다.");
        }

        // 1. 저장 경로(Key) 생성: pungsu/certificates/yyyyMMdd/UUID_파일명.pdf
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String originalFileName = file.getOriginalFilename();
        String objectKey = String.format("pungsu/certificates/%s/%s_%s", dateDir, UUID.randomUUID(), originalFileName);

        try {
            // 2. 스토리지 업로드 실행
            fileStorageClient.upload(file.getInputStream(), objectKey, file.getSize(), file.getContentType());

            return new File(objectKey, originalFileName);
        }
        catch (IOException e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR, "파일 저장 중 오류가 발생했습니다.");
        }
    }

    public String getPresignedUrl(String fileKey) {
        // 스토리지 클라이언트를 통해 접근 가능한 URL 생성
        return fileStorageClient.generatePresignedUrl(fileKey);
    }

}