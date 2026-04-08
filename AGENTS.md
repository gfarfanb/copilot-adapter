# AGENTS.md

## Overview

A Spring Boot 3.2 (Java 21) REST adapter that translates OpenAI API requests to GitHub Copilot REST Models API.
Uses Maven, Caffeine cache (60m TTL), and supports streaming chat completions and embeddings.

## Build & Run

**Build:**
```sh
./mvnw clean package
```

**Run locally:**
```sh
export GITHUB_COPILOT_TOKEN=<token>
./mvnw spring-boot:run
```

**Test locally after changes:**
```sh
./mvnw clean package
java -jar target/copilot-adapter-1.0.0-SNAPSHOT.jar
```

Server listens on port 8181 by default (configurable via `SERVER_PORT` env var).

**No tests exist** — focus on integration via Docker or manual endpoint verification.

## Project Structure

- **Main entrypoint:** `src/main/java/com/legadi/openai/copilot/CopilotAdapterApplication.java`
- **Controller:** `OpenAIV1Controller.java` — REST endpoints at `/v1/chat/completions`, `/v1/embeddings`, `/v1/models`
- **Service layer:** `CopilotService.java` — calls GitHub Copilot API; `ExchangePrinterService.java` logs requests/responses
- **Configuration:** Spring caching via Caffeine, WebClient for HTTP calls, Lombok for POJOs
- **Resources:** `src/main/resources/application.yml` — all config is env-var driven

## Critical Dependencies

- `spring-boot-starter-web` + `spring-boot-starter-webflux` — REST + reactive HTTP client
- `spring-boot-starter-cache` + `caffeine` — in-memory caching with 60m expiration
- Lombok for boilerplate reduction

## Configuration & Secrets

Required environment variables (see README.md):
- `GITHUB_COPILOT_TOKEN` — GitHub API token with Models read permission (no default)
- `SERVER_PORT` — defaults to 8181
- `COPILOT_API_URL` — defaults to `https://models.github.ai`
- `COPILOT_API_VERSION` — defaults to `2026-03-10`

Caffeine caching is configured in application.yml with 60m expiration and max 1000 entries (no env vars needed).

Debug logging enabled for `com.legadi.openai.copilot` in application.yml.

## Docker Build

Multi-stage build (Alpine JDK 21 → JRE):
```sh
export BUILD_TAG=<version>
docker build --build-arg DOCKER_TAG=$BUILD_TAG -t gfarfanb/copilot-adapter:$BUILD_TAG .
docker push gfarfanb/copilot-adapter:$BUILD_TAG
```

Dockerfile skips tests during build (`-DskipTests`). No integration tests to worry about.

## API Endpoints

- `POST /v1/chat/completions` — streams chat responses (Server-Sent Events)
- `POST /v1/embeddings` — returns embeddings
- `GET /v1/models` — lists available models

All responses adapt GitHub Copilot API to OpenAI format.

## Architecture Quirks

- **Streaming:** chat completions use `Flux<String>` for SSE; handled by Spring WebFlux
- **Caching:** automatic Caffeine cache on model calls; 60m TTL, 1000 max entries
- **Header filtering:** request/response headers sanitized (auth tokens hidden) before logging
- **Spring Boot 3.2 quirk:** uses virtual threads under the hood if JDK 21; no explicit config needed

## When Adding Features

1. Add new endpoints to `OpenAIV1Controller.java`
2. Implement business logic in `CopilotService.java`
3. Use `@Cacheable` annotation for response caching (already enabled)
4. Ensure `GITHUB_COPILOT_TOKEN` is available in tests/Docker
5. No test suite exists — verify via integration or Docker image

## Common Mistakes

- Forgetting `GITHUB_COPILOT_TOKEN` env var → adapter won't authenticate to GitHub Copilot API
- Chat completions endpoint expects `@RequestBody Map<String, Object>` for flexibility; don't over-type it

## Logging & Debugging

- `ExchangePrinterService.java` prints request/response bodies (with auth headers hidden)
- Spring WebFilter logs HTTP details at DEBUG level
- Check `application.yml` logging config; `com.legadi.openai.copilot` at DEBUG by default
