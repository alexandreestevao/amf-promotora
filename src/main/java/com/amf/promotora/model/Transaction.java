package com.amf.promotora.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;

    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String type; // TRANSFER
    private OffsetDateTime createdAt;
    private String performedBy;
}
