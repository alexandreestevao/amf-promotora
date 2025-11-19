package com.amf.promotora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Informações de saldo da conta")
public class BalanceDTO {

    @Schema(description = "ID da conta bancária", example = "acc123")
    private String accountId;

    @Schema(description = "Saldo atual da conta", example = "1500.75")
    private BigDecimal balance;
}
