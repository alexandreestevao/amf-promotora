package com.amf.promotora.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String type;
    private Instant createdAt;
    private String performedBy;
}
