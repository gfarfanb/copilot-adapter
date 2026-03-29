
# copilot-adapter

GitHub Copilot [REST Models API](https://docs.github.com/en/rest/models) adapter for OpenAI API compatibility. This was created to integrate GitHub Copilot with Open WebUI.


## App environment variables

|Variable|Default|
|---|---|
|SERVER_PORT|8181|
|REDIS_HOST|localhost|
|REDIS_PORT|6379|
|REDIS_DATABASE|0|
|COPILOT_API_URL|https://models.github.ai|
|COPILOT_API_VERSION|2026-03-10|


## GitHub Fine-grained personal access token

Create a [GitHub token](https://github.com/settings/personal-access-tokens/new)
```yml
Token name: <github_copilot_token>
Expiration: <select>
Repository access: Public reporitories
Permissions:
    - Models: Read-only
```

Set token as environment variable
```sh
# Linux example
echo 'export GITHUB_COPILOT_TOKEN=<github_copilot_token>' >> ~/.bashrc

source ~/.bashrc
```

## Open WebUI (local setup)

Create new connection: `Admin Panel / Settings / Connections / Add Connection`
```yml
Connection Type: External
URL: http://localhost:8181/v1
Prefix ID: GitHub Copilot # Optional
```


## Docker

### Execution

*Docker* run command
```sh
GITHUB_COPILOT_TOKEN=$GITHUB_COPILOT_TOKEN \
    docker run gfarfanb/copilot-adapter:<tag>
```

*Docker Compose* example
```yml
services:
  copilot-adapter:
    image: gfarfanb/copilot-adapter:<tag>
    container_name: copilot-adapter
    environment:
      - SERVER_PORT=${COPILOT_ADAPTER_PORT} # Optional
      - REDIS_HOST=${REDIS_HOST} # Optional
      - REDIS_PORT=${REDIS_PORT} # Optional
      - REDIS_DATABASE=${REDIS_DATABASE} # Optional
      - GITHUB_COPILOT_TOKEN=${COPILOT_ADAPTER_GITHUB_TOKEN}
      - COPILOT_API_URL=${COPILOT_ADAPTER_API_URL} # Optional
      - COPILOT_API_VERSION=${COPILOT_ADAPTER_API_VERSION} # Optional
    network_mode: host
    restart: unless-stopped
    depends_on:
      - redis

  redis:
    image: redis:8.6.2-alpine
    container_name: redis
    network_mode: host
    restart: unless-stopped
```


### Upload image

> Push image from [Docker Hub repo](https://hub.docker.com/repository/docker/gfarfanb/copilot-adapter/general)

```sh
export BUILD_TAG=<tag>

docker build --build-arg DOCKER_TAG=$BUILD_TAG -t gfarfanb/copilot-adapter:$BUILD_TAG .

docker push gfarfanb/copilot-adapter:$BUILD_TAG
```
