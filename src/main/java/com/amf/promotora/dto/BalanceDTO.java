package com.amf.promotora.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDTO {
    private String accountId;
    private BigDecimal balance;
}
