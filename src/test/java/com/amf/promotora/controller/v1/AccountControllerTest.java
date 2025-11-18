package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.dto.BalanceDTO;
import com.amf.promotora.model.Account;
import com.amf.promotora.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void testCreateAccount() throws Exception {
        Account account = Account.builder()
                .id("acc1")
                .clientId("client1")
                .number("123456")
                .type("CORRENTE")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountService.create(any(AccountDTO.class))).thenReturn(account);

        AccountDTO dto = new AccountDTO();
        dto.setClientId("client1");
        dto.setType("CORRENTE");

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("acc1"))
                .andExpect(jsonPath("$.type").value("CORRENTE"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void testGetBalance() throws Exception {
        when(accountService.getBalance("acc1")).thenReturn(BigDecimal.valueOf(150));

        mockMvc.perform(get("/api/v1/accounts/acc1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acc1"))
                .andExpect(jsonPath("$.balance").value(150));
    }
}
