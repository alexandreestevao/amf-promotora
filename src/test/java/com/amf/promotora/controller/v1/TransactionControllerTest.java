package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.AmountDTO;
import com.amf.promotora.dto.TransactionDTO;
import com.amf.promotora.enums.TransactionType;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testTransfer() throws Exception {
        Transaction tx = Transaction.builder()
                .id("tx1")
                .fromAccountId("1")
                .toAccountId("2")
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.TRANSFER)
                .createdAt(Instant.now())
                .performedBy("alexandre")
                .build();

        when(transactionService.transfer(eq("1"), eq("2"), eq(BigDecimal.valueOf(50)), eq("alexandre")))
                .thenReturn(tx);

        TransactionDTO dto = new TransactionDTO();
        dto.setFromAccountId("1");
        dto.setToAccountId("2");
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setPerformedBy("alexandre");

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx1"))
                .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    void testDeposit() throws Exception {
        Transaction tx = Transaction.builder()
                .id("tx2")
                .fromAccountId(null)
                .toAccountId("1")
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .createdAt(Instant.now())
                .performedBy("alexandre")
                .build();

        when(transactionService.deposit(eq("1"), eq(BigDecimal.valueOf(100)), eq("alexandre")))
                .thenReturn(tx);

        AmountDTO dto = new AmountDTO();
        dto.setAccountId("1");
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setPerformedBy("alexandre");

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx2"))
                .andExpect(jsonPath("$.type").value("DEPOSIT"));
    }

    @Test
    void testWithdraw() throws Exception {
        Transaction tx = Transaction.builder()
                .id("tx3")
                .fromAccountId("1")
                .toAccountId(null)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.WITHDRAW)
                .createdAt(Instant.now())
                .performedBy("alexandre")
                .build();

        when(transactionService.withdraw(eq("1"), eq(BigDecimal.valueOf(50)), eq("alexandre")))
                .thenReturn(tx);

        AmountDTO dto = new AmountDTO();
        dto.setAccountId("1");
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setPerformedBy("alexandre");

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx3"))
                .andExpect(jsonPath("$.type").value("WITHDRAW"));
    }

    @Test
    void testGetTransactions() throws Exception {
        Transaction tx1 = Transaction.builder()
                .id("tx1")
                .fromAccountId(null)
                .toAccountId("1")
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .createdAt(Instant.now())
                .performedBy("alexandre")
                .build();

        Transaction tx2 = Transaction.builder()
                .id("tx2")
                .fromAccountId("1")
                .toAccountId("2")
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.TRANSFER)
                .createdAt(Instant.now())
                .performedBy("alexandre")
                .build();

        when(transactionService.getTransactions(eq("1"), any(), any()))
                .thenReturn(List.of(tx1, tx2));

        mockMvc.perform(get("/api/v1/transactions/account/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("tx1"))
                .andExpect(jsonPath("$[1].id").value("tx2"));
    }
}
