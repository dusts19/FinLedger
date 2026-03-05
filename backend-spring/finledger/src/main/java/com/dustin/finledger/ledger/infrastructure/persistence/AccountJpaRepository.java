package com.dustin.finledger.ledger.infrastructure.persistence;

import java.util.Currency;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;
import com.dustin.finledger.ledger.infrastructure.persistence.entity.AccountEntity;


@Repository
public class AccountJpaRepository implements AccountRepository{

    private final SpringDataAccountRepository springDataRepo;

    public AccountJpaRepository(SpringDataAccountRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public void save(Account account) {
        springDataRepo.save(toEntity(account));
    };

    @Override
    public Optional<Account> getById(AccountId id){
        return springDataRepo.findById(id.id())
            .map(this::toDomain);
    };


    private AccountEntity toEntity(Account account) {
        return new AccountEntity(
            account.getId().id(),
            account.getName(),
            account.getType(),
            account.getCurrency().getCurrencyCode(),
            account.getStatus()
            );
    }
    
    private Account toDomain(AccountEntity entity) {
        Account account =  new Account(
            new AccountId(entity.getId()),
            entity.getName(),
            entity.getType(),
            Currency.getInstance(entity.getCurrencyCode())
        );
        switch (entity.getStatus()) {
            case FROZEN -> account.freeze();
            case CLOSED -> account.close();
            default -> {}
        }
        return account;
    }
}
