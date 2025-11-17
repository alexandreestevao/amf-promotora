package com.amf.promotora.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Document(collection = "clients")
public class Client {
    @Id
    private String id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String cpf;

    @Past
    private LocalDate birthDate;
}
