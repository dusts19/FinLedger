package com.dustin.finledger.ledger.domain.account;

/**
 * Status of an account, indicating whether transactions can be posted.
 * <ul>
 *     <li>OPEN: transactions can be posted</li>
 *     <li>FROZEN: temporarily prevents posting</li>
 *     <li>CLOSED: permanently prevents posting</li>
 * </ul>
 */
public enum AccountStatus {
    OPEN,
    FROZEN,
    CLOSED
}
