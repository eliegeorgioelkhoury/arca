package com.arca.domain;

/**
 * Expense categories, each mapped to the expense account it debits when approved.
 * Centralising the mapping here keeps double-entry posting in one place.
 */
public enum ExpenseCategory {
    TRAVEL("5000"),
    MEALS("5100"),
    SOFTWARE("5200"),
    EQUIPMENT("5300"),
    OFFICE("5400"),
    OTHER("5900");

    private final String accountCode;

    ExpenseCategory(String accountCode) {
        this.accountCode = accountCode;
    }

    public String accountCode() {
        return accountCode;
    }
}
