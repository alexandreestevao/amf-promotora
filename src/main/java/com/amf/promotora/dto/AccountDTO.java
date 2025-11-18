package com.amf.promotora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO para criação de conta bancária")
public class AccountDTO {

    @NotBlank
    @Schema(description = "ID do cliente proprietário da conta", example = "123e4567-e89b-12d3-a456-426614174000")
    private String clientId;

    @NotBlank
    @Pattern(regexp = "CORRENTE|POUPANCA", message = "Tipo deve ser CORRENTE ou POUPANCA")
    @Schema(description = "Tipo da conta (CORRENTE ou POUPANCA)", example = "CORRENTE")
    private String type;
}
