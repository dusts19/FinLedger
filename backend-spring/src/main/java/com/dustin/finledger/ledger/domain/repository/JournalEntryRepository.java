package com.dustin.finledger.ledger.domain.repository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;

public interface JournalEntryRepository {
    void save(JournalEntry entry);
    Optional<JournalEntry> getById(JournalEntryId id);
    List<JournalEntry> findAllPostedByAccountId(AccountId accountId);
    List<JournalEntry> findAll();
    Money getAccountBalance(AccountId accountId, Currency currency);
    
}
