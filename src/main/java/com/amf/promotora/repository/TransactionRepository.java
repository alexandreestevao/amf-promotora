package com.amf.promotora.repository;

import com.amf.promotora.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByFromAccountIdOrToAccountIdAndCreatedAtBetween(
        String fromId, String toId, OffsetDateTime start, OffsetDateTime end);
}
