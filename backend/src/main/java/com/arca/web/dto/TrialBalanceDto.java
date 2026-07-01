package com.arca.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * The whole point of double-entry: {@code balance} (totalDebit - totalCredit) is always zero.
 */
public record TrialBalanceDto(
        List<TrialBalanceRowDto> rows,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        BigDecimal balance) {
}
