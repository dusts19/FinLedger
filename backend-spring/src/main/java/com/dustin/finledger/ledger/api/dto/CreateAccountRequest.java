package com.dustin.finledger.ledger.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAccountRequest(

    @NotBlank    
    String name,
    
    @NotBlank
    String type,
    
    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    String currency
) {}
