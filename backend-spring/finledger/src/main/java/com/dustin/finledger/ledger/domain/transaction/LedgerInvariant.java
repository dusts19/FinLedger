package com.dustin.finledger.ledger.domain.transaction;

import java.time.Instant;
import java.util.List;

/**
 * Utility class for enforcing ledger invariants in a double-entry accouting system.
 * <p>
 * This class provides static methods to validate that new {@link LedgerEntry} instances
 * adhere to the rules required for a consistent and correct ledger:
 * <ul>
 *     <li>All entries must use the same currency</li>
 *     <li>Entry IDs must be unique</li>
 *     <li>Optional checks (currently commented out) include:
 *         <ul>
 *             <li>No negative balances</li>
 *             <li>Entry timestamps cannot be in the future</li>
 *         </ul>
 *     </li>
 * </ul>
 * <p>
 * All methods are static and the class cannot be instantiated.
 */
public final class LedgerInvariant {
    /** Private constructor to prevent instantiation. */
    private LedgerInvariant() { }

    /**
     * Validates a new ledger entry against a list of existing entries.
     * <p>
     * Throws a {@link LedgerInvariantViolation} if the new entry violates any rules:
     * <ul>
     *     <li>Currency mismatch with existing entries</li>
     *     <li>Duplicate ledger entry ID</li>
     *     <!-- <li>Negative balance (optional)</li> -->
     *     <!-- <li>Timestamp in the future (optional)</li> -->
     * </ul>
     * 
     * @param newEntry The new ledger entry to validate
     * @param existingEntries The list of existing ledger entries to validate against.
     * @throws LedgerInvariantViolation if any invariant is violated.
     */
    public static void validateNewEntry(
        LedgerEntry newEntry,
        List<LedgerEntry> existingEntries
    ) {
        ensureSameCurrency(newEntry, existingEntries);
        // ensureNoFutureTimeStamp(newEntry);
        ensureNoDuplicateEntryId(newEntry, existingEntries);
        // ensureNoNegativeBalance(newEntry, existingEntries);
    }
    
    /**
     * Ensures that the new entry does not duplicate an existing entry ID.
     * 
     * @param newEntry The new ledger entry to check.
     * @param existingEntries The list of existing ledger entries.
     * @throws LedgerInvariantViolation if the new entry's ID already exists.
    */
    public static void ensureNoDuplicateEntryId(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
        if (existingEntries.stream().anyMatch(e -> e.id().equals(newEntry.id()))) {
            throw new LedgerInvariantViolation(
                "Duplicate ledger entry ID: " + newEntry.id()
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
     * Ensures that all ledger entries use the same currency.
     * 
     * @param newEntry The new ledger entry to validate.
     * @param existingEntries The list of existing ledger entries.
     * @throws LedgerInvariantViolation if the new entry's currency does not match existing entries.
    */
   public static void ensureSameCurrency(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
       if (!existingEntries.isEmpty()) {
           var currency = existingEntries.get(0).amount().currency();
           if (!currency.equals(newEntry.amount().currency())) {
               throw new LedgerInvariantViolation(
                   "All ledger entries must use the same currency"
                );
            }
        }
    }

    /**
     * Ensures that the ledger entry's timestamp is not in the future.
     * <p>
     * Currently not used; can be enabled if future timestamps should be prohibited.
     * 
     * @param newEntry The new ledger entry to check.
     * @throws LedgerInvariantViolation if the entry's timestamp is after {@link Instant#now()}.
    */
    public static void ensureNoFutureTimeStamp(LedgerEntry newEntry) {
        if (newEntry.occurredAt().isAfter(Instant.now())) {
            throw new LedgerInvariantViolation(
                "Ledger entry timestamp cannot be in the future"
            );
        }
    }

    // Optional method for future use:
    // /**
    //  * Ensures that adding the new entry will not result in a negative ledger balance.
    //  * 
    //  * @param newEntry The new ledger entry to check.
    //  * @param existingEntries The list of existing ledger entries.
    //  * @throws LedgerInvariantViolation if balance would become negative.
    // */
    // public static void ensureNoNegativeBalance(LedgerEntry newEntry, List<LedgerEntry> existingEntries) {
    //     Money balance = Money.zero(newEntry.amount().currency());

    //     for (LedgerEntry entry : existingEntries) {
    //         balance = balance.add(entry.amount());
    //     }
    //     balance = balance.add(newEntry.amount());
    
    //     if (balance.isNegative()) {
    //         throw new LedgerInvariantViolation(
    //             "Ledger balance cannot be negative"
    //         );
    //     }
    
    //     // if (existingEntries.isEmpty() && newEntry.amount().amount().compareTo(BigDecimal.ZERO) > 0) return;
    
    //     // BigDecimal total = newEntry.amount().amount();
    //     // // BigDecimal total = new BigDecimal(0.0);
    
    //     // for (LedgerEntry entry : existingEntries) {
    //     //     total = total.add(entry.amount().amount());
    //     // }
    //     // if (total.compareTo(BigDecimal.ZERO) < 0) {
    //     //     throw new LedgerInvariantViolation(
    //     //         "Ledger balance mustt be greater than 0"
    //     //     );
    //     // }
    // }
}
