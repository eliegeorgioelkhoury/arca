import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, TitleCasePipe } from '@angular/common';
import { ExpenseService } from '../../core/expense.service';
import { Expense } from '../../core/models';
import { MoneyPipe } from '../../shared/money.pipe';

interface Posted {
  amount: number;
  currency: string;
  category: string;
  entryId: number | null;
}

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [DatePipe, TitleCasePipe, MoneyPipe],
  template: `
    <h1>Approvals</h1>
    <p class="muted">Pending expenses awaiting your decision.</p>

    @if (posted(); as p) {
      <div class="posting card" data-testid="posting">
        <div class="legs">
          <div class="leg leg-debit">
            <span class="leg-tag">Debit</span>
            <span class="leg-acct">{{ p.category | titlecase }} expense</span>
            <span class="leg-amt debit">+{{ p.amount | money: p.currency }}</span>
          </div>
          <div class="join">
            <span class="join-line"></span>
            <span class="join-eq">nets to $0.00</span>
            <span class="join-line"></span>
          </div>
          <div class="leg leg-credit">
            <span class="leg-tag">Credit</span>
            <span class="leg-acct">Accounts Payable</span>
            <span class="leg-amt credit">−{{ p.amount | money: p.currency }}</span>
          </div>
        </div>
        <p class="posting-msg" data-testid="approve-message">
          Posted balanced journal entry #{{ p.entryId }} — the debit and credit balance. ✓
        </p>
      </div>
    }
    @if (note()) {
      <p class="note muted" data-testid="reject-note">{{ note() }}</p>
    }

    <div class="card reveal queue">
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
              <td>{{ e.category | titlecase }}</td>
              <td>{{ e.merchant || '—' }}</td>
              <td class="num">{{ e.amount | money: e.currency }}</td>
              <td>
                <div class="row">
                  <button class="btn credit" (click)="approve(e)" [attr.data-testid]="'approve-' + e.id" [disabled]="busy()">
                    Approve
                  </button>
                  <button class="btn danger" (click)="reject(e)" [attr.data-testid]="'reject-' + e.id" [disabled]="busy()">
                    Reject
                  </button>
                </div>
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
  styles: [
    `
      .posting {
        margin: var(--space-4) 0;
        border-color: var(--copper);
        border-left: 3px solid var(--copper);
      }
      .legs {
        display: grid;
        grid-template-columns: 1fr auto 1fr;
        gap: var(--space-4);
        align-items: stretch;
      }
      @media (max-width: 640px) {
        .legs {
          grid-template-columns: 1fr;
        }
      }
      .leg {
        display: flex;
        flex-direction: column;
        gap: 3px;
        padding: var(--space-3) var(--space-4);
        border-radius: var(--radius-sm);
      }
      .leg-debit {
        background: var(--debit-soft);
        animation: slide-from-left 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
      }
      .leg-credit {
        background: var(--credit-soft);
        text-align: right;
        align-items: flex-end;
        animation: slide-from-right 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
      }
      .leg-tag {
        font: 600 10px/1 var(--font-body);
        text-transform: uppercase;
        letter-spacing: 0.08em;
        color: var(--muted);
      }
      .leg-acct {
        font-weight: 600;
        font-size: 14px;
      }
      .leg-amt {
        font-family: var(--font-mono);
        font-size: 20px;
        font-weight: 600;
        margin-top: 2px;
      }
      .join {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 6px;
        animation: pop-in 0.45s 0.35s both;
      }
      .join-line {
        width: 1px;
        flex: 1;
        min-height: 10px;
        background: var(--border-strong);
      }
      .join-eq {
        font: 600 12px/1 var(--font-mono);
        color: var(--midnight);
        white-space: nowrap;
      }
      .posting-msg {
        margin: var(--space-4) 0 0;
        font-weight: 600;
        color: var(--credit-text);
        animation: rise 0.5s 0.3s both;
      }
      .note {
        margin-top: var(--space-3);
        font-weight: 600;
      }
      .queue {
        margin-top: var(--space-4);
      }
    `,
  ],
})
export class ApprovalsComponent implements OnInit {
  private svc = inject(ExpenseService);

  pending = signal<Expense[]>([]);
  busy = signal(false);
  posted = signal<Posted | null>(null);
  note = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  approve(e: Expense): void {
    this.busy.set(true);
    this.note.set(null);
    this.svc.approve(e.id).subscribe({
      next: (x) => {
        this.posted.set({
          amount: x.amount,
          currency: x.currency,
          category: x.category,
          entryId: x.journalEntryId ?? null,
        });
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
    this.posted.set(null);
    this.svc.reject(e.id, comment).subscribe({
      next: () => {
        this.note.set('Expense rejected.');
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
