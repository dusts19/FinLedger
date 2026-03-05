package com.dustin.finledger.ledger.domain.journal;

import java.util.Objects;
import java.util.UUID;


/**
 * Value object representing the unique identifier of a {@link JournalLine}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be generated automatically with {@link #newId()}</li>
 *     <li>Can be created from a string representation of a UUID with {@link #fromString(String)}</li>
 * </ul>
 * <p>
 * This is an immutable identifier suitable for use in a financial ledger system
 * where each journal line must have a globally unique ID.
 */
public record JournalLineId(UUID id) {
    /**
     * Canonical constructor that validates the ID is not null.
     * 
     * @param id the UUID represents this journal line's identifier
     * @throws NullPointerException if {@code id} is null
     */
    public JournalLineId {
        Objects.requireNonNull(id, "JournalLineId cannot be null");
    }
    
    /**
     * Generates a new unique journal line ID.
     * 
     * @return a new {@code JournalLineId} with a randomly generated UUID
     */
    public static JournalLineId newId() {
        return new JournalLineId(UUID.randomUUID());
    }
    /**
     * Creates a {@code JournalLineId} from a string representation of a UUID.
     * 
     * @param id the string representation of a UUID
     * @return a new {@code JournalLineId} with the specified UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static JournalLineId fromString(String id){
        return new JournalLineId(UUID.fromString(id));
    }
}
