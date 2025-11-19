## Documentação da API - AMF Promotora

````markdown
## Tecnologias

- Java 17  
- Spring Boot 3+  
- MongoDB (via Docker)  
- Spring Data MongoDB  
- Bean Validation (ex.: @NotBlank, @Positive nos DTOs)  
- Lombok (opcional)  
- Vaadin 24+ (Dashboard Web)  
- Tratamento de exceções global (@ControllerAdvice)  
- Versionamento de API (/api/v1/...)  
- Auditoria básica (quem fez a transferência e quando)  
````
## Como executar o projeto localmente

1. **Subir o MongoDB com Docker**  
   O `docker-compose.yml` já mapeia a porta 27017 e cria o usuário administrador:
   ```bash
   docker compose up -d
   ```

Credenciais:

* `MONGO_INITDB_ROOT_USERNAME: admin`
* `MONGO_INITDB_ROOT_PASSWORD: admin123`

2. **Rodar a aplicação**

   ```bash
   mvn spring-boot:run
   ```

   A aplicação estará disponível em:
   `http://localhost:8080/`

---




## Testes com Postman

Para facilitar os testes da API, incluímos uma **coleção Postman** pronta no projeto:  

**Arquivo:** `amf-promotora.postman_collection.json` (localizado na raiz do projeto)

### Como importar no Postman

1. Abra o **Postman**.
2. Clique em **Import** (canto superior esquerdo).
3. Selecione a opção **File** e escolha o arquivo `amf-promotora.postman_collection.json`.
4. Clique em **Import**.

A coleção `AMF Promotora API` será adicionada ao Postman, contendo todos os endpoints:

- **Clientes**
  - `POST /api/v1/clients` → Criar novo cliente
- **Contas**
  - `POST /api/v1/accounts` → Criar conta bancária
  - `GET /api/v1/accounts/{id}/balance` → Consultar saldo
- **Transações**
  - `POST /api/v1/transactions/transfer` → Transferência entre contas
  - `POST /api/v1/transactions/deposit` → Depósito
  - `POST /api/v1/transactions/withdraw` → Saque
  - `GET /api/v1/transactions/account/{accountId}/transactions` → Consultar extrato

### Observações

- Os endpoints que recebem datas devem usar o **formato ISO 8601**, ex: `2025-11-18T00:00:00Z`.
- É necessário que a aplicação esteja rodando localmente (`http://localhost:8080`) para que as requisições funcionem.
- Você pode editar os valores de `accountId`, `fromAccountId`, `toAccountId` e `performedBy` diretamente na coleção para seus testes.


---

## Rodando o Front-end (Vaadin)

O front-end está integrado à aplicação Spring Boot.
Basta acessar via browser após subir a aplicação:

```
http://localhost:8080/transactions
```

* A tela permite: Transferência, Depósito e Saque.
* Filtro de extrato por Conta Origem e período.

---

## Testes

### Unitários

* Estão implementados com **JUnit 5 + Mockito**.
* Para rodar todos os testes unitários:

   ```bash
   mvn test
   ```

### Integração

* Testes de integração podem ser rodados da mesma forma, desde que o MongoDB esteja ativo via Docker.
* O `TransactionService` e `AccountService` possuem métodos validados nos testes.