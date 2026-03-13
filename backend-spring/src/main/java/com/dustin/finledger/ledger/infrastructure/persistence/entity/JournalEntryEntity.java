package com.dustin.finledger.ledger.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dustin.finledger.ledger.domain.journal.JournalEntry;
import com.dustin.finledger.ledger.domain.journal.JournalEntryId;
import com.dustin.finledger.ledger.domain.journal.JournalLine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
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

    @Column(name = "created_at", nullable = false)
    private Instant timestamp;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "journal_entry_id")
    private List<JournalLineEntity> lines = new ArrayList<>();

    @Column(name = "posted", nullable = false)
    private boolean posted;
    
    protected JournalEntryEntity() {};

    public JournalEntryEntity(UUID id, String description, Instant timestamp, List<JournalLineEntity> lines, boolean posted){
        this.id = id;
        this.description  = description;
        this.timestamp = timestamp;
        this.lines = lines;
        this.posted = posted;
    }

    public static JournalEntryEntity fromDomain(JournalEntry domain) {
        JournalEntryEntity entity = new JournalEntryEntity(
            domain.getId().id(),
            domain.getDescription(),
            domain.getTimestamp(),
            new ArrayList<>(),
            domain.isPosted()
        );

        for (JournalLine line : domain.getLines()) {
            JournalLineEntity lineEntity = JournalLineEntity.fromDomain(line);
            lineEntity.setJournalEntry(entity);
            entity.lines.add(lineEntity);
        }
        return entity;
    }
    public JournalEntry toDomain() {
        JournalEntry entry = new JournalEntry(new JournalEntryId(this.getId()), this.getDescription());
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
    public Instant getTimestamp() { return timestamp; }
    public List<JournalLineEntity> getLines() { return lines; }
    public boolean getPosted() { return posted; }
}
