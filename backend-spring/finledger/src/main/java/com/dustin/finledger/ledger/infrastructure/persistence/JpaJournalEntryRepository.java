package com.dustin.finledger.ledger.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;
import com.dustin.finledger.ledger.infrastructure.persistence.entity.JournalEntryEntity;

import jakarta.persistence.EntityManager;

@Repository
public class JpaJournalEntryRepository implements JournalEntryRepository{
    
    private final EntityManager em;

    public JpaJournalEntryRepository(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public void save(JournalEntry entry) {
        JournalEntryEntity entity = JournalEntryEntity.fromDomain(entry);
        em.persist(entity);
    };

    @Override
    public Optional<JournalEntry> getById(JournalEntryId id) {
        JournalEntryEntity entity = em.find(JournalEntryEntity.class, id.id());
        if (entity == null) return Optional.empty();
        return Optional.of(entity.toDomain());
    };

    @Override
    public List<JournalEntry> findAllPostedByAccountId(AccountId accountId) {
        
        List<JournalEntryEntity> entities = em.createQuery("""
            SELECT DISTINCT je
            FROM JournalEntryEntity je
            JOIN je.lines jl
            WHERE jl.accountId = :accountId
            AND je.posted = true
        """, JournalEntryEntity.class)
        .setParameter("accountId", accountId.id())
        .getResultList();
        
        return entities.stream()
                .map(JournalEntryEntity::toDomain)
                .toList();
    }

    @Override
    public List<JournalEntry> findAll() {
        List<JournalEntryEntity> entities = em.createQuery("""
                SELECT je
                FROM JournalEntryEntity je
                WHERE je.posted = true
                """, JournalEntryEntity.class)
                .getResultList();
        return entities.stream()
                .map(JournalEntryEntity::toDomain)
                .toList();
    }

    @Override
    public Money getAccountBalance(AccountId accountId, Currency currency) {
        BigDecimal debitSum = em.createQuery("""
                SELECT COALESCE(SUM(l.amount.amount), 0)
                FROM JournalEntryEntity j JOIN j.lines l
                WHERE j.posted = true
                AND l.accountId = :accountId
                AND l.amount.currencyCode = :currencyCode
                AND l.side = :debit
                """, BigDecimal.class
        )
        .setParameter("accountId", accountId.id())
        .setParameter("currencyCode", currency.getCurrencyCode())
        .setParameter("debit", EntrySide.DEBIT)
        .getSingleResult();

        BigDecimal creditSum = em.createQuery("""
                SELECT COALESCE(SUM(l.amount.amount), 0)
                FROM JournalEntryEntity j JOIN j.lines l
                WHERE j.posted = true
                AND l.accountId = :accountId
                AND l.amount.currencyCode = :currencyCode
                AND l.side = :credit
                """, BigDecimal.class
        )
        .setParameter("accountId", accountId.id())
        .setParameter("currencyCode", currency.getCurrencyCode())
        .setParameter("credit", EntrySide.CREDIT)
        .getSingleResult();


        return Money.of(debitSum.subtract(creditSum), currency);
    }
}
