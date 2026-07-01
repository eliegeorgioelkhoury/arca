package com.arca.web.dto;

import com.arca.domain.JournalLine;
import java.math.BigDecimal;

public record JournalLineDto(
        Long id,
        String accountCode,
        String accountName,
        String side,
        BigDecimal amount) {

    public static JournalLineDto from(JournalLine l) {
        boolean debit = l.getAmount().signum() >= 0;
        return new JournalLineDto(
                l.getId(),
                l.getAccount().getCode(),
                l.getAccount().getName(),
                debit ? "DEBIT" : "CREDIT",
                l.getAmount().abs());
    }
}
