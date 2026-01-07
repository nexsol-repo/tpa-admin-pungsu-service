package com.nexsol.tpa.core.domain;

import java.io.InputStream;

public interface FileStorageClient {

    /**
     * 임의의 파일을 업로드합니다.
     * @param inputStream 파일 데이터 스트림
     * @param objectKey 저장할 경로 키 (예: insurance/signatures/uuid.png)
     * @param size 파일 크기
     * @param contentType MIME 타입
     * @return 저장된 파일의 접근 경로 (Key)
     */
    String upload(InputStream inputStream, String objectKey, long size, String contentType);

    /**
     * Presigned URL 생성 (다운로드/조회용)
     * @param objectKey 저장된 파일 키
     * @return 다운로드 URL
     */
    String generatePresignedUrl(String objectKey);

    void delete(String objectKey);

}
