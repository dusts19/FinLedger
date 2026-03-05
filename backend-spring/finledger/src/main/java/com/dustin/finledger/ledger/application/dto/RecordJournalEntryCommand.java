package com.dustin.finledger.ledger.application.dto;

import java.util.List;

public record RecordJournalEntryCommand(
    String description,
    List<JournalLineCommand> lines
) {}
