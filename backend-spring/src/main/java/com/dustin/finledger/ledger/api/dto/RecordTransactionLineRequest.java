package com.dustin.finledger.ledger.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RecordTransactionLineRequest(
    
    @NotNull
    UUID id,

    @NotNull
    UUID accountId,

    @NotNull
    BigDecimal amount,

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    String currency,

    @NotNull
    Instant occurredAt,

    @NotNull
    String side
) {}
