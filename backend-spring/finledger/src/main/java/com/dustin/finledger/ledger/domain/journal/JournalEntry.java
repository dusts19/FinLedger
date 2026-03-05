package com.dustin.finledger.ledger.domain.journal;


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
 * A Transaction consists of one or more {@link JournalLine} objects.
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
public class JournalEntry {
    private final JournalEntryId id;
    private final String description;
    private final LocalDateTime timestamp;
    private final List<JournalLine> lines;
    private boolean posted;

    public static JournalEntry create(String description) {
        return new JournalEntry(JournalEntryId.newId(), description);
    }

    public static JournalEntry fromId(JournalEntryId id) {
        Objects.requireNonNull(id, "JournalEntryId cannot be null");
        return new JournalEntry(id, "");
    }

    /**
     * Creates a new transaction with the given ID and description.
     * Entries are initially empty and the transaction is unposted.
     * 
     * @param id            the unique transaction ID, must not be null
     * @param description   a human-readable description of the transaction
     */
    public JournalEntry(JournalEntryId id, String description) {
        this.id = Objects.requireNonNull(id);
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.lines = new ArrayList<>();
        this.posted = false;
    }


    /**
     * Adds a {@link JournalLine} to this transaction.
     * <p>
     * Before adding, the entry is validated against domain invariants via
     * {@link JournalInvariant#validateNewEntry(JournalLine, List)}. This ensures:
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
     * @param line the ledger entry to add, must not be null
     * @throws NullPointerException if {@code entry} is null
     * @throws DomainException if the journal entry has already been posted
     * @throws JournalInvariantViolation if the entry violates any ledger invariants
     */
    public void addLine(JournalLine line){
        if (posted) {
            throw new DomainException("Cannot add entry to a posted journal entry");
        }
        // JournalInvariant.validateNewEntry(entry, entries);
        ensureNotNull(line);
        ensureNoDuplicateEntryId(line);
        ensureSameCurrency(line);
        lines.add(line);
    }

    /**
     * Posts this transaction, marking it as final.
     * <p>
     * The transaction can only be posted if the total debits equal the total credits.
     * After posting, the transaction becomes immutable; no further entries can be added.
     * 
     * @throws JournalInvariantViolation if debits and credits do not balance
     */
    public void post(){
        Money totalDebits = getTotalDebits();
        Money totalCredits = getTotalCredits();

        if (!totalDebits.equals(totalCredits)) {
            throw new JournalInvariantViolation("Debits and credits must balance");
        }
        this.posted = true;
    }

    /**
     * Creates a new transaction that reverses this transaction.
     * <p>
     * Each ledger entry in this transaction is mirrored with the debit/credit
     * side flipped and a new {@link JournalLineId}. The reversal transaction is
     * automatically posted.
     * 
     * @param newId the ID to assign to the reversed transaction
     * @return a new {@link JournalEntry} representing the reversal
     * @throws DomainException if this transaction has not been posted yet
     */
    public JournalEntry reverse(JournalEntryId newId) {
        if (!posted) {
            throw new DomainException("Cannot reverse an unposted journal");
        }

        JournalEntry reversed = new JournalEntry(newId, "Reversal of: " + description);

        for (JournalLine line : lines) {
            JournalLine reversedEntry = new JournalLine(
                JournalLineId.newId(),
                line.accountId(),
                line.amount(),
                Instant.now(),
                line.side() == EntrySide.DEBIT ? EntrySide.CREDIT : EntrySide.DEBIT
            );
            reversed.addLine(reversedEntry);
        }
        reversed.post();
        return reversed;
    }

    /**
     * Creates a reversal transaction using a newly generated ID.
     * 
     * @return a new {@link JournalEntry} representing the reversal
     * @throws DomainException if this transaction has not been posted yet
     */
    public JournalEntry reverse() {
        return reverse(JournalEntryId.newId());
    }

    
    public JournalEntryId getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public List<JournalLine> getLines() {
        return Collections.unmodifiableList(lines);
    }
    public boolean isPosted() {
        return posted;
    }

    /**
     * Computes the total of all debit entries in this transaction.
     * 
     * @return the sum of the debit amounts as a {@link Money} object
     */
    public Money getTotalDebits() {
        if (lines.isEmpty()) {
            throw new IllegalStateException("No lines to calculate debits");
        }
        Currency currency = lines.get(0).amount().currency();
        return lines.stream()
            .filter(l -> l.side() == EntrySide.DEBIT)
            .map(JournalLine::amount)
            .reduce(Money.zero(currency), Money::add);
    }

    /**
     * Computes the total of all credit entries in this transaction.
     * 
     * @return the sum of the credit amounts as a {@link Money} object
     */
    public Money getTotalCredits(){
        if (lines.isEmpty()) {
            throw new IllegalStateException("No lines to calculate credits");
        }
        Currency currency = lines.get(0).amount().currency();
        return lines.stream()
            .filter(l -> l.side() == EntrySide.CREDIT)
            .map(JournalLine::amount)
            .reduce(Money.zero(currency), Money::add);
    }
    

    private void ensureNotNull(JournalLine line) {
        Objects.requireNonNull(line, "JournalLine cannot be null");
    }

    
    /**
     * Ensures that the new entry does not duplicate an existing entry ID.
     * 
     * @param newLine The new ledger entry to check.
     * @throws JournalInvariantViolation if the new entry's ID already exists.
    */
    private void ensureNoDuplicateEntryId(JournalLine newLine) {
        if (lines.stream().anyMatch(l -> l.id().equals(newLine.id()))) {
            throw new JournalInvariantViolation(
                "Duplicate journal line ID: " + newLine.id()
            );
        }
        // if (existingEntries.isEmpty()) return;
        
        // for (LedgerEntry entry : existingEntries) {
            //     if (newEntry.id().equals(entry.id())) {
                //         throw new LedgerInvariantViolation(
                    //             "Duplicate ledger entry ID: " + newEntry.id()
                    //         );
                    //     }
                    // }
    }
    
    /**
     * Ensures that all journal lines use the same currency.
     * 
     * @param newLine The new journal line to validate.
     * @throws JournalInvariantViolation if the new line's currency does not match existing entries.
    */
    private void ensureSameCurrency(JournalLine newLine) {
        ensureNotNull(newLine);
        Money amount = newLine.amount();
        if (amount == null) throw new JournalInvariantViolation("JournalLine amount cannot be null");
        

        if (!lines.isEmpty()) {
           Currency currency = lines.get(0).amount().currency();
           if (!currency.equals(newLine.amount().currency())) {
               throw new JournalInvariantViolation(
                   "All journal lines must use the same currency"
                );
            }
        }
    }
    
    // Optional method for future use:
    // /**
    //  * Ensures that the ledger entry's timestamp is not in the future.
    //  * <p>
    //  * Currently not used; can be enabled if future timestamps should be prohibited.
    //  * 
    //  * @param newEntry The new ledger entry to check.
    //  * @throws JournalInvariantViolation if the entry's timestamp is after {@link Instant#now()}.
    // */
    // private void ensureNoFutureTimeStamp(JournalLine newEntry) {
    //     if (newEntry.occurredAt().isAfter(Instant.now())) {
    //         throw new JournalInvariantViolation(
    //             "Ledger entry timestamp cannot be in the future"
    //         );
    //     }
    // }

    // Optional method for future use:
    // /**
    //  * Ensures that adding the new entry will not result in a negative ledger balance.
    //  * 
    //  * @param newEntry The new ledger entry to check.
    //  * @throws LedgerInvariantViolation if balance would become negative.
    // */
    // public static void ensureNoNegativeBalance(LedgerEntry newEntry) {
    //     Money balance = Money.zero(newEntry.amount().currency());

    //     for (LedgerEntry entry : entries) {
    //         balance = balance.add(entry.amount());
    //     }
    //     balance = balance.add(newEntry.amount());
    
    //     if (balance.isNegative()) {
    //         throw new LedgerInvariantViolation(
    //             "Ledger balance cannot be negative"
    //         );
    //     }
    
    //     // if (entries.isEmpty() && newEntry.amount().amount().compareTo(BigDecimal.ZERO) > 0) return;
    
    //     // BigDecimal total = newEntry.amount().amount();
    //     // // BigDecimal total = new BigDecimal(0.0);
    
    //     // for (LedgerEntry entry : entries) {
    //     //     total = total.add(entry.amount().amount());
    //     // }
    //     // if (total.compareTo(BigDecimal.ZERO) < 0) {
    //     //     throw new LedgerInvariantViolation(
    //     //         "Ledger balance mustt be greater than 0"
    //     //     );
    //     // }
    // }
}
