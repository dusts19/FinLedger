package com.dustin.finledger.ledger.domain.transaction;

import java.util.Objects;
import java.util.UUID;


/**
 * Value object representing the unique identifier of a {@link LedgerEntry}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be generated automatically with {@link #newId()}</li>
 *     <li>Can be created from a string representation of a UUID with {@link #fromString(String)}</li>
 * </ul>
 * <p>
 * This is an immutable identifier suitable for use in a financial ledger system
 * where each ledger entry must have a globally unique ID.
 */
public record LedgerEntryId(UUID id) {
    /**
     * Canonical constructor that validates the ID is not null.
     * 
     * @param id the UUID represents this ledger entry's identifier
     * @throws NullPointerException if {@code id} is null
     */
    public LedgerEntryId {
        Objects.requireNonNull(id, "LedgerEntryId cannot be null");
    }
    
    /**
     * Generates a new unique ledger entry ID.
     * 
     * @return a new {@code LedgerEntryId} with a randomly generated UUID
     */
    public static LedgerEntryId newId() {
        return new LedgerEntryId(UUID.randomUUID());
    }
    /**
     * Creates a {@code LedgerEntryId} from a string representation of a UUID.
     * 
     * @param id the string representation of a UUID
     * @return a new {@code LedgerEntryId} with the specified UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static LedgerEntryId fromString(String id){
        return new LedgerEntryId(UUID.fromString(id));
    }
}
