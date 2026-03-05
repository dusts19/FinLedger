package com.dustin.finledger.ledger.domain.journal;

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
public record JournalLine(
    JournalLineId id,
    AccountId accountId,
    Money amount,
    Instant occurredAt,
    EntrySide side
) {
    public JournalLine {
        Objects.requireNonNull(id, "JournalLine id cannot be null");
        Objects.requireNonNull(accountId, "JournalLine accountId cannot be null");
        Objects.requireNonNull(amount, "JournalLine amount cannot be null");
        Objects.requireNonNull(occurredAt, "JournalLine occurredAt cannot be null");
        Objects.requireNonNull(side, "JournalLine side cannot be null");

        if (occurredAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("JournalLine cannot occur in the future");
        }
    }
}