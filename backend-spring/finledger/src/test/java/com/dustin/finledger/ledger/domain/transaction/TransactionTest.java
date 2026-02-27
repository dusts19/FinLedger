package com.dustin.finledger.ledger.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;
// import com.dustin.finledger.ledger.domain.transaction.LedgerEntry;

class TransactionTest {
    
    @Nested
    class ConstructorTests{
        @Test
        void transactionConstructorSetsFieldsCorrectly() {
            TransactionId id = TransactionId.newId();
            String desc = "Test transaction";

            Transaction tx = new Transaction(id, desc);
            
            assertEquals(id, tx.getId());
            assertEquals(desc, tx.getDescription());
            assertFalse(tx.isPosted());
            assertTrue(tx.getEntries().isEmpty());
            assertNotNull(tx.getTimestamp());
        }

        @Test
        void transactionConstructorNullIdThrows() {
            assertThrows(NullPointerException.class, () -> {
                new Transaction(null, "desc");
            });
        };

        @Test
        void transactionConstructorNullDescriptionThrows() {
            TransactionId id = TransactionId.newId();
            assertThrows(NullPointerException.class, () -> new Transaction(id, null));
        }
    }
    
    @Nested
    class AddEntryTests {
        @Test
        void addingEntryBeforePostingSucceeds() {
            TransactionId txId = TransactionId.newId();
            Transaction tx = new Transaction(txId, "Normal transaction");

            AccountId accountId = AccountId.newId();
            Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
            LedgerEntry entry = new LedgerEntry(
                LedgerEntryId.newId(),
                accountId,
                amount,
                Instant.now(),
                EntrySide.DEBIT
            );

            tx.addEntry(entry);

            assertEquals(1, tx.getEntries().size());
            assertSame(entry, tx.getEntries().get(0));
        }

        @Test
        void addingEntryAfterPostingThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Post check");

            LedgerEntry entry1 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );

            tx.addEntry(entry1);
            tx.post();

            LedgerEntry entry2 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );

            assertThrows(DomainException.class, () -> tx.addEntry(entry2));
        }   
        
        @Test
        void addingEntryWithFutureTimeStampThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Future timestamp test");

            LedgerEntry futureEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now().plusSeconds(3600),
                EntrySide.DEBIT
            );
            assertThrows(LedgerInvariantViolation.class, () -> tx.addEntry(futureEntry));
        }

        @Test
        void addingEntryWithCurrencyMismatchThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Currency Mismatch");

            LedgerEntry entry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            tx.addEntry(entry);
            LedgerEntry mismatchEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("EUR")),
                Instant.now(),
                EntrySide.DEBIT
            );
            assertThrows(LedgerInvariantViolation.class, () -> tx.addEntry(mismatchEntry));
        }
        @Test
        void addingDuplicateEntryIdThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Currency Mismatch");

            LedgerEntry entry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            tx.addEntry(entry);
            LedgerEntry duplicateIdEntry = new LedgerEntry(
                entry.id(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("EUR")),
                Instant.now(),
                EntrySide.DEBIT
            );
            assertThrows(LedgerInvariantViolation.class, () -> tx.addEntry(duplicateIdEntry));
        }
    }


    @Nested
    class PostTests {
        @Test
        void postBalancedTransactionSucceeds() {
            Transaction tx = new Transaction(TransactionId.newId(), "Balanced transaction");

            LedgerEntry debitEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            LedgerEntry creditEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );

            tx.addEntry(debitEntry);
            tx.addEntry(creditEntry);
            tx.post();
            assertTrue(tx.isPosted());

        }

        @Test
        void postUnbalancedTransactionThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Balanced transaction");

            LedgerEntry debitEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            LedgerEntry creditEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addEntry(debitEntry);
            tx.addEntry(creditEntry);
            assertThrows(LedgerInvariantViolation.class, tx::post);
        }
        @Test
        void totalDebitsAndCreditsComputeCorrectly() {
            Transaction tx = new Transaction(TransactionId.newId(), "Balanced transaction");

            LedgerEntry debitEntry1 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            LedgerEntry debitEntry2 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            LedgerEntry creditEntry1 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("120.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            LedgerEntry creditEntry2 = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("30.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addEntry(debitEntry1);
            tx.addEntry(debitEntry2);
            tx.addEntry(creditEntry1);
            tx.addEntry(creditEntry2);
            
            Money totalDebits = tx.getTotalDebits();
            Money totalCredits = tx.getTotalCredits();

            assertEquals(new Money(new BigDecimal("150.00"), Currency.getInstance("USD")), totalDebits);
            assertEquals(new Money(new BigDecimal("150.00"), Currency.getInstance("USD")), totalCredits);
        }
    }


    @Nested
    class ReverseTests {
        @Test
        void reversingUnpostedTransactionThrows() {
            Transaction tx = new Transaction(TransactionId.newId(), "Test transaction");
            LedgerEntry entry = new LedgerEntry(LedgerEntryId.newId(), AccountId.newId(), new Money(new BigDecimal("100.00"), Currency.getInstance("USD")), Instant.now(), EntrySide.DEBIT);
            tx.addEntry(entry);

            assertThrows(DomainException.class, () -> tx.reverse(TransactionId.newId()));
        }

        @Test
        void reverseFlipsAllEntriesAndPosts() {
            TransactionId reversalId = TransactionId.newId();
            Transaction tx = new Transaction(TransactionId.newId(), "Original tx");

            
            LedgerEntry debitEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            LedgerEntry creditEntry = new LedgerEntry(
                LedgerEntryId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addEntry(debitEntry);
            tx.addEntry(creditEntry);
            tx.post();

            Transaction reversal = tx.reverse(reversalId);

            assertEquals(reversalId, reversal.getId());
            assertEquals("Reversal of: " + tx.getDescription(), reversal.getDescription());
            assertTrue(reversal.isPosted());

            for (int i = 0; i < tx.getEntries().size(); i++) {
                LedgerEntry original = tx.getEntries().get(i);
                LedgerEntry reversedEntry = reversal.getEntries().get(i);

                assertEquals(original.accountId(), reversedEntry.accountId());
                assertEquals(original.amount(), reversedEntry.amount());

                EntrySide expectedFlipped = original.side() == EntrySide.DEBIT ? EntrySide.CREDIT : EntrySide.DEBIT;
                assertEquals(expectedFlipped, reversedEntry.side());

                assertEquals(original.side(), tx.getEntries().get(i).side());
            }
        }

    }
    
}