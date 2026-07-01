package com.arca.web.dto;

import java.math.BigDecimal;

/** Aggregated spend views over APPROVED expenses. */
public final class AnalyticsDtos {

    private AnalyticsDtos() {
    }

    public record CategorySpend(String category, BigDecimal total, long count) {
    }

    public record MonthlySpend(String month, BigDecimal total, long count) {
    }

    public record TeamSpend(String team, BigDecimal total, long count) {
    }
}
