package com.arca.web.dto;

import java.math.BigDecimal;

public record TrialBalanceRowDto(
        String accountCode,
        String accountName,
        String type,
        String normalSide,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal balance) {
}
