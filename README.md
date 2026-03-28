
# copilot-adapter

GitHub Copilot adapter for OpenAI API compatibility. This was created to integrate GitHub Copilot with Open WebUI.


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
# Some Linux example
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
    docker run gfarfanb/copilot-adapter:<version>
```

*Docker Compose* example
```yml
services:
  copilot-adapter:
    image: gfarfanb/copilot-adapter:<version>
    container_name: copilot-adapter
    environment:
      - SERVER_PORT=${COPILOT_ADAPTER_PORT} # Optional
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
docker build -t gfarfanb/copilot-adapter:<version> .

docker push gfarfanb/copilot-adapter:<version>
```
