package com.dustin.finledger.ledger.api;


import com.dustin.finledger.common.money.Money;
import com.dustin.finledger.ledger.api.dto.AccountBalanceResponse;
import com.dustin.finledger.ledger.api.dto.AccountResponse;
import com.dustin.finledger.ledger.api.dto.CreateAccountRequest;
import com.dustin.finledger.ledger.application.CalculateAccountBalanceService;
import com.dustin.finledger.ledger.application.CreateAccountService;
import com.dustin.finledger.ledger.application.GetAccountService;
import com.dustin.finledger.ledger.application.dto.CreateAccountCommand;
import com.dustin.finledger.ledger.domain.account.Account;
import com.dustin.finledger.ledger.domain.account.AccountId;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountService createAccountService;
    private final GetAccountService getAccountService;
    private final CalculateAccountBalanceService calculateAccountBalanceService;
    
    public AccountController(CreateAccountService createAccountService, GetAccountService getAccountService, CalculateAccountBalanceService calculateAccountBalanceService) {
        this.createAccountService = createAccountService;
        this.getAccountService = getAccountService;
        this.calculateAccountBalanceService = calculateAccountBalanceService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        
        CreateAccountCommand command = new CreateAccountCommand(request.name(), request.type(), request.currency());
        AccountId id = createAccountService.handle(command);

        AccountResponse response = 
            new AccountResponse(
                id.id().toString(),
                request.name(),
                request.type(),
                "ACTIVE",
                request.currency()
            );

        return ResponseEntity
            .created(URI.create("/accounts/" + id.id())) // Change to ServletUriComponentsBuilder
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id) {
        
        Account account = getAccountService.handle(id);

        AccountResponse accountResponse = new AccountResponse(
            account.getId().id().toString(),
            account.getName(),
            account.getType().name(),
            account.getStatus().name(),
            account.getCurrency().getCurrencyCode()
        );

        return ResponseEntity.ok(accountResponse);
    }
    
    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable UUID id){

        Money balance = calculateAccountBalanceService.handle(id);

        AccountBalanceResponse response = new AccountBalanceResponse(
            id.toString(),
            balance.amount(),
            balance.currency().getCurrencyCode()
        );
        
        return ResponseEntity.ok(response);
    }
}
