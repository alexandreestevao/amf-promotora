package com.amf.promotora.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TransactionDTO {
    @NotBlank
    private String fromAccountId;
    @NotBlank
    private String toAccountId;
    @Positive
    private BigDecimal amount;
    private String performedBy;
}
