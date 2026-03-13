package com.dustin.finledger.ledger.api.dto;

import java.math.BigDecimal;

public record AccountBalanceResponse(
    String accountId,
    BigDecimal amount,
    String currency
) {
    
}
