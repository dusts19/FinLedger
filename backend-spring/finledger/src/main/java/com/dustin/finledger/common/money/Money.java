package com.dustin.finledger.common.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Represents an immutable amount of money in a specific currency.
 * 
 * Guarantees:
 * - amount and currency cannot be null
 * - amount has a scale that matches the currency's default fraction digits
 * 
 * Provides operations for adding, negating, and checking negativity.
 * Use {@link #zero(Currency)} to get a zero amount in a given currency.
 * 
 * Example usage:
 * <pre>
 * Money m1 = new Money(new BigDecimal("10.50"), Currency.getInstance("USD"));
 * Money m2 = Money.zero(Currency.getInstance("USD"));
 * Money sum = m1.add(m2);
 * </pre>
 */
public record Money(BigDecimal amount, Currency currency) {
    
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");

        int scale = currency.getDefaultFractionDigits();
        amount = amount.setScale(scale, RoundingMode.UNNECESSARY);
        // if (amount.scale() > scale) {
        //     throw new IllegalArgumentException(
        //         "Too many decimal places for currency " + currency
        //     );
        // }
    }

    /**
     * Returns a zero amount of money in the specified currency.
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits()), currency);
    }

    /**
     * Returns a new Money instance that is the sum of this and another Money.
     * @throws IllegalArgumentException if currencies differ
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), currency);
    }

    /**
     * Returns a new Money instance with the negated amount.
     */
    public Money negate() {
        return new Money(this.amount.negate(), currency);
    }

    /**
     * Returns true if the amount is negative.
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }
    // Ensures both Money objects have the same currency, otherwise throws.
    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Currency mismatch: " + this.currency + " vs " + other.currency
            );
        }
    }
}
