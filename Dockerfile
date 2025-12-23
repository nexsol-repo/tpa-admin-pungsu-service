# 1. Builder Stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Gradle 설정 및 래퍼 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY lint.gradle .

# 멀티 모듈 의존성 캐싱 (이 부분이 빠지면 매번 수분씩 소요됨)
COPY core/core-api/build.gradle ./core/core-api/
COPY core/core-enum/build.gradle ./core/core-enum/
COPY storage/db-core/build.gradle ./storage/db-core/
COPY support/logging/build.gradle ./support/logging/
COPY tests/api-docs/build.gradle ./tests/api-docs/

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || return 0

# 소스 전체 복사 및 빌드
COPY . .
# :core:core-api:bootJar만 실행해도 asciidoctor가 자동으로 실행되도록 설정함
RUN ./gradlew :core:core-api:clean :core:core-api:bootJar -x test --no-daemon

# 2. Runtime Stage
FROM eclipse-temurin:25-jre
# [중요] Docker HealthCheck 및 배포 스크립트의 curl을 위해 설치 필수
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 빌드된 결과물 복사
COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul
EXPOSE 8080

# 유동적인 설정은 Compose에서 주입하므로 ENTRYPOINT는 고정
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]