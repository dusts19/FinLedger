package com.dustin.finledger.ledger.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "journal_entry")
public class JournalEntryEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "journal_entry_id")
    private List<JournalLineEntity> lines = new ArrayList<>();

    @Column(name = "posted", nullable = false)
    private boolean posted;
    
    protected JournalEntryEntity() {};

    public JournalEntryEntity(UUID id, String description, LocalDateTime timestamp, List<JournalLineEntity> lines, boolean posted){
        this.id = id;
        this.description  = description;
        this.timestamp = timestamp;
        this.lines = lines;
        this.posted = posted;
    }

    public static JournalEntryEntity fromDomain(JournalEntry domain) {
        List<JournalLineEntity> linesEntity = domain.getLines().stream()
            .map(JournalLineEntity::fromDomain)
            .toList();

        return new JournalEntryEntity(
            domain.getId().id(),
            domain.getDescription(),
            domain.getTimestamp(),
            linesEntity,
            domain.isPosted()
        );
    }
    public JournalEntry toDomain() {
        JournalEntry entry = JournalEntry.fromId(new JournalEntryId(this.getId()));
        for (JournalLineEntity lineEntity : this.lines) {
            entry.addLine(lineEntity.toDomain());
        }
        if (posted) {
            entry.post();
        }
        return entry;
    }

    public UUID getId() {  return id;  }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<JournalLineEntity> getLines() { return lines; }
    public boolean getPosted() { return posted; }
}
