package com.amf.promotora.model;

import com.amf.promotora.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private Instant createdAt;
    private String performedBy;
}
