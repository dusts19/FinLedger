package com.dustin.finledger.ledger.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
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
import com.dustin.finledger.ledger.api.dto.RecordTransactionRequest;
import com.dustin.finledger.ledger.application.GetJournalEntryService;
import com.dustin.finledger.ledger.application.RecordJournalEntryService;
import com.dustin.finledger.ledger.application.ReverseJournalEntryService;
import com.dustin.finledger.ledger.domain.account.AccountId;
import com.dustin.finledger.ledger.domain.journal.EntrySide;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.journal.JournalLine;
import com.dustin.finledger.ledger.domain.journal.JournalLineId;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private RecordJournalEntryService recordJournalEntryService;

    @MockitoBean
    private GetJournalEntryService getJournalEntryService;

    @MockitoBean
    private ReverseJournalEntryService reverseJournalEntryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recordTransaction_shouldReturn201() throws Exception {
        JournalEntryId id = JournalEntryId.newId();

        when(recordJournalEntryService.handle(any()))
                .thenReturn(id);
        
        RecordTransactionRequest request =
                TestData.sampleTransactionRequest();

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/transactions/" + id.id()));
    }

    @Test
    void getTransaction_shouldReturnTransactionResponse() throws Exception {
        UUID rawId = UUID.randomUUID();

        JournalEntryId journalEntryId = JournalEntryId.of(rawId);

        JournalEntry entry = new JournalEntry(journalEntryId, "Test transaction");
        JournalLine debit = new JournalLine(
            JournalLineId.newId(),
            AccountId.newId(),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.DEBIT
        );
        JournalLine credit = new JournalLine(
            JournalLineId.newId(),
            AccountId.newId(),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            Instant.now(),
            EntrySide.CREDIT
        );
        entry.addLine(debit);
        entry.addLine(credit);
        entry.post();

        when(getJournalEntryService.handle(journalEntryId)).thenReturn(entry);

        mockMvc.perform(get("/transactions/{id}", rawId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rawId.toString()))
                .andExpect(jsonPath("$.description").value("Test transaction"))
                .andExpect(jsonPath("$.lines[0].amount").value(100.00))
                .andExpect(jsonPath("$.lines[0].currency").value("USD"))
                .andExpect(jsonPath("$.posted").value(true));
    }

    
    @Test
    void reverseTransaction_shouldReturn201() throws Exception {
        UUID id = UUID.randomUUID();

        JournalEntry reversed = mock(JournalEntry.class);
        JournalEntryId reversedId = JournalEntryId.newId();

        when(reverseJournalEntryService.handle(any()))
                .thenReturn(reversed);
    
        when(reversed.getId()).thenReturn(reversedId);

        mockMvc.perform(post("/transactions/{id}/reverse", id))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/transactions/" + reversedId.id()));
    }

    
        
}
