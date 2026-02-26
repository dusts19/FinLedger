package com.dustin.finledger.common.id;

import java.util.UUID;

public record LedgerEntryId(UUID value) {
    public LedgerEntryId {
        if (value == null) {
            throw new IllegalArgumentException("LedgerEntryId cannot be null");
        }
    }
}
