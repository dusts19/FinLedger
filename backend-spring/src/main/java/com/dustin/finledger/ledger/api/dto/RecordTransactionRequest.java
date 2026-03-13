package com.dustin.finledger.ledger.api.dto;

import java.util.List;

public record RecordTransactionRequest(
    String description,
    List<RecordTransactionLineRequest> lines
) {}
