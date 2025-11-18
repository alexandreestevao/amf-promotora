package com.amf.promotora.repository;

import com.amf.promotora.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    @Query("{$and: [ {$or: [{'fromAccountId': ?0}, {'toAccountId': ?0}]}, {'createdAt': {$gte: ?1, $lte: ?2}} ]}")
    List<Transaction> findByAccountIdAndCreatedAtBetween(String accountId, Instant start, Instant end);

    @Query("{$or: [{'fromAccountId': ?0}, {'toAccountId': ?0}]}")
    List<Transaction> findByAccountId(String accountId);
}
