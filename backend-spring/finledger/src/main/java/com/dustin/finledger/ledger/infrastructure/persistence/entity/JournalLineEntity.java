package com.dustin.finledger.ledger.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalLine;
import com.dustin.finledger.ledger.domain.journal.JournalLineId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "journal_line")
public class JournalLineEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "amount", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "id", nullable = false)
    private EntrySide side;


    protected JournalLineEntity() {};

    public JournalLineEntity(UUID id, UUID accountId, BigDecimal amount, String currency, Instant occurredAt, EntrySide side) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
        this.side = side;
    }

    public static JournalLineEntity fromDomain(JournalLine line) {
        return new JournalLineEntity(
            line.id().id(),
            line.accountId().id(),
            line.amount().amount(),
            line.amount().currency().getCurrencyCode(),
            line.occurredAt(),
            line.side()
        );
    }
    
    public JournalLine toDomain() {
        return new JournalLine(
            new JournalLineId(this.id),
            new AccountId(this.accountId),
            new Money(this.amount, Currency.getInstance(this.currency)),
            this.occurredAt,
            this.side);
    }


    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getOccurredAt() { return occurredAt; }
    public EntrySide getSide() { return side; }
}
