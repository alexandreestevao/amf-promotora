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
    void setup() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    // ---------------------------------------------------------
    // TRANSFER
    // ---------------------------------------------------------
    @Test
    void testTransferSuccess() {
        Account from = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(500))
                .build();

        Account to = Account.builder()
                .id("A2")
                .balance(BigDecimal.valueOf(100))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(from));
        when(accountRepository.findById("A2")).thenReturn(Optional.of(to));

        Transaction transactionMock = Transaction.builder()
                .fromAccountId("A1")
                .toAccountId("A2")
                .amount(BigDecimal.valueOf(200))
                .type(TransactionType.TRANSFER)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionMock);

        Transaction result = transactionService.transfer("A1", "A2", BigDecimal.valueOf(200), "admin");

        assertEquals("A1", result.getFromAccountId());
        assertEquals("A2", result.getToAccountId());
        assertEquals(BigDecimal.valueOf(200), result.getAmount());

        assertEquals(BigDecimal.valueOf(300), from.getBalance());
        assertEquals(BigDecimal.valueOf(300), to.getBalance());
    }

    @Test
    void testTransferSameAccountThrowsException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionService.transfer("A1", "A1", BigDecimal.TEN, "admin"));

        assertEquals("Transferência entre a mesma conta não é permitida", ex.getMessage());
    }

    @Test
    void testTransferFromAccountNotFound() {
        when(accountRepository.findById("A1")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionService.transfer("A1", "A2", BigDecimal.TEN, "admin"));

        assertEquals("Conta origem não encontrada", ex.getMessage());
    }

    @Test
    void testTransferToAccountNotFound() {
        Account from = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(500))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(from));
        when(accountRepository.findById("A2")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionService.transfer("A1", "A2", BigDecimal.TEN, "admin"));

        assertEquals("Conta destino não encontrada", ex.getMessage());
    }

    @Test
    void testTransferInsufficientBalance() {
        Account from = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(50))
                .build();

        Account to = Account.builder()
                .id("A2")
                .balance(BigDecimal.valueOf(100))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(from));
        when(accountRepository.findById("A2")).thenReturn(Optional.of(to));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionService.transfer("A1", "A2", BigDecimal.valueOf(200), "admin"));

        assertEquals("Saldo insuficiente", ex.getMessage());
    }

    // ---------------------------------------------------------
    // DEPOSIT
    // ---------------------------------------------------------
    @Test
    void testDepositSuccess() {
        Account account = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(100))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(account));

        Transaction savedTx = Transaction.builder()
                .toAccountId("A1")
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.DEPOSIT)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        Transaction result = transactionService.deposit("A1", BigDecimal.valueOf(50), "admin");

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, result.getType());
    }

    // ---------------------------------------------------------
    // WITHDRAW
    // ---------------------------------------------------------
    @Test
    void testWithdrawSuccess() {
        Account account = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(200))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(account));

        Transaction savedTx = Transaction.builder()
                .fromAccountId("A1")
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.WITHDRAW)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        Transaction result = transactionService.withdraw("A1", BigDecimal.valueOf(50), "admin");

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals(TransactionType.WITHDRAW, result.getType());
    }

    @Test
    void testWithdrawInsufficientBalance() {
        Account account = Account.builder()
                .id("A1")
                .balance(BigDecimal.valueOf(30))
                .build();

        when(accountRepository.findById("A1")).thenReturn(Optional.of(account));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionService.withdraw("A1", BigDecimal.valueOf(100), "admin"));

        assertEquals("Saldo insuficiente", ex.getMessage());
    }

    // ---------------------------------------------------------
    // GET TRANSACTIONS
    // ---------------------------------------------------------
    @Test
    void testGetTransactionsWithDateRange() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();

        List<Transaction> mockList = List.of(
                Transaction.builder().id("T1").build(),
                Transaction.builder().id("T2").build()
        );

        when(transactionRepository.findByAccountIdAndCreatedAtBetween("A1", start, end))
                .thenReturn(mockList);

        List<Transaction> result = transactionService.getTransactions("A1", start, end);

        assertEquals(2, result.size());
    }

    @Test
    void testGetTransactionsWithoutDateRange() {
        List<Transaction> mockList = List.of(
                Transaction.builder().id("T1").build()
        );

        when(transactionRepository.findByAccountId("A1")).thenReturn(mockList);

        List<Transaction> result = transactionService.getTransactions("A1", null, null);

        assertEquals(1, result.size());
    }
}
