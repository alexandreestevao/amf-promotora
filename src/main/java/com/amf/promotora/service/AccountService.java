package com.amf.promotora.service;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    public Account create(AccountDTO dto) {
        if (!clientRepository.existsById(dto.getClientId())) {
            throw new BusinessException("Cliente não encontrado");
        }
        Account a = new Account();
        a.setClientId(dto.getClientId());
        a.setNumber(generateAccountNumber());
        a.setType(dto.getType());
        a.setBalance(BigDecimal.ZERO);
        return accountRepository.save(a);
    }

    public BigDecimal getBalance(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Conta não encontrada"))
                .getBalance();
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}
