package com.dustin.finledger.ledger.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

import com.dustin.finledger.common.money.Money;

public class MoneyTest {
    
    private final Currency USD = Currency.getInstance("USD");
    

    @Test
    void testAddSameCurrency() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("5.00"), USD);

        Money result = a.add(b);

        assertEquals(new BigDecimal("15.00"), result.amount());
        assertEquals(USD, result.currency());
    }

    @Test
    void testAddDifferentCurrencyThrows() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("5.00"), Currency.getInstance("EUR"));

        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    void testNegate() {
        Money a = new Money(new BigDecimal("15.00"), USD);
        Money neg = a.negate();

        assertEquals(new BigDecimal("-15.00"), neg.amount());
        assertEquals(USD, neg.currency());

    }

    @Test
    void testZero() {
        Money zero = Money.zero(USD);
        assertEquals(BigDecimal.ZERO, zero.amount());
        assertEquals(USD, zero.currency());
    }

}
