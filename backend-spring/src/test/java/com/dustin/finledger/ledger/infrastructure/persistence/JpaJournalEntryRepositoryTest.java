package com.dustin.finledger.ledger.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalLine;
import com.dustin.finledger.ledger.domain.journal.JournalLineId;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class JpaJournalEntryRepositoryTest {
    
    @Autowired
    private EntityManager em;

    private JpaJournalEntryRepository repository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        repository = new JpaJournalEntryRepository(em);

        testAccount = new Account(
            AccountId.newId(),
            "Cash",
            AccountType.ASSET,
            Currency.getInstance("USD")
        );
    }

    @Test
    void saveAndGetById_shouldPersistAndReturnJournalEntry() {
        JournalEntry entry = JournalEntry.create("Test Entry");

        
        JournalLine debit = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        JournalLine credit = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );

        entry.addLine(debit);
        entry.addLine(credit);
        entry.post();

        repository.save(entry);

        em.flush();
        em.clear();

        JournalEntry retrieved = repository.getById(entry.getId()).orElseThrow();

        assertThat(retrieved.getId()).isEqualTo(entry.getId());
        assertThat(retrieved.getLines()).hasSize(2);
    }

    @Test
    void findAllPostedByAccountId_shouldReturnOnlyPostedEntriedForAccount() {
        JournalEntry posted = JournalEntry.create("Posted Entry");
        JournalEntry unposted = JournalEntry.create("Unposted Entry");

    
        JournalLine debitEntry1 = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        JournalLine creditEntry1 = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );

        posted.addLine(debitEntry1);
        posted.addLine(creditEntry1);
        posted.post();


        JournalLine debitEntry2 = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("50.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        JournalLine creditEntry2 = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("50.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );
        
        unposted.addLine(debitEntry2);
        unposted.addLine(creditEntry2);

        repository.save(posted);
        repository.save(unposted);

        em.flush();
        em.clear();

        List<JournalEntry> results = repository.findAllPostedByAccountId(testAccount.getId());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Posted Entry");
    }

    @Test
    void getAccountBalance_shouldReturnCorrectBalance() {
        JournalEntry entry = JournalEntry.create("Balance Test");

        
        JournalLine debit = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        JournalLine credit = new JournalLine(
            JournalLineId.newId(),
            testAccount.getId(),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );

        entry.addLine(debit);
        entry.addLine(credit);
        entry.post();

        repository.save(entry);

        em.flush();
        em.clear();

        Money balance = repository.getAccountBalance(testAccount.getId(), testAccount.getCurrency());
        assertThat(balance.amount()).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    void getAccountBalance_shouldReturnZero_whenNoEntriesExist() {
        em.flush();
        em.clear();

        Money balance = repository.getAccountBalance(testAccount.getId(), testAccount.getCurrency());
        assertThat(balance.amount()).isEqualByComparingTo("0.00");
        assertThat(balance.currency()).isEqualTo(testAccount.getCurrency());
    }
}
