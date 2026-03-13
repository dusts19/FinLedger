package com.dustin.finledger.ledger.api.dto;

import java.time.Instant;
import java.util.List;

public record TransactionResponse(
    String id,
    String description,
    Instant timestamp,
    List<TransactionLineResponse> lines,
    boolean posted
) {
    
}
