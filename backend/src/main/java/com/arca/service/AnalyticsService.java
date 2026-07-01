package com.arca.service;

import com.arca.web.dto.AnalyticsDtos.CategorySpend;
import com.arca.web.dto.AnalyticsDtos.MonthlySpend;
import com.arca.web.dto.AnalyticsDtos.TeamSpend;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final JdbcTemplate jdbc;

    public AnalyticsService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CategorySpend> byCategory() {
        return jdbc.query("""
                SELECT category, COALESCE(SUM(amount), 0) AS total, COUNT(*) AS cnt
                  FROM expenses WHERE status = 'APPROVED'
                 GROUP BY category ORDER BY total DESC
                """, (rs, i) -> new CategorySpend(
                rs.getString("category"), rs.getBigDecimal("total"), rs.getLong("cnt")));
    }

    public List<MonthlySpend> byMonth() {
        return jdbc.query("""
                SELECT to_char(spent_on, 'YYYY-MM') AS month, COALESCE(SUM(amount), 0) AS total, COUNT(*) AS cnt
                  FROM expenses WHERE status = 'APPROVED'
                 GROUP BY 1 ORDER BY 1
                """, (rs, i) -> new MonthlySpend(
                rs.getString("month"), rs.getBigDecimal("total"), rs.getLong("cnt")));
    }

    public List<TeamSpend> byTeam() {
        return jdbc.query("""
                SELECT COALESCE(t.name, 'Unassigned') AS team, COALESCE(SUM(e.amount), 0) AS total, COUNT(*) AS cnt
                  FROM expenses e LEFT JOIN teams t ON e.team_id = t.id
                 WHERE e.status = 'APPROVED'
                 GROUP BY t.name ORDER BY total DESC
                """, (rs, i) -> new TeamSpend(
                rs.getString("team"), rs.getBigDecimal("total"), rs.getLong("cnt")));
    }

    public String exportCsv() {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT e.id, e.spent_on, u.full_name AS submitter, COALESCE(t.name, 'Unassigned') AS team,
                       e.category, e.amount, e.currency, e.merchant, e.status
                  FROM expenses e
                  JOIN users u ON u.id = e.submitter_id
                  LEFT JOIN teams t ON t.id = e.team_id
                 WHERE e.status = 'APPROVED'
                 ORDER BY e.spent_on
                """);
        StringBuilder sb = new StringBuilder("id,spent_on,submitter,team,category,amount,currency,merchant,status\n");
        for (Map<String, Object> r : rows) {
            sb.append(csv(r.get("id"))).append(',')
                    .append(csv(r.get("spent_on"))).append(',')
                    .append(csv(r.get("submitter"))).append(',')
                    .append(csv(r.get("team"))).append(',')
                    .append(csv(r.get("category"))).append(',')
                    .append(csv(r.get("amount"))).append(',')
                    .append(csv(r.get("currency"))).append(',')
                    .append(csv(r.get("merchant"))).append(',')
                    .append(csv(r.get("status"))).append('\n');
        }
        return sb.toString();
    }

    private static String csv(Object o) {
        if (o == null) {
            return "";
        }
        String s = o.toString().replace("\"", "\"\"");
        return (s.contains(",") || s.contains("\"") || s.contains("\n")) ? "\"" + s + "\"" : s;
    }
}
