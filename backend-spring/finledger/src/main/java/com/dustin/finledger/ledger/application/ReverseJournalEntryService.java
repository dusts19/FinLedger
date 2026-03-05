package com.dustin.finledger.ledger.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;



@Service
@Transactional
public class ReverseJournalEntryService {
    
    private final JournalEntryRepository journalEntryRepository;

    public ReverseJournalEntryService(JournalEntryRepository journalEntryRepository){
        this.journalEntryRepository = journalEntryRepository;
    }

    public JournalEntry handle(JournalEntryId id) {
        JournalEntry original = journalEntryRepository.getById(id)
            .orElseThrow(() -> new DomainException("Journal entry not found: " + id));
        
        if (!original.isPosted()) {
            throw new DomainException("Cannot reverse an unposted journal entry: " + id);
        }
        
        JournalEntry reversal = original.reverse();

        journalEntryRepository.save(reversal);

        return reversal;
    }
}
