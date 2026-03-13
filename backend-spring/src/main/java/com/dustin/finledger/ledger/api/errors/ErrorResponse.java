package com.dustin.finledger.ledger.api.errors;

import java.time.Instant;

public record ErrorResponse(
    String error,
    String message,
    Instant timestamp
) {}
