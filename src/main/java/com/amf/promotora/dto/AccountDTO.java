package com.amf.promotora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountDTO {
    @NotBlank
    private String clientId;
    @NotBlank
    @Pattern(regexp = "CORRENTE|POUPANCA", message = "Tipo deve ser CORRENTE ou POUPANCA")
    private String type;
}
