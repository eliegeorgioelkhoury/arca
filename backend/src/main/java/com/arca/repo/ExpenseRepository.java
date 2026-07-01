package com.arca.repo;

import com.arca.domain.Expense;
import com.arca.domain.ExpenseStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findBySubmitter_IdOrderBySubmittedAtDesc(Long submitterId);

    List<Expense> findByStatusOrderBySubmittedAtDesc(ExpenseStatus status);

    List<Expense> findAllByOrderBySubmittedAtDesc();
}
