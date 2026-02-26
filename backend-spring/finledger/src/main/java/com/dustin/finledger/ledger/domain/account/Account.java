package com.dustin.finledger.ledger.domain.account;

import java.util.Objects;

import com.dustin.finledger.common.exceptions.DomainException;

/**
 * Represents a user's financial account in the ledger.
 * <p>
 * Accounts are aggregate roots in the ledger domain. Each account has:
 * <ul>
 *     <li>An {@link AccountId} that uniquely identifies it</li>
 *     <li>A name (e.g., "Checking" or "Savings")</li>
 *     <li>An {@link AccountType} indicating its accounting behavior (Asset, Liability, etc.)</li>
 *     <li>An {@link AccountStatus} (OPEN, FROZEN, CLOSED)</li>
 * </ul>
 * <p>
 * Invariants:
 * <ul>
 *     <li>Account name cannot be null or blank</li>
 *     <li>Cannot post transactions to frozen or closed accounts</li>
 *     <li>Accounts start with status OPEN</li>
 * </ul>
 * 
 * Example usage:
 * <pre>{@code
 * Account account = new Account(new AccountId(), "Checking", AccountType.ASSET);
 * account.ensureCanPost(); // throws DomainException if status is not OPEN
 * account.freeze();        // freezes the account
 * account.close();         // closes the account
 * }</pre>
 */
public class Account {
    private final AccountId id;
    private final String name;
    private final AccountType type;
    private AccountStatus status;

    public Account(AccountId id, String name, AccountType type) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Account name cannot be empty");
        }
        this.id = Objects.requireNonNull(id);
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.status = AccountStatus.OPEN;
    }

    public AccountId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new DomainException("Cannot freeze a closed account");
        }
        this.status = AccountStatus.FROZEN;
    }

    public void close() {
        if (status == AccountStatus.FROZEN) {
            throw new DomainException("Cannot close a frozen account");
        }
        this.status = AccountStatus.CLOSED;
    }

    public void ensureCanPost() {
        if (status != AccountStatus.OPEN) {
            throw new DomainException("Cannot post to account with status: " + status);
        }
    }
}