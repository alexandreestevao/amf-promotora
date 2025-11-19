package com.amf.promotora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO para operações de depósito ou saque")
public class AmountDTO {

    @NotNull
    @Schema(description = "ID da conta afetada", example = "acc123")
    private String accountId;

    @NotNull
    @Positive
    @Schema(description = "Valor da operação", example = "200.00")
    private BigDecimal amount;

    @Schema(description = "Usuário que executou a operação", example = "alexandre")
    private String performedBy;
}
