package com.dustin.finledger.ledger.domain.transaction;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identifier of a {@link Transaction}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be generated automatically with {@link #newId()}</li>
 *     <li>Can be created from a string representation of a UUID with {@link #fromString(String)}</li>
 * </ul>
 * <p>
 * This is an immutable identifier suitable for use in a financial ledger system
 * where each transaction must have a globally unique ID.
 */
public record TransactionId(UUID id) {

    /**
     * Canonical constructor that validates the ID is not null.
     * 
     * @param id the UUID represents this transaction identifier
     * @throws NullPointerException if {@code id} is null
     */
    public TransactionId {
        Objects.requireNonNull(id, "TransactionId cannot be null");
    }

    /**
     * Generates a new unique transaction ID.
     * 
     * @return a new {@code TransactionId} with a randomly generated UUID
     */
    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID());
    }

    /**
     * Creates a {@code TransactionId} from a string representation of a UUID.
     * 
     * @param id the string representation of a UUID
     * @return a new {@code TransactionId} with the specified UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static TransactionId fromString(String id){
        return new TransactionId(UUID.fromString(id));
    }
}
