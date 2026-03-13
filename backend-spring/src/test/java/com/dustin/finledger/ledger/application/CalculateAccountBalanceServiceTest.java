package com.dustin.finledger.ledger.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;
import com.dustin.finledger.ledger.domain.repository.AccountRepository;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;

@ExtendWith(MockitoExtension.class)
class CalculateAccountBalanceServiceTest {
    
    @Mock
    private JournalEntryRepository journalEntryRepository;
    
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CalculateAccountBalanceService service;

    private Account usdAccount;
    private UUID usdAccountRawId;

    @BeforeEach
    void setUp() {
        usdAccountRawId = UUID.randomUUID();
        usdAccount = new Account(
            AccountId.of(usdAccountRawId),
            "Cash",
            AccountType.ASSET,
            Currency.getInstance("USD")
        );
    }

    @Test
    void handle_shouldReturnBalance_whenAccountExists() {
        when(accountRepository.getById(AccountId.of(usdAccountRawId)))
            .thenReturn(Optional.of(usdAccount));
        when(journalEntryRepository.getAccountBalance(AccountId.of(usdAccountRawId), usdAccount.getCurrency()))
            .thenReturn(Money.of(new BigDecimal("500.00"), usdAccount.getCurrency()));
        
        Money balance = service.handle(usdAccountRawId);

        assertThat(balance.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(balance.currency()).isEqualTo(usdAccount.getCurrency());
        verify(accountRepository, times(1)).getById(AccountId.of(usdAccountRawId));
        verify(journalEntryRepository, times(1))
            .getAccountBalance(AccountId.of(usdAccountRawId), usdAccount.getCurrency());

    }

    @Test
    void handle_shouldThrow_whenAccountNotFound() {
        when(accountRepository.getById(AccountId.of(usdAccountRawId)))
            .thenReturn(Optional.empty());
        
        assertThrows(DomainException.class, () -> service.handle(usdAccountRawId));
        verify(accountRepository, times(1)).getById(AccountId.of(usdAccountRawId));
        verifyNoInteractions(journalEntryRepository);
    }
}
