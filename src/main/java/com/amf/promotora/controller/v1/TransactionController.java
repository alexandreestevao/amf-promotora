package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AmountDTO;
import com.amf.promotora.dto.TransactionDTO;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
@Tag(name = "Transactions", description = "API para transferências, depósitos e saques")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Realizar transferência entre contas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@Valid @RequestBody TransactionDTO dto) {
        Transaction tx = transactionService.transfer(
                dto.getFromAccountId(),
                dto.getToAccountId(),
                dto.getAmount(),
                dto.getPerformedBy()
        );
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }

    @Operation(summary = "Depósito em conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@Valid @RequestBody AmountDTO dto) {
        Transaction tx = transactionService.deposit(
                dto.getAccountId(),
                dto.getAmount(),
                dto.getPerformedBy()
        );
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }

    @Operation(summary = "Saque de conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@Valid @RequestBody AmountDTO dto) {
        Transaction tx = transactionService.withdraw(
                dto.getAccountId(),
                dto.getAmount(),
                dto.getPerformedBy()
        );
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }

    @Operation(summary = "Consultar extrato de movimentações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Instant start = null;
        Instant end = null;

        try {
            if (startDate != null) start = Instant.parse(startDate);
            if (endDate != null) end = Instant.parse(endDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use ISO 8601 (ex: 2025-11-18T00:00:00Z)");
        }

        List<Transaction> transactions = transactionService.getTransactions(accountId, start, end);
        return ResponseEntity.ok(transactions);
    }
}
