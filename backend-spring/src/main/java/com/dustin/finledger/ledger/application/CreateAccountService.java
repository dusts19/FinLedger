package com.dustin.finledger.ledger.application;

import java.util.Currency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dustin.finledger.ledger.application.dto.CreateAccountCommand;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;


@Service
@Transactional
public class CreateAccountService {
    private final AccountRepository accountRepository;

    public CreateAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountId handle(CreateAccountCommand command) {

        AccountType type = AccountType.valueOf(command.type());

        Currency currency = Currency.getInstance(command.currencyCode());

        Account account = Account.create(command.name(), type, currency);

        accountRepository.save(account);

        return account.getId();
    }

}
