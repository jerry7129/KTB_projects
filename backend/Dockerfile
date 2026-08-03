# syntax=docker/dockerfile:1

# Builder Stage
FROM amazoncorretto:21 AS builder

LABEL authors="ktb_ksg"

WORKDIR /app

# Gradle Wrapper에서 사용하는 xargs 설치
RUN dnf install -y findutils \
    && dnf clean all \
    && rm -rf /var/cache/dnf

# Gradle Wrapper와 빌드 설정을 먼저 복사
# Overlafs 캐시 이점 살리기 위함
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* ./
COPY build.gradle* ./

RUN chmod +x gradlew

# 소스 코드 복사
COPY src ./src

# Spring Boot 실행 JAR 생성
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon \
    && JAR_FILE="$(find build/libs \
        -maxdepth 1 \
        -type f \
        -name '*.jar' \
        ! -name '*-plain.jar' \
        | head -n 1)" \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /app/app.jar


# Runner Stage
FROM amazoncorretto:21-al2023-headless AS runtime

WORKDIR /app

COPY --from=builder --chown=10001:10001 /app/app.jar ./app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]