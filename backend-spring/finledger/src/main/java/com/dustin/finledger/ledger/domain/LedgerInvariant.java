package com.dustin.finledger.ledger.domain;

import java.time.Instant;
import java.util.List;


import com.dustin.finledger.common.money.Money;

public class LedgerInvariant {
    
    public static void validateNewEntry(
        LedgerEntry newEntry,
        List<LedgerEntry> existingEntries
    ) {
        ensureSameCurrency(newEntry, existingEntries);
        ensureNoFutureTimeStamp(newEntry);
        ensureNoDuplicateEntryId(newEntry, existingEntries);
        ensureNoNegativeBalance(newEntry, existingEntries);
    }

    
    public static void ensureSameCurrency(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
        if (existingEntries.isEmpty()) return;

        Money existingMoney = existingEntries.get(0).amount();
        if (existingMoney.currency().equals(newEntry.amount().currency())) {
            throw new LedgerInvariantViolation(
                "All ledger entries must use the same currency"
            );
        }
    }

    public static void ensureNoFutureTimeStamp(LedgerEntry newEntry) {
        if (newEntry.occurredAt().isAfter(Instant.now())) {
            throw new LedgerInvariantViolation(
                "Ledger entry timestamp cannot be in the future"
            );
        }
    }
    
    public static void ensureNoDuplicateEntryId(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
        if (existingEntries.isEmpty()) return;
        
        for (LedgerEntry entry : existingEntries) {
            if (newEntry.id().equals(entry.id())) {
                throw new LedgerInvariantViolation(
                    "Duplicate ledger entry ID: " + newEntry.id()
                );
            }
        }
    }

    public static void ensureNoNegativeBalance(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
        Money balance = Money.zero(newEntry.amount().currency());

        for (LedgerEntry entry : existingEntries) {
            balance = balance.add(entry.amount());
        }
        balance = balance.add(newEntry.amount());

        if (balance.isNegative()) {
            throw new LedgerInvariantViolation(
                "Ledger balance cannot be negative"
            );
        }

        // if (existingEntries.isEmpty() && newEntry.amount().amount().compareTo(BigDecimal.ZERO) > 0) return;

        // BigDecimal total = newEntry.amount().amount();
        // // BigDecimal total = new BigDecimal(0.0);

        // for (LedgerEntry entry : existingEntries) {
        //     total = total.add(entry.amount().amount());
        // }
        // if (total.compareTo(BigDecimal.ZERO) < 0) {
        //     throw new LedgerInvariantViolation(
        //         "Ledger balance mustt be greater than 0"
        //     );
        // }
    }
}
