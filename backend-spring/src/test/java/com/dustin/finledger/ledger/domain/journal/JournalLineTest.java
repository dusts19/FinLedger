package com.dustin.finledger.ledger.domain.journal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;

public class JournalLineTest {

    @Test
    void constructorSetsFieldsCorrectly() {

        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("15.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        JournalLine entry = new JournalLine(id, accountId, amount, now, EntrySide.DEBIT);

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
            new JournalLine(null, accountId, amount, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullAccountIdThrows() {
        JournalLineId id = JournalLineId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        assertThrows(NullPointerException.class, () ->
            new JournalLine(id, null, amount, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullAmountThrows() {
        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Instant now = Instant.now();

        
        assertThrows(NullPointerException.class, () ->
            new JournalLine(id, accountId, null, now, EntrySide.DEBIT)
        );
    }

    @Test
    void constructorNullOccuredAtThrows() {
        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        
        assertThrows(NullPointerException.class, () ->
            new JournalLine(id, accountId, amount, null, EntrySide.DEBIT)
        );

    }

    @Test
    void constructorNullSideThrows() {

        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        assertThrows(NullPointerException.class, () ->
            new JournalLine(id, accountId, amount, now, null)
        );
    }

    @Test
    void constructorFutureOccurredAtThrows() {

        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Instant future = Instant.now().plusSeconds(3600);

        assertThrows(IllegalArgumentException.class, () ->
            new JournalLine(id, accountId, amount, future, EntrySide.DEBIT)
        );
    }

    @Test
    void negativeAmountAllowedOrNot() {

        JournalLineId id = JournalLineId.newId();
        AccountId accountId = AccountId.newId();
        Money negativeAmount = new Money(new BigDecimal("-100.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        JournalLine entry = new JournalLine(id, accountId, negativeAmount, now, EntrySide.DEBIT);
        assertEquals(negativeAmount, entry.amount());
    }
}
