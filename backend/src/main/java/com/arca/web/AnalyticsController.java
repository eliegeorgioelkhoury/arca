package com.arca.web;

import com.arca.service.AnalyticsService;
import com.arca.web.dto.AnalyticsDtos.CategorySpend;
import com.arca.web.dto.AnalyticsDtos.MonthlySpend;
import com.arca.web.dto.AnalyticsDtos.TeamSpend;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/by-category")
    public List<CategorySpend> byCategory() {
        return analytics.byCategory();
    }

    @GetMapping("/by-month")
    public List<MonthlySpend> byMonth() {
        return analytics.byMonth();
    }

    @GetMapping("/by-team")
    public List<TeamSpend> byTeam() {
        return analytics.byTeam();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"arca-approved-expenses.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(analytics.exportCsv());
    }
}
