import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ExpenseService } from '../../core/expense.service';
import { Expense } from '../../core/models';
import { MoneyPipe } from '../../shared/money.pipe';

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [DatePipe, MoneyPipe],
  template: `
    <h1>Approvals</h1>
    <p class="muted">Pending expenses awaiting your decision.</p>
    @if (message()) {
      <p class="credit" data-testid="approve-message" style="font-weight: 600">{{ message() }}</p>
    }
    <div class="card reveal" style="margin-top: 12px">
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Employee</th>
            <th>Category</th>
            <th>Merchant</th>
            <th class="num">Amount</th>
            <th>Decision</th>
          </tr>
        </thead>
        <tbody>
          @for (e of pending(); track e.id) {
            <tr data-testid="pending-row" [attr.data-id]="e.id">
              <td>{{ e.spentOn | date: 'MMM d' }}</td>
              <td>{{ e.submitterName }}</td>
              <td>{{ e.category }}</td>
              <td>{{ e.merchant || '—' }}</td>
              <td class="num">{{ e.amount | money: e.currency }}</td>
              <td class="row">
                <button class="btn credit" (click)="approve(e)" [attr.data-testid]="'approve-' + e.id" [disabled]="busy()">
                  Approve
                </button>
                <button class="btn danger" (click)="reject(e)" [attr.data-testid]="'reject-' + e.id" [disabled]="busy()">
                  Reject
                </button>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="muted" data-testid="empty-queue">Nothing pending. All caught up.</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class ApprovalsComponent implements OnInit {
  private svc = inject(ExpenseService);

  pending = signal<Expense[]>([]);
  busy = signal(false);
  message = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  approve(e: Expense): void {
    this.busy.set(true);
    this.svc.approve(e.id).subscribe({
      next: (x) => {
        this.message.set(`Approved — posted balanced journal entry #${x.journalEntryId}.`);
        this.busy.set(false);
        this.load();
      },
      error: () => this.busy.set(false),
    });
  }

  reject(e: Expense): void {
    const comment = window.prompt('Reason for rejection?');
    if (!comment) {
      return;
    }
    this.busy.set(true);
    this.svc.reject(e.id, comment).subscribe({
      next: () => {
        this.message.set('Expense rejected.');
        this.busy.set(false);
        this.load();
      },
      error: () => this.busy.set(false),
    });
  }

  private load(): void {
    this.svc.pending().subscribe((x) => this.pending.set(x));
  }
}
