package com.amf.promotora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO para transferências entre contas")
public class TransactionDTO {

    @NotBlank
    @Schema(description = "ID da conta origem", example = "acc123")
    private String fromAccountId;

    @NotBlank
    @Schema(description = "ID da conta destino", example = "acc456")
    private String toAccountId;

    @NotNull
    @Positive
    @Schema(description = "Valor da transação", example = "100.50")
    private BigDecimal amount;

    @NotBlank
    @Schema(description = "Nome de quem realizou a transação", example = "alexandre")
    private String performedBy;
}
