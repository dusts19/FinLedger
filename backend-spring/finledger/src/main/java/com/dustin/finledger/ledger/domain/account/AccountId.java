package com.dustin.finledger.ledger.domain.account;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique identifier for an {@link Account}.
 * <p>
 * Guarantees:
 * <ul>
 *     <li>Cannot be null</li>
 *     <li>Can be auto-generated with a no-arg constructor</li>
 *     <li>Can be created from a string UUID</li>
 * </ul>
 */
public record AccountId (
    UUID id
    ) {
        public AccountId {
            Objects.requireNonNull(id, "AccountId cannot be null");
        }
        public AccountId() {
            this(UUID.randomUUID());
        }
        public AccountId(String id) {
            this(UUID.fromString(id));
        }
    }
