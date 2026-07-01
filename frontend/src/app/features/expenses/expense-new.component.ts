import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ExpenseService } from '../../core/expense.service';
import { CATEGORIES, CreateExpense } from '../../core/models';

@Component({
  selector: 'app-expense-new',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="spread">
      <h1>New expense</h1>
      <a class="btn secondary" routerLink="/expenses">Cancel</a>
    </div>
    <div class="card reveal" style="max-width: 560px; margin-top: 12px">
      <form (ngSubmit)="submit()" #f="ngForm">
        <div class="field">
          <label for="amount">Amount (USD)</label>
          <input id="amount" name="amount" type="number" step="0.01" min="0.01"
                 [(ngModel)]="model.amount" required data-testid="amount" />
        </div>
        <div class="field">
          <label for="category">Category</label>
          <select id="category" name="category" [(ngModel)]="model.category" required data-testid="category">
            @for (c of categories; track c) {
              <option [value]="c">{{ c }}</option>
            }
          </select>
        </div>
        <div class="field">
          <label for="merchant">Merchant</label>
          <input id="merchant" name="merchant" [(ngModel)]="model.merchant" data-testid="merchant" />
        </div>
        <div class="field">
          <label for="description">Description</label>
          <textarea id="description" name="description" rows="2" [(ngModel)]="model.description" data-testid="description"></textarea>
        </div>
        <div class="field">
          <label for="spentOn">Date</label>
          <input id="spentOn" name="spentOn" type="date" [(ngModel)]="model.spentOn" required data-testid="spentOn" />
        </div>
        <div class="field">
          <label for="receipt">Receipt (optional)</label>
          <input id="receipt" name="receipt" type="file" (change)="onFile($event)" data-testid="receipt" />
        </div>
        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
        <button class="btn credit" type="submit" [disabled]="submitting() || f.invalid" data-testid="submit-expense">
          Submit expense
        </button>
      </form>
    </div>
  `,
})
export class ExpenseNewComponent {
  private svc = inject(ExpenseService);
  private router = inject(Router);

  categories = CATEGORIES;
  model: CreateExpense = {
    amount: 0,
    category: 'TRAVEL',
    currency: 'USD',
    merchant: '',
    description: '',
    spentOn: new Date().toISOString().slice(0, 10),
  };
  private file: File | null = null;
  submitting = signal(false);
  error = signal<string | null>(null);

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.file = input.files?.[0] ?? null;
  }

  submit(): void {
    if (!this.model.amount || this.model.amount <= 0) {
      this.error.set('Enter a positive amount.');
      return;
    }
    this.submitting.set(true);
    this.error.set(null);
    this.svc.create(this.model).subscribe({
      next: (expense) => {
        if (this.file) {
          this.svc.uploadReceipt(expense.id, this.file).subscribe({ next: () => this.done(), error: () => this.done() });
        } else {
          this.done();
        }
      },
      error: () => {
        this.submitting.set(false);
        this.error.set('Could not submit expense.');
      },
    });
  }

  private done(): void {
    this.submitting.set(false);
    this.router.navigateByUrl('/expenses');
  }
}
