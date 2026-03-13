package com.dustin.finledger.ledger.api.dto;

public record AccountResponse(
    String id,
    String name,
    String type,
    String status,
    String currency
) {
    
}
