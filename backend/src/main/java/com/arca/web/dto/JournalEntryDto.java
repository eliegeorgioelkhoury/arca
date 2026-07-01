package com.arca.web.dto;

import com.arca.domain.JournalEntry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record JournalEntryDto(
        Long id,
        LocalDate entryDate,
        String memo,
        String sourceType,
        Long sourceId,
        List<JournalLineDto> lines,
        BigDecimal totalDebit,
        BigDecimal totalCredit) {

    public static JournalEntryDto from(JournalEntry e) {
        List<JournalLineDto> lines = e.getLines().stream().map(JournalLineDto::from).toList();
        BigDecimal totalDebit = lines.stream()
                .filter(l -> l.side().equals("DEBIT"))
                .map(JournalLineDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = lines.stream()
                .filter(l -> l.side().equals("CREDIT"))
                .map(JournalLineDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new JournalEntryDto(
                e.getId(), e.getEntryDate(), e.getMemo(), e.getSourceType(), e.getSourceId(),
                lines, totalDebit, totalCredit);
    }
}
