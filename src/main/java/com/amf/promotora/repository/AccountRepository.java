package com.amf.promotora.repository;

import com.amf.promotora.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByNumber(String number);
}
