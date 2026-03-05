// package com.dustin.finledger.ledger.domain.transaction;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.Currency;
// import java.util.List;

// import org.junit.jupiter.api.Test;

// import com.dustin.finledger.common.money.Money;
// import com.dustin.finledger.ledger.domain.JournalEntry.EntrySide;
// import com.dustin.finledger.ledger.domain.JournalEntry.JournalLine;
// import com.dustin.finledger.ledger.domain.JournalEntry.JournalLineId;
// import com.dustin.finledger.ledger.domain.JournalEntry.JournalInvariant;
// import com.dustin.finledger.ledger.domain.JournalEntry.JournalInvariantViolation;
// import com.dustin.finledger.ledger.domain.account.AccountId;

// class JournalInvariantTest {
    
//     @Test
//     void nullLedgerEntryThrows() {
//         assertThrows(NullPointerException.class, () ->
//             JournalInvariant.validateNewEntry(null, List.of())   
//         );
//     }


//     @Test
//     void duplicateIdThrows() {
//         JournalLine entry1 = new JournalLine(
//             JournalLineId.newId(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
//             Instant.now(),
//             EntrySide.DEBIT
//         );
        
//         JournalLine entry2 = new JournalLine(
//             entry1.id(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(50), Currency.getInstance("USD")),
//             Instant.now(),
//             EntrySide.CREDIT
//         );

//         assertThrows(JournalInvariantViolation.class, () ->
//             JournalInvariant.validateNewEntry(entry2, List.of(entry1))
//         );

//     }

//     @Test
//     void differentCurrencyThrows() {
//         JournalLine entry1 = new JournalLine(
//             JournalLineId.newId(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
//             Instant.now(),
//             EntrySide.DEBIT
//         );
        
//         JournalLine entry2 = new JournalLine(
//             JournalLineId.newId(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(50), Currency.getInstance("EUR")),
//             Instant.now(),
//             EntrySide.CREDIT
//         );

//         assertThrows(JournalInvariantViolation.class, () ->
//             JournalInvariant.validateNewEntry(entry2, List.of(entry1))
//         );

//     }

//     @Test
//     void validEntryDoesNotThrow() {
//         JournalLine entry1 = new JournalLine(
//             JournalLineId.newId(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(100), Currency.getInstance("USD")),
//             Instant.now(),
//             EntrySide.DEBIT
//         );
        
//         JournalLine entry2 = new JournalLine(
//             JournalLineId.newId(),
//             AccountId.newId(),
//             new Money(BigDecimal.valueOf(50), Currency.getInstance("USD")),
//             Instant.now(),
//             EntrySide.CREDIT
//         );

//         assertDoesNotThrow(() ->
//             JournalInvariant.validateNewEntry(entry2, List.of(entry1))
//         );

//     }

// }
