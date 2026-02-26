package com.dustin.finledger.common.id;

import java.util.UUID;

public record AccountId(UUID value) {
    public AccountId {
        if (value == null) {
            throw new IllegalArgumentException("AccountId cannot be null");
        }
    }
}
