package com.dustin.finledger.ledger.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;

@Service
@Transactional(readOnly = true)
public class GetAccountService {
    
    private final AccountRepository accountRepository;

    public GetAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Account handle(UUID accountIdRaw) {
        
        AccountId accountId = AccountId.of(accountIdRaw);

        Account account = accountRepository.getById(accountId)
            .orElseThrow(() -> new DomainException("Account not found: " + accountId) );


        return account;
    }
}
