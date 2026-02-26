package com.dustin.finledger.ledger.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dustin.finledger.ledger.domain.transaction.EntrySide;

public class AccountTypeTest {

    @Nested
    class IncreasesWith {
        
        @Test
        void asset_increases_with_debit() {
            assertTrue(AccountType.ASSET.increasesWith(EntrySide.DEBIT));
        }
    
        @Test
        void asset_does_not_increase_with_credit() {
            assertFalse(AccountType.ASSET.increasesWith(EntrySide.CREDIT));
        }
    
        @Test
        void liability_increases_with_credit() {
            assertTrue(AccountType.LIABILITY.increasesWith(EntrySide.CREDIT));
        }
    
        @Test
        void liability_does_not_increase_with_debit() {
            assertFalse(AccountType.LIABILITY.increasesWith(EntrySide.DEBIT));
        }
    }

    @Nested
    class NormalBalanceSide {
        
        @Test
        void asset_normal_balance_side_is_debit() {
            assertEquals(EntrySide.DEBIT, AccountType.ASSET.normalBalanceSide());
        }
    
        @Test
        void liability_normal_balance_side_is_credit() {
            assertEquals(EntrySide.CREDIT, AccountType.LIABILITY.normalBalanceSide());
        }
    }

}
