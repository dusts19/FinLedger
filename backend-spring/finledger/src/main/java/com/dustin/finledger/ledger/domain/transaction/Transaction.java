package com.dustin.finledger.ledger.domain.transaction;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.common.money.Money;

/**
 * Represents a financial transaction in a double-entry ledger.
 * <p>
 * A Transaction consists of one or more {@link LedgerEntry} objects.
 * Transactions must be balanced (total debits = total credits) to be posted.
 * Transactions can be reversed to create a new, opposite transaction.
 * <p>
 * Usage example:
 * <pre>
 * Transaction tx = new Transaction(TransactionId.newId(), "Invoice payment");
 * tx.addEntry(new LedgerEntry(...));
 * tx.post();
 * </pre>
 */
public class Transaction {
    private final TransactionId id;
    private final String description;
    private final LocalDateTime timestamp;
    private final List<LedgerEntry> entries;
    private boolean posted;

    /**
     * Creates a new transaction with the given ID and description.
     * Entries are initially empty and the transaction is unposted.
     * 
     * @param id            the unique transaction ID, must not be null
     * @param description   a human-readable description of the transaction
     */
    public Transaction(TransactionId id, String description) {
        this.id = Objects.requireNonNull(id);
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.entries = new ArrayList<>();
        this.posted = false;
    }

    public TransactionId getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public List<LedgerEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    public boolean isPosted() {
        return posted;
    }

    /**
     * Adds a {@link LedgerEntry} to this transaction.
     * <p>
     * Before adding, the entry is validated against domain invariants via
     * {@link LedgerInvariant#validateNewEntry(LedgerEntry, List)}. This ensures:
     * <ul>
     *     <li>The entry is not null</li>
     *     <li>Currency consistency with existing entries</li>
     *     <li>No duplicate entry IDs</li>
     *     <li>No future timestamps</li>
     *     <li>Optional: no negative balance (if enabled)</li>
     * </ul>
     * <p>
     * Entries can only be added to transactions that have not been posted.
     * Once posted, the transaction is immutable.
     * 
     * @param entry the ledger entry to add, must not be null
     * @throws NullPointerException if {@code entry} is null
     * @throws DomainException if the transaction has already been posted
     * @throws LedgerInvariantViolation if the entry violates any ledger invariants
     */
    public void addEntry(LedgerEntry entry){
        if (posted) {
            throw new DomainException("Cannot add entry to a posted transaction");
        }
        LedgerInvariant.validateNewEntry(entry, entries);
        entries.add(entry);
    }

    /**
     * Posts this transaction, marking it as final.
     * <p>
     * The transaction can only be posted if the total debits equal the total credits.
     * After posting, the transaction becomes immutable; no further entries can be added.
     * 
     * @throws LedgerInvariantViolation if debits and credits do not balance
     */
    public void post(){
        Money totalDebits = getTotalDebits();
        Money totalCredits = getTotalCredits();

        if (!totalDebits.equals(totalCredits)) {
            throw new LedgerInvariantViolation("Debits and credits must balance");
        }
        this.posted = true;
    }

    /**
     * Creates a new transaction that reverses this transaction.
     * <p>
     * Each ledger entry in this transaction is mirrored with the debit/credit
     * side flipped and a new {@link LedgerEntryId}. The reversal transaction is
     * automatically posted.
     * 
     * @param newId the ID to assign to the reversed transaction
     * @return a new {@link Transaction} representing the reversal
     * @throws DomainException if this transaction has not been posted yet
     */
    public Transaction reverse(TransactionId newId) {
        if (!posted) {
            throw new DomainException("Cannot reverse an unposted transaction");
        }

        Transaction reversed = new Transaction(newId, "Reversal of: " + description);

        for (LedgerEntry entry : entries) {
            LedgerEntry reversedEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                entry.accountId(),
                entry.amount(),
                Instant.now(),
                entry.side() == EntrySide.DEBIT ? EntrySide.CREDIT : EntrySide.DEBIT
            );
            reversed.addEntry(reversedEntry);
        }
        reversed.post();
        return reversed;
    }

    /**
     * Creates a reversal transaction using a newly generated ID.
     * 
     * @return a new {@link Transaction} representing thre reversal
     * @throws DomainException if this transaction has not been posted yet
     */
    public Transaction reverse() {
        return reverse(TransactionId.newId());
    }

    /**
     * Computes the total of all debit entries in this transaction.
     * 
     * @return the sum of the debit amounts as a {@link Money} object
     */
    public Money getTotalDebits() {
        return entries.stream()
            .filter(e -> e.side() == EntrySide.DEBIT)
            .map(LedgerEntry::amount)
            .reduce(Money.zero(Currency.getInstance("USD")), Money::add);
    }

    /**
     * Computes the total of all credit entries in this transaction.
     * 
     * @return the sum of the credit amounts as a {@link Money} object
     */
    public Money getTotalCredits(){
        return entries.stream()
            .filter(e -> e.side() == EntrySide.CREDIT)
            .map(LedgerEntry::amount)
            .reduce(Money.zero(Currency.getInstance("USD")), Money::add);
    }
}
