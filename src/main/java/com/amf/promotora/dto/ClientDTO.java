package com.amf.promotora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Schema(description = "DTO para cadastro de cliente")
public class ClientDTO {

    @NotBlank
    @JsonProperty("name")
    @Schema(description = "Nome completo do cliente", example = "João da Silva")
    private String fullName;

    @NotBlank
    @Schema(description = "CPF do cliente (único)", example = "123.456.789-00")
    private String cpf;

    @Past
    @Schema(description = "Data de nascimento do cliente", example = "1980-01-01")
    private LocalDate birthDate;
}
