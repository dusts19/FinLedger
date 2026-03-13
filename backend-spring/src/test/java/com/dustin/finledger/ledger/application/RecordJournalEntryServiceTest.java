package com.dustin.finledger.ledger.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.application.dto.JournalLineCommand;
import com.dustin.finledger.ledger.application.dto.RecordJournalEntryCommand;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.journal.JournalInvariantViolation;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecordJournalEntryServiceTest {
    
    @Mock
    private JournalEntryRepository journalEntryRepository;
    
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RecordJournalEntryService service;

    private final AccountId usdAccountId = AccountId.newId();

    private Account usdAccount;

    @BeforeEach
    void setUp(){
        // MockitoAnnotations.openMocks(this);
        usdAccount = new Account(
            usdAccountId, 
            "Cash", 
            AccountType.ASSET, 
            Currency.getInstance("USD")
        );
    }

    @Test
    void handle_shouldPostAndSaveTransaction_whenAllLinesValid() {
        when(accountRepository.getById(eq(usdAccountId)))
            .thenReturn(Optional.of(usdAccount));

        JournalLineCommand debit = new JournalLineCommand(
            usdAccountId,
            new BigDecimal("100.00"),
            "USD",
            "DEBIT", 
            Instant.now()
        );
        JournalLineCommand credit = new JournalLineCommand(
            usdAccountId,
            new BigDecimal("100.00"),
            "USD",
            "CREDIT", 
            Instant.now()
        );

        RecordJournalEntryCommand command = new RecordJournalEntryCommand(
            "Test Transaction", 
            List.of(debit, credit)
        );

        JournalEntryId entryId = assertDoesNotThrow(() -> service.handle(command));

        assertThat(entryId).isNotNull();
        verify(journalEntryRepository, times(1)).save(any(JournalEntry.class));
        verify(accountRepository, times(2)).getById(usdAccount.getId());
    }

    @Test
    void handle_shouldThrow_whenAccountNotFound() {
        when(accountRepository.getById(any())).thenReturn(Optional.empty());

        JournalLineCommand line = new JournalLineCommand(
            AccountId.newId(),
            new BigDecimal("50.00"),
            "USD",
            "DEBIT",
            Instant.now()
        );

        RecordJournalEntryCommand command = new RecordJournalEntryCommand(
            "Missing account test",
            List.of(line, line)
        );

        assertThrows(DomainException.class, () -> service.handle(command));
        verify(journalEntryRepository, never()).save(any());
    }

    @Test
    void handle_shouldThrow_whenCurrencyMismatch() {
        when(accountRepository.getById(eq(usdAccountId)))
            .thenReturn(Optional.of(usdAccount));
        
        JournalLineCommand debit = new JournalLineCommand(
            usdAccountId,
            new BigDecimal("100.00"),
            "USD",
            "DEBIT",
            Instant.now()
        );
        
        JournalLineCommand credit = new JournalLineCommand(
            usdAccountId,
            new BigDecimal("100.00"),
            "EUR",
            "CREDIT",
            Instant.now()
        );

        RecordJournalEntryCommand command = new RecordJournalEntryCommand(
            "Currency mismatch",
            List.of(debit, credit)
        );

        assertThrows(DomainException.class, () -> service.handle(command));
        verify(journalEntryRepository, never()).save(any());
    }

    @Test
    void handle_shouldThrow_whenPostingUnbalancedTransaction() {
        when(accountRepository.getById(usdAccount.getId()))
            .thenReturn(Optional.of(usdAccount));

        
        JournalLineCommand debit1 = new JournalLineCommand(
            usdAccount.getId(),
            new BigDecimal("100.00"),
            "USD",
            "DEBIT",
            Instant.now()
        );
        JournalLineCommand debit2 = new JournalLineCommand(
            usdAccount.getId(),
            new BigDecimal("100.00"),
            "USD",
            "DEBIT",
            Instant.now()
        );

        RecordJournalEntryCommand command = new RecordJournalEntryCommand(
            "Unbalanced transaction",
            List.of(debit1, debit2)
        );

        assertThrows(JournalInvariantViolation.class, () -> service.handle(command));
        verify(journalEntryRepository, never()).save(any());

    }
}
