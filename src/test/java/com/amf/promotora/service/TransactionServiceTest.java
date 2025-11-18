package com.amf.promotora.service;

import com.amf.promotora.enums.TransactionType;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    @Test
    void transferSuccess() {
        Account from = new Account();
        from.setId("1");
        from.setBalance(BigDecimal.valueOf(100));

        Account to = new Account();
        to.setId("2");
        to.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById("1")).thenReturn(Optional.of(from));
        when(accountRepository.findById("2")).thenReturn(Optional.of(to));

        Transaction savedTx = Transaction.builder()
                .fromAccountId("1")
                .toAccountId("2")
                .amount(BigDecimal.valueOf(30))
                .type(TransactionType.TRANSFER)
                .createdAt(Instant.now())
                .performedBy("user")
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        Transaction tx = transactionService.transfer("1", "2", BigDecimal.valueOf(30), "user");

        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(BigDecimal.valueOf(70), from.getBalance());
        assertEquals(BigDecimal.valueOf(80), to.getBalance());

        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transferSameAccountShouldThrow() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.transfer("1", "1", BigDecimal.TEN, "user"));
        assertEquals("Transferência entre a mesma conta não é permitida", exception.getMessage());
    }

    @Test
    void transferInsufficientBalanceShouldThrow() {
        Account from = new Account();
        from.setId("1");
        from.setBalance(BigDecimal.valueOf(10));

        Account to = new Account();
        to.setId("2");
        to.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById("1")).thenReturn(Optional.of(from));
        when(accountRepository.findById("2")).thenReturn(Optional.of(to));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.transfer("1", "2", BigDecimal.valueOf(20), "user"));
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void depositSuccess() {
        Account account = new Account();
        account.setId("1");
        account.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById("1")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = transactionService.deposit("1", BigDecimal.valueOf(30), "user");

        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(BigDecimal.valueOf(80), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(tx);
    }

    @Test
    void withdrawSuccess() {
        Account account = new Account();
        account.setId("1");
        account.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById("1")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = transactionService.withdraw("1", BigDecimal.valueOf(40), "user");

        assertEquals(TransactionType.WITHDRAW, tx.getType());
        assertEquals(BigDecimal.valueOf(60), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(tx);
    }

    @Test
    void withdrawInsufficientBalanceShouldThrow() {
        Account account = new Account();
        account.setId("1");
        account.setBalance(BigDecimal.valueOf(20));

        when(accountRepository.findById("1")).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.withdraw("1", BigDecimal.valueOf(50), "user"));
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void getTransactionsWithoutDates() {
        Transaction tx1 = Transaction.builder().id("tx1").build();
        Transaction tx2 = Transaction.builder().id("tx2").build();

        when(transactionRepository.findByAccountId("1")).thenReturn(List.of(tx1, tx2));

        List<Transaction> transactions = transactionService.getTransactions("1", null, null);

        assertEquals(2, transactions.size());
        verify(transactionRepository).findByAccountId("1");
    }

    @Test
    void getTransactionsWithDates() {
        Transaction tx1 = Transaction.builder().id("tx1").build();
        Transaction tx2 = Transaction.builder().id("tx2").build();

        Instant start = Instant.parse("2025-11-17T00:00:00Z");
        Instant end = Instant.parse("2025-11-17T23:59:59Z");

        when(transactionRepository.findByAccountIdAndCreatedAtBetween("1", start, end))
                .thenReturn(List.of(tx1, tx2));

        List<Transaction> transactions = transactionService.getTransactions("1",
                "2025-11-17T00:00:00Z", "2025-11-17T23:59:59Z");

        assertEquals(2, transactions.size());
        verify(transactionRepository).findByAccountIdAndCreatedAtBetween("1", start, end);
    }

    @Test
    void getTransactionsInvalidDateShouldThrow() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.getTransactions("1", "invalid", "date"));
        assertEquals("Formato de data inválido. Use ISO 8601.", exception.getMessage());
    }
}
