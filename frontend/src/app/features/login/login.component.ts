import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { Role } from '../../core/models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="login-wrap">
      <div class="card login reveal">
        <h1>ARCA</h1>
        <p class="muted">B2B expense &amp; reimbursement on a real double-entry ledger.</p>

        <p class="muted small label">One-click demo</p>
        <div class="demo-btns">
          <button class="btn credit" (click)="demo('EMPLOYEE')" data-testid="demo-EMPLOYEE" [disabled]="loading()">
            Employee
          </button>
          <button class="btn credit" (click)="demo('MANAGER')" data-testid="demo-MANAGER" [disabled]="loading()">
            Manager
          </button>
          <button class="btn credit" (click)="demo('ADMIN')" data-testid="demo-ADMIN" [disabled]="loading()">
            Admin
          </button>
        </div>

        <div class="divider"><span>or sign in</span></div>

        <form (ngSubmit)="submit()" #f="ngForm">
          <div class="field">
            <label for="email">Email</label>
            <input id="email" name="email" type="email" [(ngModel)]="email" required data-testid="email" />
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input id="password" name="password" type="password" [(ngModel)]="password" required data-testid="password" />
          </div>
          <button class="btn" type="submit" [disabled]="loading() || f.invalid" data-testid="login">Sign in</button>
        </form>

        @if (error()) {
          <p class="error" data-testid="login-error">{{ error() }}</p>
        }
        <p class="muted small hint">Demo password for all seeded accounts: <code>demo1234</code></p>
      </div>
    </div>
  `,
  styles: [
    `
      .login-wrap {
        display: flex;
        justify-content: center;
        padding-top: 40px;
      }
      .login {
        width: 100%;
        max-width: 420px;
      }
      .label {
        margin-top: 16px;
        margin-bottom: 8px;
      }
      .demo-btns {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 10px;
      }
      .divider {
        display: flex;
        align-items: center;
        gap: 12px;
        margin: 20px 0;
        color: var(--muted);
        font-size: 12px;
      }
      .divider::before,
      .divider::after {
        content: '';
        flex: 1;
        height: 1px;
        background: var(--border);
      }
      .hint {
        margin-top: 14px;
      }
      code {
        background: #eef3f4;
        padding: 1px 6px;
        border-radius: 6px;
      }
    `,
  ],
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  loading = signal(false);
  error = signal<string | null>(null);

  demo(role: Role): void {
    this.loading.set(true);
    this.error.set(null);
    this.auth.demoLogin(role).subscribe({
      next: () => this.go(role),
      error: () => this.fail(),
    });
  }

  submit(): void {
    if (!this.email || !this.password) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.go(this.auth.user()!.role),
      error: () => this.fail(),
    });
  }

  private go(role: Role): void {
    this.loading.set(false);
    const target = role === 'ADMIN' ? '/admin' : role === 'MANAGER' ? '/approvals' : '/expenses';
    this.router.navigateByUrl(target);
  }

  private fail(): void {
    this.loading.set(false);
    this.error.set('Sign in failed. Check your credentials.');
  }
}
