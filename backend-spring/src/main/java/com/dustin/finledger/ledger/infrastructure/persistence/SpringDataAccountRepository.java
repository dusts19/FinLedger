package com.dustin.finledger.ledger.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dustin.finledger.ledger.infrastructure.persistence.entity.AccountEntity;

public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, UUID>{
    
    Optional<AccountEntity> findByName(String name);
}
