package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.dto.BalanceDTO;
import com.amf.promotora.model.Account;
import com.amf.promotora.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountDTO dto) {
        Account a = accountService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + a.getId())).body(a);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceDTO> balance(@PathVariable String id) {
        BigDecimal b = accountService.getBalance(id);
        BalanceDTO dto = new BalanceDTO();
        dto.setAccountId(id);
        dto.setBalance(b);
        return ResponseEntity.ok(dto);
    }
}
