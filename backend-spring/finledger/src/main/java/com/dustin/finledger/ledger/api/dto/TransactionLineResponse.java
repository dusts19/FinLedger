package com.dustin.finledger.ledger.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionLineResponse(
    String id,
    String accountId,
    BigDecimal amount,
    String currency,
    Instant occurredAt,
    String side
    
) {}
