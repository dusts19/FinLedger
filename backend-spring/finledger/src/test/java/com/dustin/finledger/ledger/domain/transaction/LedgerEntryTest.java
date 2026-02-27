package com.dustin.finledger.ledger.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;

public class LedgerEntryTest {

    @Test
    void constructorSetsFieldsCorrectly() {

        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("15.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        LedgerEntry entry = new LedgerEntry(id, accountId, amount, now, EntrySide.DEBIT);

        assertEquals(id, entry.id());
        assertEquals(accountId, entry.accountId());
        assertEquals(amount, entry.amount());
        assertEquals(now, entry.occurredAt());
        assertEquals(EntrySide.DEBIT, entry.side());
    }

    @Test 
    void constructorNullIdThrows() {
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();
        
        assertThrows(NullPointerException.class, () -> 
            new LedgerEntry(null, accountId, amount, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullAccountIdThrows() {
        LedgerEntryId id = LedgerEntryId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        assertThrows(NullPointerException.class, () ->
            new LedgerEntry(id, null, amount, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullAmountThrows() {
        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Instant now = Instant.now();

        
        assertThrows(NullPointerException.class, () ->
            new LedgerEntry(id, accountId, null, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullOccuredAtThrows() {
        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        
        assertThrows(NullPointerException.class, () ->
            new LedgerEntry(id, accountId, amount, null, EntrySide.DEBIT)
        );

    }

    @Test
    void constructorNullSideThrows() {

        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        assertThrows(NullPointerException.class, () ->
            new LedgerEntry(id, accountId, amount, now, null)
        );
    }

    @Test
    void constructorFutureOccurredAtThrows() {

        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant future = Instant.now().plusSeconds(3600);

        assertThrows(IllegalArgumentException.class, () ->
            new LedgerEntry(id, accountId, amount, future, EntrySide.DEBIT)
        );
    }

    @Test
    void negativeAmountAllowedOrNot() {

        LedgerEntryId id = LedgerEntryId.newId();
        AccountId accountId = AccountId.newId();
        Money negativeAmount = new Money(new BigDecimal("-100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        LedgerEntry entry = new LedgerEntry(id, accountId, negativeAmount, now, EntrySide.DEBIT);
        assertEquals(negativeAmount, entry.amount());
    }
}
