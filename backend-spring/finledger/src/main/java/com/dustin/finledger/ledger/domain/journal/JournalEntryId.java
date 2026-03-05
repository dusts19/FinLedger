package com.dustin.finledger.ledger.domain.journal;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identifier of a {@link JournalEntry}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be generated automatically with {@link #newId()}</li>
 *     <li>Can be created from a string representation of a UUID with {@link #fromString(String)}</li>
 * </ul>
 * <p>
 * This is an immutable identifier suitable for use in a financial ledger system
 * where each journal entry must have a globally unique ID.
 */
public record JournalEntryId(UUID id) {

    /**
     * Canonical constructor that validates the ID is not null.
     * 
     * @param id the UUID represents this journal entry identifier
     * @throws NullPointerException if {@code id} is null
     */
    public JournalEntryId {
        Objects.requireNonNull(id, "JournalEntryId cannot be null");
    }

    /**
     * Generates a new unique journal entry ID.
     * 
     * @return a new {@code JournalEntryId} with a randomly generated UUID
     */
    public static JournalEntryId newId() {
        return new JournalEntryId(UUID.randomUUID());
    }

    /**
     * Creates a {@code JournalEntryId} from a string representation of a UUID.
     * 
     * @param id the string representation of a UUID
     * @return a new {@code JournalEntryId} with the specified UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static JournalEntryId fromString(String id){
        return new JournalEntryId(UUID.fromString(id));
    }
}
