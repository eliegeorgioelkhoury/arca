package com.arca.web;

import com.arca.service.LedgerService;
import com.arca.web.dto.JournalEntryDto;
import com.arca.web.dto.TrialBalanceDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
@Tag(name = "Ledger")
public class LedgerController {

    private final LedgerService ledger;

    public LedgerController(LedgerService ledger) {
        this.ledger = ledger;
    }

    @GetMapping("/trial-balance")
    public TrialBalanceDto trialBalance() {
        return ledger.trialBalance();
    }

    @GetMapping("/entries/{id}")
    public JournalEntryDto entry(@PathVariable Long id) {
        return ledger.getEntry(id);
    }
}
