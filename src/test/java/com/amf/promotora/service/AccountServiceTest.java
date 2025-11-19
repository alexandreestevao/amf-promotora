package com.amf.promotora.service;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private ClientRepository clientRepository;
    private TransactionRepository transactionRepository;

    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        clientRepository = mock(ClientRepository.class);
        transactionRepository = mock(TransactionRepository.class);

        accountService = new AccountService(accountRepository, clientRepository, transactionRepository);
    }

    // --------------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------------
    @Test
    void testCreateSuccess() {
        AccountDTO dto = new AccountDTO();
        dto.setClientId("client-1");
        dto.setType("CHECKING");

        when(clientRepository.existsById("client-1")).thenReturn(true);

        Account saved = new Account();
        saved.setId("acc-1");
        saved.setClientId("client-1");
        saved.setType("CHECKING");
        saved.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = accountService.create(dto);

        assertEquals("client-1", result.getClientId());
        assertEquals("CHECKING", result.getType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void testCreateThrowsClientNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setClientId("x");

        when(clientRepository.existsById("x")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.create(dto));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    // --------------------------------------------------------------------------
    // GET BALANCE
    // --------------------------------------------------------------------------
    @Test
    void testGetBalanceSuccess() {
        Account acc = new Account();
        acc.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById("a1")).thenReturn(Optional.of(acc));

        BigDecimal result = accountService.getBalance("a1");

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testGetBalanceThrowsAccountNotFound() {
        when(accountRepository.findById("a1")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.getBalance("a1"));

        assertEquals("Conta não encontrada", ex.getMessage());
    }

    // --------------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------------
    @Test
    void testDeleteSuccess() {
        when(accountRepository.existsById("a1")).thenReturn(true);
        doNothing().when(accountRepository).deleteById("a1");

        accountService.delete("a1");

        verify(accountRepository, times(1)).deleteById("a1");
    }

    @Test
    void testDeleteThrowsAccountNotFound() {
        when(accountRepository.existsById("a1")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.delete("a1"));

        assertEquals("Conta não encontrada", ex.getMessage());
    }

    // --------------------------------------------------------------------------
    // FIND ALL
    // --------------------------------------------------------------------------
    @Test
    void testFindAll() {
        when(accountRepository.findAll()).thenReturn(List.of(new Account(), new Account()));

        List<Account> result = accountService.findAll();

        assertEquals(2, result.size());
    }

    // --------------------------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------------------------
    @Test
    void testUpdateSuccess() {
        AccountDTO dto = new AccountDTO();
        dto.setId("a1");
        dto.setClientId("client-1");
        dto.setType("SAVINGS");

        Account existing = new Account();
        existing.setId("a1");
        existing.setClientId("client-1");
        existing.setType("CHECKING");

        when(accountRepository.findById("a1")).thenReturn(Optional.of(existing));
        when(clientRepository.existsById("client-1")).thenReturn(true);
        when(accountRepository.save(existing)).thenReturn(existing);

        Account updated = accountService.update(dto);

        assertEquals("client-1", updated.getClientId());
        assertEquals("SAVINGS", updated.getType());
    }

    @Test
    void testUpdateThrowsAccountNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setId("a1");

        when(accountRepository.findById("a1")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.update(dto));

        assertEquals("Conta não encontrada", ex.getMessage());
    }

    @Test
    void testUpdateThrowsClientNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setId("a1");
        dto.setClientId("client-1");

        Account existing = new Account();
        existing.setId("a1");

        when(accountRepository.findById("a1")).thenReturn(Optional.of(existing));
        when(clientRepository.existsById("client-1")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.update(dto));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }
}
