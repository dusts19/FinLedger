package com.dustin.finledger.ledger.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.api.dto.CreateAccountRequest;
import com.dustin.finledger.ledger.application.CalculateAccountBalanceService;
import com.dustin.finledger.ledger.application.CreateAccountService;
import com.dustin.finledger.ledger.application.GetAccountService;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.account.AccountType;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountService createAccountService;

    @MockitoBean
    private GetAccountService getAccountService;

    @MockitoBean
    private CalculateAccountBalanceService calculateAccountBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_shouldReturn201() throws Exception {
        AccountId id = AccountId.newId();
        
        when(createAccountService.handle(any()))
            .thenReturn(id);
        
        CreateAccountRequest request = new CreateAccountRequest("Cash", "ASSET", "USD");
        
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/accounts/" + id.id()));

    }

    @Test
    void getAccount_shouldReturnAccount() throws Exception {

        UUID id = UUID.randomUUID();

        Account account = new Account(
            AccountId.of(id),
            "Cash",
            AccountType.ASSET,
            Currency.getInstance("USD")
        );

        when(getAccountService.handle(id))
                .thenReturn(account);

        mockMvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Cash"))
                .andExpect(jsonPath("$.type").value("ASSET"));
    }

    @Test
    void getAccountBalance_shouldReturnBalance() throws Exception {

        UUID id = UUID.randomUUID();

        Money balance = Money.of(
            new BigDecimal("100.00"),
            Currency.getInstance("USD")
        );
        
        when(calculateAccountBalanceService.handle(id))
                .thenReturn(balance);
        
        mockMvc.perform(get("/accounts/{id}/balance", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(id.toString()))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }
}
