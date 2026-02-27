// package com.dustin.finledger.ledger.application;

// import org.springframework.stereotype.Service;

// import com.dustin.finledger.common.id.AccountId;
// import com.dustin.finledger.common.money.Money;

// import com.dustin.finledger.common.id.LedgerEntryId;
// import com.dustin.finledger.ledger.domain.LedgerInvariant;
// import com.dustin.finledger.ledger.domain.repository.TransactionRepository;
// import com.dustin.finledger.ledger.domain.transaction.LedgerEntry;

// import java.time.Instant;
// import java.util.UUID;

// @Service
// public class RecordTransactionService {
    
//     private final TransactionRepository transactionRepository;

//     public RecordTransactionService(TransactionRepository transactionRepository) {
//         this.transactionRepository = transactionRepository;
//     }

//     public LedgerEntry recordTransaction(AccountId accountId, Money amount, Instant occurredAt) {
//         LedgerEntry entry = new LedgerEntry(
//             new LedgerEntryId(UUID.randomUUID()),
//             accountId,
//             amount,
//             occurredAt
//         );

//         // TODO: implement cross-object invariants (validateNewEntry)
//         LedgerInvariant.validateNewEntry(entry, transactionRepository.findByAccountId(accountId));

//         transactionRepository.save(entry);

//         return entry;
//     }


// }
