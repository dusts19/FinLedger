package com.dustin.finledger.ledger.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dustin.finledger.ledger.application.dto.CreateAccountCommand;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class CreateAccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountService service;

    @Test
    void handle_shouldCreateAndSaveAccount() {

        CreateAccountCommand command = new CreateAccountCommand(
            "Cash",
            "ASSET",
            "USD"
        );

        AccountId id = service.handle(command);

        assertThat(id).isNotNull();

        verify(accountRepository, times(1)).save(any(Account.class));
    }
}
