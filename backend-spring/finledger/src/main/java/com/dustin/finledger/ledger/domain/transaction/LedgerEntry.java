package com.dustin.finledger.ledger.domain.transaction;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;

import java.time.Instant;
import java.util.Objects;


/**
 * A single entry in a financial ledger.
 * Represents either a debit or a credit to a specific amount for a certain amount.
 * 
 * Each LedgerEntry is part of a Transaction and is used to calculate
 * debits and credits to ensure the ledger remains balanced.
 * 
 * Immutable. Validates that:
 * - id, accountId, amount, occurredAt, side are non-null
 * - occurredAt is not in the future
 */
public record LedgerEntry(
    LedgerEntryId id,
    AccountId accountId,
    Money amount,
    Instant occurredAt,
    EntrySide side
) {
    public LedgerEntry {
        Objects.requireNonNull(id, "LedgerEntry id cannot be null");
        Objects.requireNonNull(accountId, "LedgerEntry accountId cannot be null");
        Objects.requireNonNull(amount, "LedgerEntry amount cannot be null");
        Objects.requireNonNull(occurredAt, "LedgerEntry occurredAt cannot be null");
        Objects.requireNonNull(side, "LedgerEntry side cannot be null");

        if (occurredAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("LedgerEntry cannot occur in the future");
        }
    }
}