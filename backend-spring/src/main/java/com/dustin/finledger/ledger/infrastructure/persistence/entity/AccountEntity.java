package com.dustin.finledger.ledger.infrastructure.persistence.entity;

import java.util.UUID;

import com.dustin.finledger.ledger.domain.account.AccountStatus;
import com.dustin.finledger.ledger.domain.account.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "account")
public class AccountEntity {
    
    @Id
    @Column(name = "id", nullable = false)    
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)    
    private AccountType type;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    protected AccountEntity() {};

    public AccountEntity(UUID id, String name, AccountType type, String currencyCode, AccountStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currencyCode = currencyCode;
        this.status = status;
    }


    public UUID getId() {  return id;  }
    public String getName() { return name; }
    public AccountType getType() { return type; }
    public String getCurrencyCode() { return currencyCode; }
    public AccountStatus getStatus() { return status; }
}
