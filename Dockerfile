# Stage 1: Build the application with Maven
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests


# Stage 2: Run the application with a lightweight JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SERVER_PORT=8181

EXPOSE $SERVER_PORT

LABEL maintainer="gfarfanb" \
      version="1.0.0" \
      description="GitHub Copilot adapter for OpenAI API compatibility" \
      org.opencontainers.image.title="copilot-adapter" \
      org.opencontainers.image.description="GitHub Copilot adapter for OpenAI API compatibility" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.authors="Giovanni Farfán B." \
      org.opencontainers.image.source="https://github.com/gfarfanb/copilot-adapter"

ENTRYPOINT ["java", "-jar", "app.jar"]
