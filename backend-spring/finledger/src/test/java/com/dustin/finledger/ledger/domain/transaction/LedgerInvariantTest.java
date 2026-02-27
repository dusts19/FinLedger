package com.dustin.finledger.ledger.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;

class LedgerInvariantTest {
    
    @Test
    void nullLedgerEntryThrows() {
        assertThrows(NullPointerException.class, () ->
            LedgerInvariant.validateNewEntry(null, List.of())   
        );
    }


    @Test
    void duplicateIdThrows() {
        LedgerEntry entry1 = new LedgerEntry(
            LedgerEntryId.newId(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        
        LedgerEntry entry2 = new LedgerEntry(
            entry1.id(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(50), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );

        assertThrows(LedgerInvariantViolation.class, () ->
            LedgerInvariant.validateNewEntry(entry2, List.of(entry1))
        );

    }

    @Test
    void differentCurrencyThrows() {
        LedgerEntry entry1 = new LedgerEntry(
            LedgerEntryId.newId(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        
        LedgerEntry entry2 = new LedgerEntry(
            LedgerEntryId.newId(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(50), Currency.getInstance("EUR")),
            Instant.now(),
            EntrySide.CREDIT
        );

        assertThrows(LedgerInvariantViolation.class, () ->
            LedgerInvariant.validateNewEntry(entry2, List.of(entry1))
        );

    }

    @Test
    void validEntryDoesNotThrow() {
        LedgerEntry entry1 = new LedgerEntry(
            LedgerEntryId.newId(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        
        LedgerEntry entry2 = new LedgerEntry(
            LedgerEntryId.newId(),
            AccountId.newId(),
            new Money(BigDecimal.valueOf(50), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );

        assertDoesNotThrow(() ->
            LedgerInvariant.validateNewEntry(entry2, List.of(entry1))
        );

    }

}
