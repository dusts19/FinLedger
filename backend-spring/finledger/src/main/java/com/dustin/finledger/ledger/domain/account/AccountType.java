package com.dustin.finledger.ledger.domain.account;

import com.dustin.finledger.ledger.domain.transaction.EntrySide;

/**
 * Represents the accouting type of an account.
 * <p>
 * Each type has a "normal balance side" (debit or credit) that defines
 * how the account increases:
 * <ul>
 *     <li>ASSET, EXPENSE: increase with DEBIT</li>
 *     <li>LIABILITY, EQUITY, REVENUE: increase with CREDIT</li>
 * </ul>
 */
public enum AccountType {
    ASSET(EntrySide.DEBIT),
    LIABILITY(EntrySide.CREDIT),
    EQUITY(EntrySide.CREDIT),
    REVENUE(EntrySide.CREDIT),
    EXPENSE(EntrySide.DEBIT);

    private final EntrySide normalBalanceSide;

    AccountType(EntrySide normalBalanceSide) {
        this.normalBalanceSide = normalBalanceSide;
    }

    /**
     * Returns the normal balance side for this account type.
     */
    public EntrySide normalBalanceSide() {
        return normalBalanceSide;
    }
    
    
    /**
     * Checks if this account type increases when a given entry side is applied.
     * 
     * @param side the side of the ledger entry (DEBIT or CREDIT)
     * @return true if the account increases with this side, false otherwise
     */
    public boolean increasesWith(EntrySide side) {
        return this.normalBalanceSide == side;
    }
}
