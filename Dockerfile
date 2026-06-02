# =========================
# Stage 1: Build
# =========================
FROM gradle:8.14.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/workhub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]