package com.dustin.finledger.ledger.domain.transaction;

import com.dustin.finledger.common.money.Money;

import java.time.Instant;
import java.util.Objects;

import com.dustin.finledger.common.id.AccountId;
import com.dustin.finledger.common.id.LedgerEntryId;

public record LedgerEntry(
    LedgerEntryId id,
    AccountId accountId,
    Money amount,
    Instant occurredAt,
    EntrySide side
) {
    public LedgerEntry {
        Objects.requireNonNull(id , "LedgerEntryId cannot be null");
        Objects.requireNonNull(accountId, "AccountId cannot be null");
        Objects.requireNonNull(amount, "Money cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        Objects.requireNonNull(side, "Side cannot be null");

        if (occurredAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("LedgerEntry cannot occur in the future");
        }
    }
}