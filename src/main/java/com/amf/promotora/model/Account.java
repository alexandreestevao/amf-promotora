package com.amf.promotora.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    private String clientId;
    private String number;
    private String type;
    private BigDecimal balance = BigDecimal.ZERO;
}
