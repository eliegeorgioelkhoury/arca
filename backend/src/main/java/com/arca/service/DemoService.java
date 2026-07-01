package com.arca.service;

import com.arca.domain.Expense;
import com.arca.domain.ExpenseCategory;
import com.arca.domain.ExpenseStatus;
import com.arca.domain.JournalEntry;
import com.arca.domain.Role;
import com.arca.domain.Team;
import com.arca.domain.User;
import com.arca.repo.ExpenseRepository;
import com.arca.repo.JournalEntryRepository;
import com.arca.repo.TeamRepository;
import com.arca.repo.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotent demo data: three role accounts and a spread of sample expenses
 * (some approved so the ledger, trial balance and analytics have data). Also
 * powers the "reset demo" path so recruiter tinkering can't wreck the state.
 */
@Service
public class DemoService {

    private final UserRepository users;
    private final TeamRepository teams;
    private final ExpenseRepository expenses;
    private final JournalEntryRepository journalEntries;
    private final PasswordEncoder encoder;
    private final LedgerService ledger;
    private final String demoPassword;

    public DemoService(UserRepository users, TeamRepository teams, ExpenseRepository expenses,
            JournalEntryRepository journalEntries, PasswordEncoder encoder, LedgerService ledger,
            @Value("${app.demo.password}") String demoPassword) {
        this.users = users;
        this.teams = teams;
        this.expenses = expenses;
        this.journalEntries = journalEntries;
        this.encoder = encoder;
        this.ledger = ledger;
        this.demoPassword = demoPassword;
    }

    @Transactional
    public void ensureDemoUsers() {
        Team engineering = teams.findByName("Engineering").orElse(null);
        createIfMissing("employee@arca.demo", "Evan Employee", Role.EMPLOYEE, engineering);
        createIfMissing("manager@arca.demo", "Maria Manager", Role.MANAGER, engineering);
        createIfMissing("admin@arca.demo", "Aisha Admin", Role.ADMIN, null);
    }

    private void createIfMissing(String email, String fullName, Role role, Team team) {
        if (users.existsByEmail(email)) {
            return;
        }
        users.save(User.builder()
                .email(email)
                .fullName(fullName)
                .role(role)
                .team(team)
                .passwordHash(encoder.encode(demoPassword))
                .build());
    }

    @Transactional
    public void seedSampleExpensesIfEmpty() {
        if (expenses.count() > 0) {
            return;
        }
        User employee = users.findByEmail("employee@arca.demo").orElseThrow();
        User manager = users.findByEmail("manager@arca.demo").orElseThrow();

        createExpense(employee, "120.50", ExpenseCategory.TRAVEL, "Taxi to client site", "Uber", 10, manager);
        createExpense(employee, "64.20", ExpenseCategory.MEALS, "Team lunch", "Sweetgreen", 8, manager);
        createExpense(employee, "299.00", ExpenseCategory.SOFTWARE, "JetBrains license", "JetBrains", 6, manager);
        createExpense(employee, "45.00", ExpenseCategory.OFFICE, "Notebooks & pens", "Staples", 3, null);
        createExpense(employee, "1500.00", ExpenseCategory.EQUIPMENT, "Standing desk", "Fully", 2, null);
        createExpense(employee, "18.99", ExpenseCategory.MEALS, "Client coffee", "Blue Bottle", 1, null);
    }

    private void createExpense(User submitter, String amount, ExpenseCategory category, String description,
            String merchant, int daysAgo, User approver) {
        Expense e = expenses.save(Expense.builder()
                .submitter(submitter)
                .team(submitter.getTeam())
                .amount(new BigDecimal(amount))
                .currency("USD")
                .category(category)
                .description(description)
                .merchant(merchant)
                .status(ExpenseStatus.SUBMITTED)
                .spentOn(LocalDate.now().minusDays(daysAgo))
                .build());
        if (approver != null) {
            JournalEntry entry = ledger.postExpenseApproval(e, approver.getId());
            e.setStatus(ExpenseStatus.APPROVED);
            e.setDecidedAt(Instant.now());
            e.setDecidedBy(approver);
            e.setDecisionComment("Looks good");
            e.setJournalEntryId(entry.getId());
            expenses.save(e);
        }
    }

    /** Clear transactional data (keep users, teams, chart of accounts) and reseed. */
    @Transactional
    public Map<String, Object> reset() {
        expenses.deleteAllInBatch();        // receipts removed by ON DELETE CASCADE
        journalEntries.deleteAllInBatch();  // journal_lines removed by ON DELETE CASCADE
        seedSampleExpensesIfEmpty();
        return Map.of("reset", true, "expenses", expenses.count());
    }
}
