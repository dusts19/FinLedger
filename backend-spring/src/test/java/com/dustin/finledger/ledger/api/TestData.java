package com.dustin.finledger.ledger.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.dustin.finledger.ledger.api.dto.RecordTransactionLineRequest;
import com.dustin.finledger.ledger.api.dto.RecordTransactionRequest;

class TestData {

    static RecordTransactionRequest sampleTransactionRequest() {
        return new RecordTransactionRequest(
            "Test transaction",
            List.of(
                new RecordTransactionLineRequest(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    "USD",
                    Instant.now(),
                    "DEBIT"
                ),
                new RecordTransactionLineRequest(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    "USD",
                    Instant.now(),
                    "CREDIT"
                )
            )
        );
    }
}