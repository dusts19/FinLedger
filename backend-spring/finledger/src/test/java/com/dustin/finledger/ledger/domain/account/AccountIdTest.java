package com.dustin.finledger.ledger.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class AccountIdTest {
    
    @Test
    void constructor_with_null_uuid_throws_exception() {
        assertThrows(NullPointerException.class, () -> {
            new AccountId((UUID) null);
        });
    }

    @Test
    void no_arg_constructor_generates_non_null_uuid() {
        AccountId accountId = AccountId.newId();

        assertNotNull(accountId.id());
    }

    @Test
    void string_constructor_parses_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        AccountId accountId = AccountId.fromString(uuid.toString());
        assertEquals(uuid, accountId.id());
    }

    @Test
    void fromString_with_invalid_uuid_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("not-a-valid-uuid");
        });
    }
}
