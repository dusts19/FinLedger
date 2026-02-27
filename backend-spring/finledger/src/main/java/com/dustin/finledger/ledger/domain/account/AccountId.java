package com.dustin.finledger.ledger.domain.account;

import java.util.Objects;
import java.util.UUID;


/**
 * Value object representing the unique identifier of a {@link Account}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be generated automatically with {@link #newId()}</li>
 *     <li>Can be created from a string representation of a UUID with {@link #fromString(String)}</li>
 * </ul>
 * <p>
 * This is an immutable identifier suitable for use in a financial ledger system
 * where each account must have a globally unique ID.
 */
public record AccountId (UUID id) {
        
    /**
     * Canonical constructor that validates the ID is not null.
     * 
     * @param id the UUID represents this account identifier
     * @throws NullPointerException if {@code id} is null
     */
    public AccountId {
        Objects.requireNonNull(id, "AccountId cannot be null");
    }
    /**
     * Generates a new unique account ID.
     * 
     * @return a new {@code AccountId} with a randomly generated UUID
     */
    public static AccountId newId() {
        return new AccountId(UUID.randomUUID());
    }
    /**
     * Creates a {@code AccountId} from a string representation of a UUID.
     * 
     * @param id the string representation of a UUID
     * @return a new {@code AccountId} with the specified UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static AccountId fromString(String id) {
        return new AccountId(UUID.fromString(id));
    }
}
