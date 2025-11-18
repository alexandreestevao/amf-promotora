package com.amf.promotora.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AmountDTO {
    @NotNull
    private String accountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String performedBy;
}
