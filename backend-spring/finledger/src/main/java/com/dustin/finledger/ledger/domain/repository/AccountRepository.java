package com.dustin.finledger.ledger.domain.repository;

import java.util.Optional;

import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> getById(AccountId id);
}
