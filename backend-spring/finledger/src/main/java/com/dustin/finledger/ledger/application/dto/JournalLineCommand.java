package com.dustin.finledger.ledger.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.dustin.finledger.ledger.domain.account.AccountId;

public record JournalLineCommand (
    AccountId accountId,
    BigDecimal amount,
    String currency,
    String side,
    Instant occurredAt
){}
