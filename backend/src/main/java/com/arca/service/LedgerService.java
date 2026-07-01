package com.arca.service;

import com.arca.domain.Account;
import com.arca.domain.Expense;
import com.arca.domain.JournalEntry;
import com.arca.domain.JournalLine;
import com.arca.error.NotFoundException;
import com.arca.repo.AccountRepository;
import com.arca.repo.JournalEntryRepository;
import com.arca.web.dto.JournalEntryDto;
import com.arca.web.dto.TrialBalanceDto;
import com.arca.web.dto.TrialBalanceRowDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {

    private static final String ACCOUNTS_PAYABLE = "2000";

    private final AccountRepository accounts;
    private final JournalEntryRepository entries;
    private final JdbcTemplate jdbc;

    public LedgerService(AccountRepository accounts, JournalEntryRepository entries, JdbcTemplate jdbc) {
        this.accounts = accounts;
        this.entries = entries;
        this.jdbc = jdbc;
    }

    /**
     * Post the balanced double-entry for an approved expense:
     * debit the category's expense account, credit Accounts Payable.
     * The two lines sum to zero, which the DB constraint trigger verifies at commit.
     */
    @Transactional
    public JournalEntry postExpenseApproval(Expense expense, Long actingUserId) {
        Account expenseAccount = accounts.findByCode(expense.getCategory().accountCode())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing expense account " + expense.getCategory().accountCode()));
        Account payable = accounts.findByCode(ACCOUNTS_PAYABLE)
                .orElseThrow(() -> new IllegalStateException("Missing Accounts Payable account"));

        BigDecimal amount = expense.getAmount();
        JournalEntry entry = JournalEntry.builder()
                .entryDate(LocalDate.now())
                .memo("Approved expense #" + expense.getId())
                .sourceType("EXPENSE_APPROVAL")
                .sourceId(expense.getId())
                .createdBy(actingUserId)
                .build();
        entry.addLine(JournalLine.builder()
                .account(expenseAccount).amount(amount)
                .description("Dr " + expenseAccount.getName()).build());
        entry.addLine(JournalLine.builder()
                .account(payable).amount(amount.negate())
                .description("Cr " + payable.getName()).build());
        return entries.save(entry);
    }

    @Transactional(readOnly = true)
    public TrialBalanceDto trialBalance() {
        List<TrialBalanceRowDto> rows = jdbc.query("""
                SELECT a.code, a.name, a.type, a.normal_side,
                       COALESCE(SUM(CASE WHEN l.amount > 0 THEN l.amount ELSE 0 END), 0) AS debit,
                       COALESCE(SUM(CASE WHEN l.amount < 0 THEN -l.amount ELSE 0 END), 0) AS credit,
                       COALESCE(SUM(l.amount), 0) AS balance
                  FROM accounts a
                  LEFT JOIN journal_lines l ON l.account_id = a.id
                 GROUP BY a.code, a.name, a.type, a.normal_side
                 ORDER BY a.code
                """, (rs, i) -> new TrialBalanceRowDto(
                rs.getString("code"), rs.getString("name"), rs.getString("type"), rs.getString("normal_side"),
                rs.getBigDecimal("debit"), rs.getBigDecimal("credit"), rs.getBigDecimal("balance")));

        BigDecimal totalDebit = rows.stream().map(TrialBalanceRowDto::debit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = rows.stream().map(TrialBalanceRowDto::credit).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TrialBalanceDto(rows, totalDebit, totalCredit, totalDebit.subtract(totalCredit));
    }

    @Transactional(readOnly = true)
    public JournalEntryDto getEntry(Long id) {
        return JournalEntryDto.from(entries.findById(id)
                .orElseThrow(() -> NotFoundException.of("Journal entry", id)));
    }
}
