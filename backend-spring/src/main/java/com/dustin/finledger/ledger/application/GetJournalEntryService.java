package com.dustin.finledger.ledger.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;

@Service
@Transactional(readOnly = true)
public class GetJournalEntryService {
    
    private final JournalEntryRepository journalEntryRepository;

    public GetJournalEntryService(JournalEntryRepository journalEntryRepository){
        this.journalEntryRepository = journalEntryRepository;
    }

    public JournalEntry handle(JournalEntryId id) {
        return journalEntryRepository.getById(id)
            .orElseThrow(() -> new DomainException("Transaction not found: " + id));
    }

    public List<JournalEntry> handleAllByAccount(AccountId accountId) {
        return journalEntryRepository.findAllPostedByAccountId(accountId);
    }

    public List<JournalEntry> handleAll() {
        return journalEntryRepository.findAll();
    }
}
