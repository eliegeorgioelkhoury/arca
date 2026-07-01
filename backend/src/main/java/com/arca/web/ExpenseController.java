package com.arca.web;

import com.arca.domain.ExpenseStatus;
import com.arca.security.AuthUser;
import com.arca.service.ExpenseService;
import com.arca.web.dto.CreateExpenseRequest;
import com.arca.web.dto.DecisionRequest;
import com.arca.web.dto.ExpenseDto;
import com.arca.web.dto.ReceiptDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses")
public class ExpenseController {

    private final ExpenseService expenses;

    public ExpenseController(ExpenseService expenses) {
        this.expenses = expenses;
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> create(
            @AuthenticationPrincipal AuthUser me, @Valid @RequestBody CreateExpenseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenses.create(me.getId(), req));
    }

    @GetMapping
    public List<ExpenseDto> list(
            @AuthenticationPrincipal AuthUser me, @RequestParam(required = false) ExpenseStatus status) {
        return expenses.list(me, status);
    }

    /** Manager/admin approval queue (pending expenses). */
    @GetMapping("/pending")
    public List<ExpenseDto> pending() {
        return expenses.pendingQueue();
    }

    @GetMapping("/{id}")
    public ExpenseDto get(@AuthenticationPrincipal AuthUser me, @PathVariable Long id) {
        return expenses.get(id, me);
    }

    @GetMapping("/{id}/receipts")
    public List<ReceiptDto> receipts(@AuthenticationPrincipal AuthUser me, @PathVariable Long id) {
        return expenses.receipts(id, me);
    }

    @PostMapping(value = "/{id}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReceiptDto upload(
            @AuthenticationPrincipal AuthUser me, @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return expenses.addReceipt(id, me, file.getBytes(), file.getOriginalFilename(), file.getContentType());
    }

    @PostMapping("/{id}/approve")
    public ExpenseDto approve(
            @AuthenticationPrincipal AuthUser me, @PathVariable Long id,
            @RequestBody(required = false) DecisionRequest body) {
        return expenses.approve(id, me.getId(), body != null ? body.comment() : null);
    }

    @PostMapping("/{id}/reject")
    public ExpenseDto reject(
            @AuthenticationPrincipal AuthUser me, @PathVariable Long id,
            @Valid @RequestBody DecisionRequest body) {
        return expenses.reject(id, me.getId(), body != null ? body.comment() : null);
    }
}
