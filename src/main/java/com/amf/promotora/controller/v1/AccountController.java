package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.dto.BalanceDTO;
import com.amf.promotora.model.Account;
import com.amf.promotora.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
@Validated
@Tag(name = "Accounts", description = "API para gerenciamento de contas bancárias")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Criar conta bancária para cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado")
    })
    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountDTO dto) {
        Account a = accountService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + a.getId())).body(a);
    }

    @Operation(summary = "Consultar saldo da conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceDTO> balance(@PathVariable String id) {
        BigDecimal b = accountService.getBalance(id);
        BalanceDTO dto = new BalanceDTO();
        dto.setAccountId(id);
        dto.setBalance(b);
        return ResponseEntity.ok(dto);
    }
}
