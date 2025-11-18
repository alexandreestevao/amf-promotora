package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AmountDTO;
import com.amf.promotora.dto.TransactionDTO;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Transferência entre contas
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

    // Depósito
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@Valid @RequestBody AmountDTO dto) {
        Transaction tx = transactionService.deposit(
                dto.getAccountId(),
                dto.getAmount(),
                dto.getPerformedBy()
        );
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }

    // Saque
    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@Valid @RequestBody AmountDTO dto) {
        Transaction tx = transactionService.withdraw(
                dto.getAccountId(),
                dto.getAmount(),
                dto.getPerformedBy()
        );
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }

    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<Transaction> transactions = transactionService.getTransactions(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }


}
