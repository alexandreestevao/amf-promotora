package com.amf.promotora.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;

    private String clientId;

    private String number;

    private String type; // CORRENTE or POUPANCA

    private BigDecimal balance = BigDecimal.ZERO;
}
