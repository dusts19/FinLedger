package com.dustin.finledger.ledger.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    void newId_should_generate_non_null_id() {
        AccountId accountId = AccountId.newId();
        assertNotNull(accountId);
        assertNotNull(accountId.id());
    }

    @Test
    void newId_should_generate_unique_ids() {
        AccountId id1 = AccountId.newId();
        AccountId id2 = AccountId.newId();

        assertNotEquals(id1, id2);
    }

    @Test
    void of_generates_valid_accountId_from_uuid() {
        UUID uuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(uuid);
        assertEquals(uuid, accountId.id());
    }

    @Test
    void of_throws_nullPointerException_when_uuid_is_null() {
        assertThrows(NullPointerException.class, () -> AccountId.of(null));
    }

    @Test
    void of_should_create_equal_objects_for_same_uuid() {
        UUID uuid = UUID.randomUUID();
        AccountId id1 = AccountId.of(uuid);
        AccountId id2 = AccountId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
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
