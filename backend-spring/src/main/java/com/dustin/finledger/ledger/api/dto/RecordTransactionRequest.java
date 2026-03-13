package com.dustin.finledger.ledger.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RecordTransactionRequest(
    
    @NotBlank
    String description,

    @NotEmpty(message = "Transaction must contain at least one line")
    @Valid
    List<RecordTransactionLineRequest> lines
) {}
