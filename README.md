# AMF Promotora - Starter Project

## Tecnologias
- Java 17
- Spring Boot 3+
- MongoDB (via Docker)
- Spring Data MongoDB
- Bean Validation
- OpenAPI (springdoc)
- Lombok (opcional)

## Como executar localmente

1. Subir MongoDB (replica set):
   - `docker compose up -d`
   - Conectar ao mongo e iniciar o replica set:
     - `docker exec -it <container> mongosh`
     - `rs.initiate()`

2. Rodar a aplicação:
   - `mvn spring-boot:run`

3. Swagger UI:
   - `http://localhost:8080/swagger-ui.html`

## Endpoints principais
- `POST /api/v1/clients`
- `POST /api/v1/accounts`
- `GET /api/v1/accounts/{id}/balance`
- `POST /api/v1/transactions`
- `GET /api/v1/accounts/{id}/transactions?startDate=...&endDate=...`

