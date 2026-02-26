package com.dustin.finledger.ledger.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.id.AccountId;
import com.dustin.finledger.common.id.LedgerEntryId;
import com.dustin.finledger.common.money.Money;

public class LedgerEntryTest {
    
    @Test
    void testLedgerEntryValues() {
        LedgerEntryId id = new LedgerEntryId(UUID.randomUUID());
        AccountId accountId = new AccountId(UUID.randomUUID());
        Money amount = new Money(new BigDecimal("15.00"), Currency.getInstance("USD"));
        Instant now = Instant.now();

        LedgerEntry entry = new LedgerEntry(id, accountId, amount, now);

        assertEquals(id, entry.id());
        assertEquals(accountId, entry.accountId());
        assertEquals(amount, entry.amount());
        assertEquals(now, entry.occurredAt());
    }

}
