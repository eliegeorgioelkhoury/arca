package com.arca.service;

import com.arca.domain.Expense;
import com.arca.domain.ExpenseStatus;
import com.arca.domain.JournalEntry;
import com.arca.domain.Receipt;
import com.arca.domain.Role;
import com.arca.domain.User;
import com.arca.error.NotFoundException;
import com.arca.repo.ExpenseRepository;
import com.arca.repo.ReceiptRepository;
import com.arca.repo.UserRepository;
import com.arca.security.AuthUser;
import com.arca.web.dto.CreateExpenseRequest;
import com.arca.web.dto.ExpenseDto;
import com.arca.web.dto.ReceiptDto;
import java.time.Instant;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenses;
    private final UserRepository users;
    private final ReceiptRepository receipts;
    private final ReceiptStorageService storage;
    private final LedgerService ledger;

    public ExpenseService(ExpenseRepository expenses, UserRepository users, ReceiptRepository receipts,
            ReceiptStorageService storage, LedgerService ledger) {
        this.expenses = expenses;
        this.users = users;
        this.receipts = receipts;
        this.storage = storage;
        this.ledger = ledger;
    }

    @Transactional
    public ExpenseDto create(Long submitterId, CreateExpenseRequest req) {
        User submitter = users.findById(submitterId).orElseThrow(() -> NotFoundException.of("User", submitterId));
        Expense e = Expense.builder()
                .submitter(submitter)
                .team(submitter.getTeam())
                .amount(req.amount())
                .currency(req.currency() != null && !req.currency().isBlank() ? req.currency().toUpperCase() : "USD")
                .category(req.category())
                .description(req.description())
                .merchant(req.merchant())
                .status(ExpenseStatus.SUBMITTED)
                .spentOn(req.spentOn())
                .build();
        return ExpenseDto.from(expenses.save(e));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> list(AuthUser me, ExpenseStatus status) {
        List<Expense> result;
        if (me.getRole() == Role.EMPLOYEE) {
            result = expenses.findBySubmitter_IdOrderBySubmittedAtDesc(me.getId());
            if (status != null) {
                result = result.stream().filter(e -> e.getStatus() == status).toList();
            }
        } else {
            result = status != null
                    ? expenses.findByStatusOrderBySubmittedAtDesc(status)
                    : expenses.findAllByOrderBySubmittedAtDesc();
        }
        return result.stream().map(ExpenseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> pendingQueue() {
        return expenses.findByStatusOrderBySubmittedAtDesc(ExpenseStatus.SUBMITTED)
                .stream().map(ExpenseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ExpenseDto get(Long id, AuthUser me) {
        return ExpenseDto.from(loadAuthorized(id, me));
    }

    @Transactional(readOnly = true)
    public List<ReceiptDto> receipts(Long id, AuthUser me) {
        loadAuthorized(id, me);
        return receipts.findByExpense_IdOrderByUploadedAtDesc(id).stream().map(ReceiptDto::from).toList();
    }

    @Transactional
    public ReceiptDto addReceipt(Long id, AuthUser me, byte[] content, String filename, String contentType) {
        Expense e = loadAuthorized(id, me);
        if (me.getRole() == Role.EMPLOYEE && e.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException("Receipts can only be added while the expense is pending");
        }
        ReceiptStorageService.StoredReceipt stored = storage.store(content, filename, contentType);
        Receipt r = Receipt.builder()
                .expense(e)
                .storageKey(stored.storageKey())
                .originalFilename(filename)
                .contentType(contentType)
                .sizeBytes(stored.sizeBytes())
                .build();
        return ReceiptDto.from(receipts.save(r));
    }

    @Transactional
    public ExpenseDto approve(Long id, Long managerId, String comment) {
        Expense e = expenses.findById(id).orElseThrow(() -> NotFoundException.of("Expense", id));
        requirePending(e);
        JournalEntry entry = ledger.postExpenseApproval(e, managerId);
        e.setStatus(ExpenseStatus.APPROVED);
        e.setDecidedAt(Instant.now());
        e.setDecidedBy(users.getReferenceById(managerId));
        e.setDecisionComment(comment);
        e.setJournalEntryId(entry.getId());
        return ExpenseDto.from(expenses.save(e));
    }

    @Transactional
    public ExpenseDto reject(Long id, Long managerId, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("A rejection comment is required");
        }
        Expense e = expenses.findById(id).orElseThrow(() -> NotFoundException.of("Expense", id));
        requirePending(e);
        e.setStatus(ExpenseStatus.REJECTED);
        e.setDecidedAt(Instant.now());
        e.setDecidedBy(users.getReferenceById(managerId));
        e.setDecisionComment(comment);
        return ExpenseDto.from(expenses.save(e));
    }

    private Expense loadAuthorized(Long id, AuthUser me) {
        Expense e = expenses.findById(id).orElseThrow(() -> NotFoundException.of("Expense", id));
        if (me.getRole() == Role.EMPLOYEE && !e.getSubmitter().getId().equals(me.getId())) {
            throw new AccessDeniedException("Not your expense");
        }
        return e;
    }

    private void requirePending(Expense e) {
        if (e.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalStateException("Expense is not pending (status " + e.getStatus() + ")");
        }
    }
}
