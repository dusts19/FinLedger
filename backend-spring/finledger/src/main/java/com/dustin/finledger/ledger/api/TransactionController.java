package com.dustin.finledger.ledger.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dustin.finledger.ledger.api.dto.RecordTransactionRequest;
import com.dustin.finledger.ledger.api.dto.TransactionLineResponse;
import com.dustin.finledger.ledger.api.dto.TransactionResponse;
import com.dustin.finledger.ledger.application.GetJournalEntryService;
import com.dustin.finledger.ledger.application.RecordJournalEntryService;
import com.dustin.finledger.ledger.application.ReverseJournalEntryService;
import com.dustin.finledger.ledger.application.dto.JournalLineCommand;
import com.dustin.finledger.ledger.application.dto.RecordJournalEntryCommand;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    private final RecordJournalEntryService recordJournalEntryService;
    private final GetJournalEntryService getJournalEntryService;
    private final ReverseJournalEntryService reverseJournalEntryService;

    public TransactionController(RecordJournalEntryService recordJournalEntryService, GetJournalEntryService getJournalEntryService, ReverseJournalEntryService reverseJournalEntryService) {
        this.recordJournalEntryService = recordJournalEntryService;
        this.getJournalEntryService = getJournalEntryService;
        this.reverseJournalEntryService = reverseJournalEntryService;
    }

    @PostMapping
    public ResponseEntity<Void> recordTransaction(@RequestBody RecordTransactionRequest request) {
        
        RecordJournalEntryCommand command = new RecordJournalEntryCommand(
            request.description(),
            request.lines().stream()
                .map(line -> new JournalLineCommand(
                    AccountId.of(line.accountId()),
                    line.amount(),
                    line.currency(),
                    line.side(),
                    line.occurredAt()
                ))
                .toList()
            );


        JournalEntryId id = recordJournalEntryService.handle(command);

        URI location = URI.create("/transactions/" + id.id());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID rawJournalEntryId) {
        JournalEntryId journalEntryId = JournalEntryId.of(rawJournalEntryId);
        
        JournalEntry journalEntry = getJournalEntryService.handle(journalEntryId);
        TransactionResponse response = mapToResponse(journalEntry);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
        @RequestParam(required = false) UUID accountId) {
        
        List<JournalEntry> entries;

        if (accountId != null) {
            entries = getJournalEntryService.handleAllByAccount(AccountId.of(accountId));
        } else {
            entries = getJournalEntryService.handleAll();
        }

        List<TransactionResponse> responseList = entries.stream()
            .map(this::mapToResponse)
            .toList();
        
        return ResponseEntity.ok(responseList);
    }
    
    @PostMapping("/{id}/reverse")
    public ResponseEntity<TransactionResponse> reverse(@PathVariable UUID id) {
        JournalEntry reversedEntry = reverseJournalEntryService.handle(new JournalEntryId(id));

        TransactionResponse response = mapToResponse(reversedEntry);

        URI location = URI.create("/transactions/" + reversedEntry.getId().id());
        return ResponseEntity.created(location).body(response);
    }


    private TransactionResponse mapToResponse(JournalEntry entry) {
        return new TransactionResponse(
            entry.getId().toString(),
            entry.getDescription(),
            entry.getTimestamp(),
            entry.getLines().stream()
                .map(line -> new TransactionLineResponse(
                    line.id().toString(),
                    line.accountId().id().toString(),
                    line.amount().amount(),
                    line.amount().currency().getCurrencyCode(),
                    line.occurredAt(),
                    line.side().name()
                ))
                .toList(),
            false
        );
    }
}
