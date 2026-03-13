package com.dustin.finledger.ledger.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;


@Service
@Transactional(readOnly = true)
public class CalculateAccountBalanceService {
    
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    public CalculateAccountBalanceService(AccountRepository accountRepository, JournalEntryRepository journalEntryRepository) {
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    public Money handle(UUID accountIdRaw) {
        AccountId accountId = AccountId.of(accountIdRaw);

        Account account = accountRepository.getById(accountId)
            .orElseThrow(() -> new DomainException("Account not found: " + accountId));

        return journalEntryRepository.getAccountBalance(accountId, account.getCurrency());
    }

}
