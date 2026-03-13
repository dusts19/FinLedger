package com.dustin.finledger.common.money;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;


public class MoneyTest {
    
    private final Currency USD = Currency.getInstance("USD");
    

    @Test
    void of_generates_valid_money_from_BigDecimal() {
        Money money = Money.of(new BigDecimal("500.00"), USD);
        assertEquals(new BigDecimal("500.00"), money.amount());
        assertEquals(USD, money.currency());
    }
    
    @Test
    void of_generates_valid_money_from_string() {
        Money money = Money.of("500.00",  USD);
        assertEquals(new BigDecimal("500.00"), money.amount());
        assertEquals(USD, money.currency());
    }

    @Test
    void constructor_throws_if_amount_null() {
        assertThrows(NullPointerException.class,
            () -> Money.of((BigDecimal) null, USD)
        );
    }

    @Test
    void constructor_throws_if_currency_null() {
        assertThrows(NullPointerException.class,
            () -> Money.of(new BigDecimal("1.00"), null)
        );
    }

    @Test
    void constructor_throws_if_scale_exceeds_currency_fraction() {
        assertThrows(ArithmeticException.class,
            () -> Money.of("10.123", USD)
        );
    }


    @Test
    void test_add_same_currency() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("5.00"), USD);

        Money result = a.add(b);

        assertEquals(new BigDecimal("15.00"), result.amount());
        assertEquals(USD, result.currency());
    }

    @Test
    void test_add_different_currency_throws() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("5.00"), Currency.getInstance("EUR"));

        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    void isNegative_returns_true_for_negative_amount() {
        Money money = Money.of("-1.00", USD);
        assertTrue(money.isNegative());
    }

    @Test
    void isNegative_returns_false_for_postive_amount() {
        Money money = Money.of("1.00", USD);
        assertFalse(money.isNegative());
    }

    @Test
    void test_negate() {
        Money a = new Money(new BigDecimal("15.00"), USD);
        Money neg = a.negate();

        assertEquals(new BigDecimal("-15.00"), neg.amount());
        assertEquals(USD, neg.currency());

    }

    @Test
    void test_zero() {
        Money zero = Money.zero(USD);
        assertEquals(BigDecimal.ZERO.setScale(2), zero.amount());
        assertEquals(USD, zero.currency());
    }

}
