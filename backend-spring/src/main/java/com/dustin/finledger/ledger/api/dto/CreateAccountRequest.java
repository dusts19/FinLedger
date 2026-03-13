package com.dustin.finledger.ledger.api.dto;


public record CreateAccountRequest(
    String name,
    String type,
    String currency
) {}
