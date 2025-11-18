package com.amf.promotora.service;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Account;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private ClientRepository clientRepository;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        clientRepository = mock(ClientRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        accountService = new AccountService(accountRepository, clientRepository, transactionRepository);
    }

    @Test
    void createAccount_success() {
        AccountDTO dto = new AccountDTO();
        dto.setClientId("client1");
        dto.setType("CORRENTE");

        when(clientRepository.existsById("client1")).thenReturn(true);

        Account savedAccount = new Account();
        savedAccount.setId("acc1");
        savedAccount.setClientId("client1");
        savedAccount.setType("CORRENTE");
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.create(dto);

        assertNotNull(result);
        assertEquals("client1", result.getClientId());
        assertEquals("CORRENTE", result.getType());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals("client1", captor.getValue().getClientId());
    }

    @Test
    void createAccount_clientNotFound_throwsException() {
        AccountDTO dto = new AccountDTO();
        dto.setClientId("clientNotExist");
        dto.setType("POUPANCA");

        when(clientRepository.existsById("clientNotExist")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.create(dto));
        assertEquals("Cliente não encontrado", ex.getMessage());

        verify(accountRepository, never()).save(any());
    }

    @Test
    void getBalance_success() {
        Account account = new Account();
        account.setId("acc1");
        account.setBalance(BigDecimal.valueOf(200));

        when(accountRepository.findById("acc1")).thenReturn(Optional.of(account));

        BigDecimal balance = accountService.getBalance("acc1");
        assertEquals(BigDecimal.valueOf(200), balance);
    }

    @Test
    void getBalance_accountNotFound_throwsException() {
        when(accountRepository.findById("accNotExist")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.getBalance("accNotExist"));
        assertEquals("Conta não encontrada", ex.getMessage());
    }
}
