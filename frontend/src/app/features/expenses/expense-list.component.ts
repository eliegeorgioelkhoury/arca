import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ExpenseService } from '../../core/expense.service';
import { Expense, ExpenseStatus } from '../../core/models';
import { MoneyPipe } from '../../shared/money.pipe';
import { CountUpDirective } from '../../shared/count-up.directive';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [RouterLink, DatePipe, MoneyPipe, CountUpDirective],
  template: `
    <div class="spread">
      <div>
        <h1>Expenses</h1>
        <p class="muted">{{ subtitle() }}</p>
      </div>
      <a class="btn" routerLink="/expenses/new" data-testid="new-expense">New expense</a>
    </div>

    @if (!isEmployee()) {
      <div class="row filters">
        <button class="btn secondary" [class.active]="filter() === undefined" (click)="setFilter(undefined)">All</button>
        <button class="btn secondary" [class.active]="filter() === 'SUBMITTED'" (click)="setFilter('SUBMITTED')">Pending</button>
        <button class="btn secondary" [class.active]="filter() === 'APPROVED'" (click)="setFilter('APPROVED')">Approved</button>
        <button class="btn secondary" [class.active]="filter() === 'REJECTED'" (click)="setFilter('REJECTED')">Rejected</button>
      </div>
    }

    <div class="card reveal" style="margin-top: 16px">
      <div class="spread" style="margin-bottom: 8px">
        <span class="muted small">{{ expenses().length }} items</span>
        <span class="muted small">Total approved: <strong class="credit" [appCountUp]="approvedTotal()" prefix="$"></strong></span>
      </div>
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Category</th>
            <th>Merchant</th>
            <th class="num">Amount</th>
            <th>Status</th>
            <th>Receipts</th>
          </tr>
        </thead>
        <tbody>
          @for (e of expenses(); track e.id) {
            <tr data-testid="expense-row" [attr.data-status]="e.status" [attr.data-id]="e.id">
              <td>{{ e.spentOn | date: 'MMM d' }}</td>
              <td>{{ e.category }}</td>
              <td>{{ e.merchant || '—' }}</td>
              <td class="num">{{ e.amount | money: e.currency }}</td>
              <td><span class="chip status-{{ e.status }}" data-testid="status">{{ e.status }}</span></td>
              <td>{{ e.receiptCount }}</td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="muted">No expenses yet.</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [
    `
      .filters {
        margin-top: 12px;
      }
      .filters .btn.active {
        border-color: var(--brand);
        color: var(--brand);
      }
    `,
  ],
})
export class ExpenseListComponent implements OnInit {
  private svc = inject(ExpenseService);
  private auth = inject(AuthService);

  expenses = signal<Expense[]>([]);
  filter = signal<ExpenseStatus | undefined>(undefined);

  approvedTotal = computed(() =>
    this.expenses()
      .filter((e) => e.status === 'APPROVED')
      .reduce((sum, e) => sum + e.amount, 0),
  );

  isEmployee = (): boolean => this.auth.hasRole('EMPLOYEE');
  subtitle = (): string => (this.isEmployee() ? 'Your submitted expenses' : 'All team expenses');

  ngOnInit(): void {
    this.load();
  }

  setFilter(status?: ExpenseStatus): void {
    this.filter.set(status);
    this.load();
  }

  private load(): void {
    this.svc.list(this.filter()).subscribe((x) => this.expenses.set(x));
  }
}
