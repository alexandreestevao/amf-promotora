# AMF Promotora - Starter Project

## Tecnologias

- Java 17
- Spring Boot 3+
- MongoDB (via Docker)
- Spring Data MongoDB
- Bean Validation (ex.: @NotBlank, @Positive nos DTOs)
- OpenAPI (springdoc)
- Lombok (opcional)
- Vaadin 24+ (Dashboard Web)
- Tratamento de exceções global (@ControllerAdvice)
- Versionamento de API (/api/v1/...)
- Auditoria básica (quem fez a transferência e quando)	

## Como executar localmente

1. Subir o MongoDB com Docker:
   - `docker compose up -d`
   - O docker-compose.yml já mapeia a porta 27017 e cria o usuário administrador:
     - `MONGO_INITDB_ROOT_USERNAME: admin`
     - `MONGO_INITDB_ROOT_PASSWORD: admin123`

2. Rodar a aplicação:
   - `mvn spring-boot:run`
   - A aplicação estará disponível em:
     - `http://localhost:8080/`
	 
## Endpoints principais

1. Clientes
	- Cadastro de cliente
		- `POST /api/v1/clients`

2. Contas
	- Criação de conta bancária
		- `POST /api/v1/accounts` 
	- Consultar saldo da conta	
		- `GET /api/v1/accounts/{id}/balance` 

2. Transações
	- Realizar transferência entre contas
		- `POST /api/v1/transactions/transfer` 
	- Depósito em conta
		- `POST /api/v1/transactions/deposit` 
	- Saque de conta		
		- `POST /api/v1/transactions/withdraw` 
	- Consultar extrato de movimentações
		- `GET /api/v1/transactions/account/{accountId}/transactions?startDate={start}&endDate={end}` 

