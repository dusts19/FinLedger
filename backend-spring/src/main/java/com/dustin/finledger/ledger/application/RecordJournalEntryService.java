package com.dustin.finledger.ledger.application;

import java.time.Instant;
import java.util.Currency;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.application.dto.JournalLineCommand;
import com.dustin.finledger.ledger.application.dto.RecordJournalEntryCommand;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.journal.JournalLine;
import com.dustin.finledger.ledger.domain.journal.JournalLineId;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;


@Service
@Transactional
public class RecordJournalEntryService {
    
    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    
    public RecordJournalEntryService(JournalEntryRepository journalEntryRepository, AccountRepository accountRepository) {
        this.journalEntryRepository = journalEntryRepository;
        this.accountRepository = accountRepository;
    }

    public JournalEntryId handle(RecordJournalEntryCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        JournalEntry entry = JournalEntry.create(command.description());

        for (JournalLineCommand lineCmd : command.lines()) {

            AccountId accountId = lineCmd.accountId();
            
            Account account = accountRepository.getById(accountId)
                .orElseThrow(() -> new DomainException(
                    "Account not found:" + lineCmd.accountId()
            ));
            
            account.ensureCanPost();

            Currency currency = Currency.getInstance(lineCmd.currency());
                
            if (!account.getCurrency().equals(currency)) {
                throw new DomainException("Currency mismatch for account: " + account.getId());
            }

            Money money = Money.of(lineCmd.amount(), currency);
            
            Instant occurredAt = lineCmd.occurredAt() != null
                ? lineCmd.occurredAt()
                : Instant.now();
            
            EntrySide side = EntrySide.valueOf(lineCmd.side().toUpperCase());

            JournalLine line = new JournalLine(
                JournalLineId.newId(),
                account.getId(),
                money,
                occurredAt,
                side
            );

            entry.addLine(line);
        }
        entry.post();

        journalEntryRepository.save(entry);


        return entry.getId();
    }
    
}
