package com.arca.ledger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.arca.support.AbstractPostgresIT;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The headline invariant test (milestone 3): the double-entry sum-to-zero rule is
 * enforced by the database itself. Runs against real Postgres, and must fail on any
 * unbalanced entry.
 */
@SpringBootTest
class LedgerConstraintIT extends AbstractPostgresIT {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    PlatformTransactionManager txManager;

    @Test
    void balancedEntryCommits() {
        Long expenseAccount = accountId("5000");
        Long payable = accountId("2000");

        Long entryId = new TransactionTemplate(txManager).execute(status -> {
            Long id = insertEntry("balanced");
            insertLine(id, expenseAccount, "100.00");
            insertLine(id, payable, "-100.00");
            return id;
        });

        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM journal_lines WHERE entry_id = ?", Integer.class, entryId))
                .isEqualTo(2);
    }

    @Test
    void unbalancedEntryIsRejectedAtCommit() {
        Long expenseAccount = accountId("5000");
        TransactionTemplate tx = new TransactionTemplate(txManager);

        assertThatThrownBy(() -> tx.executeWithoutResult(status -> {
            Long id = insertEntry("unbalanced");
            insertLine(id, expenseAccount, "100.00"); // no balancing credit line
        })).hasStackTraceContaining("unbalanced");
    }

    @Test
    void removingOneSideUnbalancesAndIsRejected() {
        Long expenseAccount = accountId("5000");
        Long payable = accountId("2000");

        Long entryId = new TransactionTemplate(txManager).execute(status -> {
            Long id = insertEntry("initially balanced");
            insertLine(id, expenseAccount, "50.00");
            insertLine(id, payable, "-50.00");
            return id;
        });

        TransactionTemplate tx = new TransactionTemplate(txManager);
        assertThatThrownBy(() -> tx.executeWithoutResult(status ->
                jdbc.update("DELETE FROM journal_lines WHERE entry_id = ? AND amount < 0", entryId)))
                .hasStackTraceContaining("unbalanced");
    }

    private Long insertEntry(String memo) {
        return jdbc.queryForObject(
                "INSERT INTO journal_entries(entry_date, memo) VALUES (current_date, ?) RETURNING id",
                Long.class, memo);
    }

    private void insertLine(Long entryId, Long accountId, String amount) {
        jdbc.update("INSERT INTO journal_lines(entry_id, account_id, amount) VALUES (?, ?, ?)",
                entryId, accountId, new BigDecimal(amount));
    }

    private Long accountId(String code) {
        return jdbc.queryForObject("SELECT id FROM accounts WHERE code = ?", Long.class, code);
    }
}
