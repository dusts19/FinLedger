package com.dustin.finledger.ledger.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.repository.JournalEntryRepository;

@ExtendWith(MockitoExtension.class)
public class ReverseJournalEntryServiceTest {
    
    @Mock
    private JournalEntryRepository journalEntryRepository;

    @InjectMocks
    private ReverseJournalEntryService service;

    @Test
    void handle_shouldReverseAndSave_whenEntryIsPosted() {
        JournalEntryId id = JournalEntryId.newId();
        JournalEntry original = mock(JournalEntry.class);
        JournalEntry reversal = mock(JournalEntry.class);

        when(journalEntryRepository.getById(id))
            .thenReturn(Optional.of(original));
        
        when(original.isPosted()).thenReturn(true);
        when(original.reverse()).thenReturn(reversal);

        JournalEntry result = service.handle(id);

        assertThat(result).isEqualTo(reversal);
        verify(journalEntryRepository).save(reversal);
    }

    @Test
    void handle_shouldThrow_whenEntryNotFound() {

        JournalEntryId id = JournalEntryId.newId();

        when(journalEntryRepository.getById(id))
            .thenReturn(Optional.empty());

        DomainException ex =
            assertThrows(DomainException.class, () -> service.handle(id));
        
        assertTrue(ex.getMessage().contains("Journal entry not found"));

        verify(journalEntryRepository).getById(id);
        verify(journalEntryRepository, never()).save(any());
    }

    @Test
    void handle_shouldThrow_whenEntryNotPosted() {
        JournalEntryId id = JournalEntryId.newId();
        JournalEntry original = mock(JournalEntry.class);

        when(journalEntryRepository.getById(id))
            .thenReturn(Optional.of(original));
        
        when(original.isPosted()).thenReturn(false);

        DomainException ex =
            assertThrows(DomainException.class, () -> service.handle(id));
        
            
        assertTrue(ex.getMessage().contains("Cannot reverse an unposted journal entry"));

        verify(journalEntryRepository).getById(id);
        verify(original).isPosted();
        verify(journalEntryRepository, never()).save(any());
    }
}
