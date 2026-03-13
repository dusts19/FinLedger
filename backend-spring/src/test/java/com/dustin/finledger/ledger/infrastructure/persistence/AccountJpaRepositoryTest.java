package com.dustin.finledger.ledger.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Currency;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class AccountJpaRepositoryTest {
    
    @Autowired
    private SpringDataAccountRepository springDataRepo;

    private AccountJpaRepository repository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        repository = new AccountJpaRepository(springDataRepo);

        testAccount = new Account(
            AccountId.newId(),
            "Cash",
            AccountType.ASSET,
            Currency.getInstance("USD")
        );
    }

    @Test
    void saveAndGetById_shouldPersistAndReturnAccount() {
        repository.save(testAccount);

        Optional<Account> retrievedOpt = repository.getById(testAccount.getId());
        assertThat(retrievedOpt).isPresent();

        Account retrieved = retrievedOpt.get();
        assertThat(retrieved.getId()).isEqualTo(testAccount.getId());
        assertThat(retrieved.getName()).isEqualTo("Cash");
        assertThat(retrieved.getType()).isEqualTo(AccountType.ASSET);
        assertThat(retrieved.getCurrency()).isEqualTo(Currency.getInstance("USD"));

    }

    @Test
    void getById_shouldReturnEmpty_whenAccountNotFound() {
        AccountId unknownId = AccountId.newId();
        Optional<Account> retrieved = repository.getById(unknownId);

        assertThat(retrieved).isEmpty();
    }
}
