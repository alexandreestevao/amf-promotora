package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.TransactionDTO;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> transfer(@Valid @RequestBody TransactionDTO dto) {
        Transaction tx = transactionService.transfer(dto.getFromAccountId(), dto.getToAccountId(), dto.getAmount(), dto.getPerformedBy());
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId())).body(tx);
    }
}
