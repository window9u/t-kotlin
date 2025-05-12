# 1단계: 빌드 단계
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# 2단계: 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
