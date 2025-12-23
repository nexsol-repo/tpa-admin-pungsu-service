# 1. Builder Stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Gradle 캐싱 최적화
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY lint.gradle .

COPY core/core-api/build.gradle ./core/core-api/
COPY core/core-enum/build.gradle ./core/core-enum/
COPY storage/db-core/build.gradle ./storage/db-core/
COPY support/logging/build.gradle ./support/logging/
COPY tests/api-docs/build.gradle ./tests/api-docs/

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || return 0

COPY . .
RUN ./gradlew :core:core-api:clean :core:core-api:bootJar -x test --no-daemon

# 2. Runtime Stage
FROM eclipse-temurin:25-jre
# [중요] Docker Health Check를 위해 curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]