package com.arca.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import com.arca.domain.ExpenseCategory;
import com.arca.domain.Role;
import com.arca.domain.User;
import com.arca.repo.UserRepository;
import com.arca.service.ExpenseService;
import com.arca.service.LedgerService;
import com.arca.support.AbstractPostgresIT;
import com.arca.web.dto.CreateExpenseRequest;
import com.arca.web.dto.ExpenseDto;
import com.arca.web.dto.JournalEntryDto;
import com.arca.web.dto.TrialBalanceDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Approving an expense posts a balanced entry and the whole ledger stays at zero. */
@SpringBootTest
class LedgerServiceIT extends AbstractPostgresIT {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    UserRepository users;

    @Test
    void approvingExpensePostsBalancedEntryAndTrialBalanceIsZero() {
        User employee = users.findFirstByRole(Role.EMPLOYEE).orElseThrow();
        User manager = users.findFirstByRole(Role.MANAGER).orElseThrow();

        ExpenseDto created = expenseService.create(employee.getId(), new CreateExpenseRequest(
                new BigDecimal("250.00"), ExpenseCategory.SOFTWARE, "USD", "IntelliJ", "JetBrains", LocalDate.now()));

        ExpenseDto approved = expenseService.approve(created.id(), manager.getId(), "approved");

        assertThat(approved.status()).isEqualTo("APPROVED");
        assertThat(approved.journalEntryId()).isNotNull();

        JournalEntryDto entry = ledgerService.getEntry(approved.journalEntryId());
        assertThat(entry.totalDebit()).isEqualByComparingTo(entry.totalCredit());
        assertThat(entry.totalDebit()).isEqualByComparingTo("250.00");

        TrialBalanceDto trialBalance = ledgerService.trialBalance();
        assertThat(trialBalance.balance()).isEqualByComparingTo("0.00");
    }
}
