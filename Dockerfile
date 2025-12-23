# 1. Builder Stage
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사 (캐싱을 위해 먼저 수행)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY lint.gradle .

# 멀티 모듈 구조이므로 각 모듈의 build.gradle 파일을 모두 복사해야 캐싱이 작동함
COPY core/core-api/build.gradle ./core/core-api/
COPY core/core-enum/build.gradle ./core/core-enum/
COPY storage/db-core/build.gradle ./storage/db-core/
COPY support/logging/build.gradle ./support/logging/
COPY tests/api-docs/build.gradle ./tests/api-docs/

# 권한 부여 및 의존성 사전 다운로드
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || return 0

# 전체 소스 복사 및 빌드
COPY . .
# core-api 모듈의 실행 가능한 JAR 생성 (테스트는 CI 단계나 별도 태스크에서 수행 권장)
RUN ./gradlew :core:core-api:clean :core:core-api:bootJar -x test --no-daemon

# 2. Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# 빌드 결과물 JAR 복사 (빌드된 정확한 경로 지정)
COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar

# 시스템 시간대 설정
ENV TZ=Asia/Seoul

EXPOSE 8080

# Profile 등 유동적인 설정은 docker-compose에서 주입하므로 여기서는 기본 명령만 실행
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]