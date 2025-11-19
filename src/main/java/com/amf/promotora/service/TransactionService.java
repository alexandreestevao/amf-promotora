package com.amf.promotora.service;

import com.amf.promotora.enums.TransactionType;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

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

        Transaction tx = Transaction.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .createdAt(Instant.now())
                .performedBy(performedBy)
                .build();


        return transactionRepository.save(tx);
    }

    public Transaction deposit(String accountId, BigDecimal amount, String performedBy) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Conta não encontrada"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction tx = Transaction.builder()
                .fromAccountId(null)
                .toAccountId(accountId)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .createdAt(Instant.now())
                .performedBy(performedBy)
                .build();

        return transactionRepository.save(tx);
    }

    public Transaction withdraw(String accountId, BigDecimal amount, String performedBy) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Conta não encontrada"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction tx = Transaction.builder()
                .fromAccountId(accountId)
                .toAccountId(null)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .createdAt(Instant.now())
                .performedBy(performedBy)
                .build();

        return transactionRepository.save(tx);
    }

    public List<Transaction> getTransactions(String accountId, Instant start, Instant end) {
        if (start != null && end != null) {
            return transactionRepository.findByAccountIdAndCreatedAtBetween(accountId, start, end);
        } else {
            return transactionRepository.findByAccountId(accountId);
        }
    }

}
