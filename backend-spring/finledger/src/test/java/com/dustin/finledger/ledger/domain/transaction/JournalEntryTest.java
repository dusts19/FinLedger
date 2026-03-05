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
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.journal.JournalInvariantViolation;
import com.dustin.finledger.ledger.domain.journal.JournalLine;
import com.dustin.finledger.ledger.domain.journal.JournalLineId;

class JournalEntryTest {
    
    @Nested
    class ConstructorTests{
        @Test
        void transactionConstructorSetsFieldsCorrectly() {
            JournalEntryId id = JournalEntryId.newId();
            String desc = "Test transaction";

            JournalEntry tx = new JournalEntry(id, desc);
            
            assertEquals(id, tx.getId());
            assertEquals(desc, tx.getDescription());
            assertFalse(tx.isPosted());
            assertTrue(tx.getLines().isEmpty());
            assertNotNull(tx.getTimestamp());
        }

        @Test
        void transactionConstructorNullIdThrows() {
            assertThrows(NullPointerException.class, () -> {
                new JournalEntry(null, "desc");
            });
        };

        @Test
        void transactionConstructorNullDescriptionThrows() {
            JournalEntryId id = JournalEntryId.newId();
            assertThrows(NullPointerException.class, () -> new JournalEntry(id, null));
        }
    }
    
    @Nested
    class addLineTests {
        @Test
        void addingEntryBeforePostingSucceeds() {
            JournalEntryId txId = JournalEntryId.newId();
            JournalEntry tx = new JournalEntry(txId, "Normal transaction");

            AccountId accountId = AccountId.newId();
            Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
            JournalLine entry = new JournalLine(
                JournalLineId.newId(),
                accountId,
                amount,
                Instant.now(),
                EntrySide.DEBIT
            );

            tx.addLine(entry);

            assertEquals(1, tx.getLines().size());
            assertSame(entry, tx.getLines().get(0));
        }

        @Test
        void addingEntryAfterPostingThrows() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Post check");

            JournalLine entry1 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );

            tx.addLine(entry1);
            tx.post();

            JournalLine entry2 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );

            assertThrows(DomainException.class, () -> tx.addLine(entry2));
        }   
        
        @Test
        void addingEntryWithFutureTimeStampThrows() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Future timestamp test");

            JournalLine futureEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now().plusSeconds(3600),
                EntrySide.DEBIT
            );
            assertThrows(JournalInvariantViolation.class, () -> tx.addLine(futureEntry));
        }

        @Test
        void addingEntryWithCurrencyMismatchThrows() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Currency Mismatch");

            JournalLine entry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            tx.addLine(entry);
            JournalLine mismatchEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("EUR")),
                Instant.now(),
                EntrySide.DEBIT
            );
            assertThrows(JournalInvariantViolation.class, () -> tx.addLine(mismatchEntry));
        }
        @Test
        void addingDuplicateEntryIdThrows() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Currency Mismatch");

            JournalLine entry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            tx.addLine(entry);
            JournalLine duplicateIdEntry = new JournalLine(
                entry.id(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("EUR")),
                Instant.now(),
                EntrySide.DEBIT
            );
            assertThrows(JournalInvariantViolation.class, () -> tx.addLine(duplicateIdEntry));
        }
    }


    @Nested
    class PostTests {
        @Test
        void postBalancedTransactionSucceeds() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Balanced transaction");

            JournalLine debitEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            JournalLine creditEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );

            tx.addLine(debitEntry);
            tx.addLine(creditEntry);
            tx.post();
            assertTrue(tx.isPosted());

        }

        @Test
        void postUnbalancedTransactionThrows() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Balanced transaction");

            JournalLine debitEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            JournalLine creditEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addLine(debitEntry);
            tx.addLine(creditEntry);
            assertThrows(JournalInvariantViolation.class, tx::post);
        }
        @Test
        void totalDebitsAndCreditsComputeCorrectly() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Balanced transaction");

            JournalLine debitEntry1 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            JournalLine debitEntry2 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            JournalLine creditEntry1 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("120.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            JournalLine creditEntry2 = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("30.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addLine(debitEntry1);
            tx.addLine(debitEntry2);
            tx.addLine(creditEntry1);
            tx.addLine(creditEntry2);
            
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
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Test transaction");
            JournalLine entry = new JournalLine(JournalLineId.newId(), AccountId.newId(), new Money(new BigDecimal("100.00"), Currency.getInstance("USD")), Instant.now(), EntrySide.DEBIT);
            tx.addLine(entry);

            assertThrows(DomainException.class, () -> tx.reverse(JournalEntryId.newId()));
        }

        @Test
        void reverseFlipsAllEntriesAndPosts() {
            JournalEntryId reversalId = JournalEntryId.newId();
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Original tx");

            
            JournalLine debitEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            JournalLine creditEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addLine(debitEntry);
            tx.addLine(creditEntry);
            tx.post();

            JournalEntry reversal = tx.reverse(reversalId);

            assertEquals(reversalId, reversal.getId());
            assertEquals("Reversal of: " + tx.getDescription(), reversal.getDescription());
            assertTrue(reversal.isPosted());

            for (int i = 0; i < tx.getLines().size(); i++) {
                JournalLine original = tx.getLines().get(i);
                JournalLine reversedEntry = reversal.getLines().get(i);

                assertEquals(original.accountId(), reversedEntry.accountId());
                assertEquals(original.amount(), reversedEntry.amount());

                EntrySide expectedFlipped = original.side() == EntrySide.DEBIT ? EntrySide.CREDIT : EntrySide.DEBIT;
                assertEquals(expectedFlipped, reversedEntry.side());

                assertEquals(original.side(), tx.getLines().get(i).side());
            }
        }

    }
    
}