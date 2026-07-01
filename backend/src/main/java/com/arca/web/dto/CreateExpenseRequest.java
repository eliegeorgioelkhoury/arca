package com.arca.web.dto;

import com.arca.domain.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        @NotNull @DecimalMin(value = "0.01", message = "amount must be positive") BigDecimal amount,
        @NotNull ExpenseCategory category,
        @Size(max = 3) String currency,
        @Size(max = 500) String description,
        @Size(max = 150) String merchant,
        @NotNull @PastOrPresent LocalDate spentOn) {
}
