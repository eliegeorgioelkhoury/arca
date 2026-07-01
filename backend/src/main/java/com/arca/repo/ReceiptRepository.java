package com.arca.repo;

import com.arca.domain.Receipt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByExpense_IdOrderByUploadedAtDesc(Long expenseId);
}
