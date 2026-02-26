package com.dustin.finledger.ledger.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.exceptions.DomainException;

public class AccountTest {
    
    @Test
    void accountCreationValid() {
        Account account = new Account(new AccountId(), "Checking", AccountType.ASSET);

        assertEquals("Checking", account.getName());
        assertEquals(AccountType.ASSET, account.getType());
        assertEquals(AccountStatus.OPEN, account.getStatus());
    }

    @Test
    void accountCreationBlankNameThrows() {
        assertThrows(DomainException.class, () -> {
            new Account(new AccountId(), "", AccountType.ASSET);
        });
    }

    @Test
    void freezeThenCloseThrows() {
        Account account = new Account(new AccountId(), "Checking", AccountType.ASSET);
        account.freeze();
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        assertThrows(DomainException.class, account::close);
    }

    @Test
    void ensureCanPostFailsForFrozen() {
        Account account = new Account(new AccountId(), "Checking", AccountType.ASSET);
        account.freeze();
        assertThrows(DomainException.class, account::ensureCanPost);

    }
}
