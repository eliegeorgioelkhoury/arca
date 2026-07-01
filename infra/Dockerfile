# Multi-stage build for the ARCA Spring Boot backend. Tuned for a 512 MB instance.
# Build from the repo root:  docker build -f infra/Dockerfile -t arca-backend .
# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY backend/src ./src
RUN ./mvnw -B -q -DskipTests package

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/arca-backend-*.jar app.jar
EXPOSE 8080
# 512 MB budget: cap the JVM heap at 70% of the container memory. JAVA_TOOL_OPTIONS
# is read automatically by the JVM, so a plain exec-form entrypoint is enough.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"
ENV SPRING_MAIN_LAZY_INITIALIZATION=true
ENTRYPOINT ["java", "-jar", "app.jar"]
