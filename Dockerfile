
FROM eclipse-temurin:21-jdk-alpine AS build
RUN echo "Stage 1: Build the application with Maven"

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

ARG DOCKER_TAG

RUN echo "Stage 2: Run the application with a lightweight JRE, version: $DOCKER_TAG"

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SERVER_PORT=8181

EXPOSE $SERVER_PORT

LABEL org.opencontainers.image.title="copilot-adapter" \
      org.opencontainers.image.description="GitHub Copilot REST Models API adapter for OpenAI API compatibility" \
      org.opencontainers.image.version="$DOCKER_TAG" \
      org.opencontainers.image.authors="Giovanni Farfán B." \
      org.opencontainers.image.source="https://github.com/gfarfanb/copilot-adapter" \
      org.opencontainers.image.licenses="MIT"

ENTRYPOINT ["java", "-jar", "app.jar"]
