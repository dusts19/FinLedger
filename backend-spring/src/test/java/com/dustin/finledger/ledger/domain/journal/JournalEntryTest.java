package com.dustin.finledger.ledger.domain.journal;

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


class JournalEntryTest {
    
    @Nested
    class ConstructorTests{
        @Test
        void journalEntryConstructor_SetsFieldsCorrectly() {
            JournalEntryId id = JournalEntryId.newId();
            String desc = "Test journal entry transaction";

            JournalEntry tx = new JournalEntry(id, desc);
            
            assertEquals(id, tx.getId());
            assertEquals(desc, tx.getDescription());
            assertFalse(tx.isPosted());
            assertTrue(tx.getLines().isEmpty());
            assertNotNull(tx.getTimestamp());
        }

        @Test
        void journalEntryConstructor_throws_whenNullId() {
            assertThrows(NullPointerException.class, () -> {
                new JournalEntry(null, "desc");
            });
        };

        @Test
        void transactionConstructor_throws_whenNullDescription() {
            JournalEntryId id = JournalEntryId.newId();
            assertThrows(NullPointerException.class, () -> new JournalEntry(id, null));
        }
    }
    
    @Nested
    class addLineTests {
        @Test
        void addLine_shouldSucceed_whenAddEntryLineBeforePosting() {
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
        void addLine_shouldThrowException_whenAddEntryAfterPosting() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Post check");

            JournalLine debit = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );

            
            JournalLine credit = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );

            tx.addLine(debit);
            tx.addLine(credit);

            tx.post();

            JournalLine extraEntry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );


            assertThrows(DomainException.class, () -> tx.addLine(extraEntry));
        }   
        
        // @Test
        // void addingEntryWithFutureTimeStampThrows() {
        //     JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Future timestamp test");

        //     JournalLine futureEntry = new JournalLine(
        //         JournalLineId.newId(),
        //         AccountId.newId(),
        //         new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
        //         Instant.now().plusSeconds(3600),
        //         EntrySide.DEBIT
        //     );
        //     assertThrows(JournalInvariantViolation.class, () -> tx.addLine(futureEntry));
        // }

        @Test
        void addLine_shouldThrowException_whenCurrenciesMismatch() {
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
        void addLine_shouldThrowException_whenAddingDuplicateEntryId() {
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
        void post_succeeds_whenBalancedTransaction() {
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
        void post_shouldThrowJournalInvariantViolation_whenEntryHasLessThanTwoLines() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Journal Entry with less than two lines");
            
            JournalLine entry = new JournalLine(
                JournalLineId.newId(),
                AccountId.newId(),
                new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.DEBIT
            );
            tx.addLine(entry);

            JournalInvariantViolation ex =
                assertThrows(JournalInvariantViolation.class, tx::post);
            assertEquals("JournalEntry must contain at least two lines", ex.getMessage());
        }

        @Test
        void post_shouldThrowException_whenJournalEntryLinesUnbalanced() {
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
                new Money(new BigDecimal("50.00"), Currency.getInstance("USD")),
                Instant.now(),
                EntrySide.CREDIT
            );
            
            tx.addLine(debitEntry);
            tx.addLine(creditEntry);
            assertThrows(JournalInvariantViolation.class, tx::post);
        }


        @Test
        void getTotalDebitsAndCredits_shouldReturnCorrectTotals_whenEntryContainsMultipleLines() {
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

        @Test
        void post_shouldThrowException_whenEntryHasNoLines(){
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Empty entry");
            assertThrows(IllegalStateException.class, tx::post);
        }
    }


    @Nested
    class ReverseTests {
        @Test
        void reverse_shouldThrowDomainException_whenAddingUnpostedJournalLine() {
            JournalEntry tx = new JournalEntry(JournalEntryId.newId(), "Test transaction");
            JournalLine entry = new JournalLine(JournalLineId.newId(), AccountId.newId(), new Money(new BigDecimal("100.00"), Currency.getInstance("USD")), Instant.now(), EntrySide.DEBIT);
            tx.addLine(entry);

            assertThrows(DomainException.class, () -> tx.reverse(JournalEntryId.newId()));
        }

        @Test
        void reverse_shouldFlipAllEntriesAndPosts_whenEntryIsPosted() {
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