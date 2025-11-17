package com.amf.promotora.service;

import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional("mongoTransactionManager")
    public Transaction transfer(String fromAccountId, String toAccountId, BigDecimal amount, String performedBy) {
        if (fromAccountId.equals(toAccountId)) {
            throw new BusinessException("Transferência entre a mesma conta não é permitida");
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new BusinessException("Conta origem não encontrada"));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new BusinessException("Conta destino não encontrada"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = new Transaction();
        tx.setFromAccountId(fromAccountId);
        tx.setToAccountId(toAccountId);
        tx.setAmount(amount);
        tx.setType("TRANSFER");
        tx.setCreatedAt(OffsetDateTime.now());
        tx.setPerformedBy(performedBy);

        return transactionRepository.save(tx);
    }
}
