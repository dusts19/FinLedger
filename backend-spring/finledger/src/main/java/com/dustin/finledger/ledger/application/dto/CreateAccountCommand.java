package com.dustin.finledger.ledger.application.dto;



public record CreateAccountCommand(
    String name,
    String type,
    String currencyCode
) {}
