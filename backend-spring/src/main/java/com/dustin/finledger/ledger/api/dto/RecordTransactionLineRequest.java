package com.dustin.finledger.ledger.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RecordTransactionLineRequest(
    UUID id,
    UUID accountId,
    BigDecimal amount,
    String currency,
    Instant occurredAt,
    String side
) {}
