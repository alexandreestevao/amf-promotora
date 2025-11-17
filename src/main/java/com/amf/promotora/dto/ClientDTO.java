package com.amf.promotora.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class ClientDTO {
    @NotBlank
    private String fullName;
    @NotBlank
    private String cpf;
    @Past
    private LocalDate birthDate;
}
