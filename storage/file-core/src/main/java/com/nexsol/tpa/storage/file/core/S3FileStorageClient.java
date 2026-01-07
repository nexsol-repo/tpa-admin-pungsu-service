package com.nexsol.tpa.storage.file.core;

import com.nexsol.tpa.storage.file.core.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageClient implements FileStorageClient {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final S3Properties properties;

    @Override
    public String upload(InputStream inputStream, String objectKey, long size, String contentType) {
        PutObjectRequest putOb = PutObjectRequest.builder()
            .bucket(properties.getBucketName())
            .key(objectKey)
            .contentType(contentType)
            .build();
        s3Client.putObject(putOb, RequestBody.fromInputStream(inputStream, size));
        return objectKey;
    }

    @Override
    public String generatePresignedUrl(String objectKey) {

        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(b -> b.bucket(properties.getBucketName()).key(objectKey)) // [수정]
                .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();

        }
        catch (Exception e) {
            log.error("Presigned URL generation failed", e);
            throw new RuntimeException("URL 생성 실패", e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(properties.getBucketName()) // 버킷
                                                                                        // 이름
                                                                                        // 설정
                .key(objectKey) // 삭제할 파일의 키(경로) 설정
            );
            log.info("File Deleted: {}", objectKey);
        }
        catch (Exception e) {
            log.error("File deletion failed: {}", objectKey, e);
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

}
