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
    @Schema(description = "ID da conta de origem",
            example = "acc001")
    private String fromAccountId;

    @NotBlank
    @Schema(description = "ID da conta de destino",
            example = "acc002")
    private String toAccountId;

    @NotNull
    @Positive
    @Schema(description = "Valor da transferência", example = "250.00")
    private BigDecimal amount;

    @NotBlank
    @Schema(description = "Usuário que executou a transferência",
            example = "alexandre")
    private String performedBy;
}
