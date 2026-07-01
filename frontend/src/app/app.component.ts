import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="nav">
      <div class="nav-inner container">
        <a class="brand" routerLink="/">ARCA <span class="brand-sub">ledger</span></a>
        @if (auth.isAuthenticated()) {
          <nav class="links">
            <a routerLink="/expenses" routerLinkActive="active">Expenses</a>
            <a routerLink="/expenses/new" routerLinkActive="active">New</a>
            @if (auth.hasRole('MANAGER', 'ADMIN')) {
              <a routerLink="/approvals" routerLinkActive="active" data-testid="nav-approvals">Approvals</a>
            }
            @if (auth.hasRole('ADMIN')) {
              <a routerLink="/admin" routerLinkActive="active" data-testid="nav-admin">Admin</a>
            }
          </nav>
          <div class="user">
            <span class="chip credit" data-testid="role-chip">{{ auth.user()!.role }}</span>
            <span class="muted small">{{ auth.user()!.fullName }}</span>
            <button class="btn secondary" (click)="auth.logout()" data-testid="logout">Sign out</button>
          </div>
        }
      </div>
    </header>
    <main class="container"><router-outlet /></main>
  `,
  styles: [
    `
      .nav {
        background: #fff;
        border-bottom: 1px solid var(--border);
        position: sticky;
        top: 0;
        z-index: 10;
      }
      .nav-inner {
        display: flex;
        align-items: center;
        gap: 20px;
        padding-top: 12px;
        padding-bottom: 12px;
      }
      .brand {
        font-weight: 800;
        font-size: 18px;
        color: var(--ink);
        letter-spacing: 0.02em;
      }
      .brand-sub {
        color: var(--credit-teal);
        font-weight: 700;
      }
      .links {
        display: flex;
        gap: 16px;
        margin-left: 8px;
      }
      .links a {
        color: var(--muted);
        font-weight: 600;
        padding: 6px 2px;
        border-bottom: 2px solid transparent;
      }
      .links a.active {
        color: var(--ink);
        border-bottom-color: var(--brand);
      }
      .user {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 12px;
      }
    `,
  ],
})
export class AppComponent {
  auth = inject(AuthService);
}
