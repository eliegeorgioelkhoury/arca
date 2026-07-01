package com.arca.web.dto;

import com.arca.domain.Expense;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseDto(
        Long id,
        BigDecimal amount,
        String currency,
        String category,
        String status,
        String description,
        String merchant,
        LocalDate spentOn,
        Long submitterId,
        String submitterName,
        String teamName,
        Instant submittedAt,
        Instant decidedAt,
        String decidedByName,
        String decisionComment,
        Long journalEntryId,
        int receiptCount) {

    /** Call within an open transaction — reads the lazy receipts collection. */
    public static ExpenseDto from(Expense e) {
        return new ExpenseDto(
                e.getId(),
                e.getAmount(),
                e.getCurrency(),
                e.getCategory().name(),
                e.getStatus().name(),
                e.getDescription(),
                e.getMerchant(),
                e.getSpentOn(),
                e.getSubmitter().getId(),
                e.getSubmitter().getFullName(),
                e.getTeam() != null ? e.getTeam().getName() : null,
                e.getSubmittedAt(),
                e.getDecidedAt(),
                e.getDecidedBy() != null ? e.getDecidedBy().getFullName() : null,
                e.getDecisionComment(),
                e.getJournalEntryId(),
                e.getReceipts().size());
    }
}
