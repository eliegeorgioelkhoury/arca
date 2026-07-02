import { Component, inject, OnInit, signal } from '@angular/core';
import { AnalyticsService } from '../../core/analytics.service';
import { LedgerService } from '../../core/ledger.service';
import { CategorySpend, TeamSpend, TrialBalance } from '../../core/models';
import { MoneyPipe } from '../../shared/money.pipe';
import { CountUpDirective } from '../../shared/count-up.directive';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [MoneyPipe, CountUpDirective],
  template: `
    <div class="spread">
      <div>
        <h1>Admin analytics</h1>
        <p class="muted">Approved spend and the always-balanced trial balance.</p>
      </div>
      <button class="btn secondary" (click)="exportCsv()" data-testid="export-csv">Export CSV</button>
    </div>

    <div class="grid cols" style="margin-top: 12px">
      <div class="card reveal">
        <h2>Spend by category</h2>
        @for (c of byCategory(); track c.category) {
          <div class="bar-row">
            <span class="bar-label">{{ c.category }}</span>
            <div class="bar"><div class="bar-fill credit-bg" [style.width.%]="pct(c.total, byCategory())"></div></div>
            <span class="bar-val">{{ c.total | money }}</span>
          </div>
        } @empty {
          <p class="muted">No approved spend yet.</p>
        }
      </div>
      <div class="card reveal">
        <h2>Spend by team</h2>
        @for (t of byTeam(); track t.team) {
          <div class="bar-row">
            <span class="bar-label">{{ t.team }}</span>
            <div class="bar"><div class="bar-fill" [style.width.%]="pctTeam(t.total, byTeam())"></div></div>
            <span class="bar-val">{{ t.total | money }}</span>
          </div>
        } @empty {
          <p class="muted">No data.</p>
        }
      </div>
    </div>

    <div class="card reveal" style="margin-top: 16px">
      <div class="spread">
        <h2>Trial balance</h2>
        <span class="chip credit" data-testid="balanced-badge">Balanced ✓</span>
      </div>
      <table>
        <thead>
          <tr>
            <th>Code</th>
            <th>Account</th>
            <th class="num">Debit</th>
            <th class="num">Credit</th>
          </tr>
        </thead>
        <tbody>
          @for (r of trial()?.rows || []; track r.accountCode) {
            <tr>
              <td>{{ r.accountCode }}</td>
              <td>{{ r.accountName }}</td>
              <td class="num debit">{{ r.debit ? (r.debit | money) : '' }}</td>
              <td class="num credit">{{ r.credit ? (r.credit | money) : '' }}</td>
            </tr>
          }
        </tbody>
        <tfoot>
          <tr class="totals">
            <td colspan="2"><strong>Totals</strong></td>
            <td class="num debit"><strong>$<span [appCountUp]="trial()?.totalDebit || 0"></span></strong></td>
            <td class="num credit"><strong>$<span [appCountUp]="trial()?.totalCredit || 0"></span></strong></td>
          </tr>
          <tr>
            <td colspan="2" class="muted">Debits − Credits</td>
            <td colspan="2" class="num" data-testid="trial-balance-value">
              <strong>{{ (trial()?.balance ?? 0) | money }}</strong>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  `,
  styles: [
    `
      .cols {
        grid-template-columns: 1fr 1fr;
      }
      @media (max-width: 720px) {
        .cols {
          grid-template-columns: 1fr;
        }
      }
      .bar-row {
        display: grid;
        grid-template-columns: 120px 1fr auto;
        gap: var(--space-3);
        align-items: center;
        margin: 10px 0;
      }
      .bar {
        background: #ece6da;
        border-radius: 999px;
        height: 14px;
        overflow: hidden;
      }
      .bar-fill {
        height: 100%;
        background: var(--copper);
        border-radius: 999px;
        transform-origin: left center;
        animation: grow-x 0.7s cubic-bezier(0.22, 1, 0.36, 1) both;
      }
      .bar-fill.credit-bg {
        background: var(--credit);
      }
      .bar-label {
        font-size: 13px;
        color: var(--muted);
      }
      .bar-val {
        font-family: var(--font-mono);
        font-variant-numeric: tabular-nums;
        font-size: 13px;
        color: var(--midnight);
      }
      .totals td {
        border-top: 2px solid var(--border-strong);
      }
    `,
  ],
})
export class AdminComponent implements OnInit {
  private analytics = inject(AnalyticsService);
  private ledger = inject(LedgerService);

  byCategory = signal<CategorySpend[]>([]);
  byTeam = signal<TeamSpend[]>([]);
  trial = signal<TrialBalance | null>(null);

  ngOnInit(): void {
    this.analytics.byCategory().subscribe((x) => this.byCategory.set(x));
    this.analytics.byTeam().subscribe((x) => this.byTeam.set(x));
    this.ledger.trialBalance().subscribe((x) => this.trial.set(x));
  }

  pct(total: number, rows: CategorySpend[]): number {
    const max = Math.max(1, ...rows.map((r) => r.total));
    return (total / max) * 100;
  }

  pctTeam(total: number, rows: TeamSpend[]): number {
    const max = Math.max(1, ...rows.map((r) => r.total));
    return (total / max) * 100;
  }

  exportCsv(): void {
    this.analytics.exportCsv().subscribe((csv) => {
      const blob = new Blob([csv], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'arca-approved-expenses.csv';
      a.click();
      URL.revokeObjectURL(url);
    });
  }
}
