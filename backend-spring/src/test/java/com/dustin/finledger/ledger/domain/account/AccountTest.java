package com.dustin.finledger.ledger.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Currency;

import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.exceptions.DomainException;

public class AccountTest {
    
    @Test
    void accountCreationValid() {
        Account account = Account.create("Checking", AccountType.ASSET, Currency.getInstance("USD"));

        assertEquals("Checking", account.getName());
        assertEquals(AccountType.ASSET, account.getType());
        assertEquals(Currency.getInstance("USD"), account.getCurrency());
        assertEquals(AccountStatus.OPEN, account.getStatus());
    }

    @Test
    void accountCreationBlankNameThrows() {
        assertThrows(DomainException.class, () -> {
            Account.create("", AccountType.ASSET, Currency.getInstance("USD"));
        });
    }

    @Test
    void freezeThenCloseThrows() {
        Account account = Account.create("Checking", AccountType.ASSET,  Currency.getInstance("USD"));
        account.freeze();
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        assertThrows(DomainException.class, account::close);
    }

    @Test
    void ensureCanPostFailsForFrozen() {
        Account account = Account.create("Checking", AccountType.ASSET, Currency.getInstance("USD"));
        account.freeze();
        assertThrows(DomainException.class, account::ensureCanPost);

    }
}
