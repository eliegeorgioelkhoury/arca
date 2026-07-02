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
            <span class="role-chip" data-testid="role-chip">{{ auth.user()!.role }}</span>
            <span class="who">{{ auth.user()!.fullName }}</span>
            <button class="btn signout" (click)="auth.logout()" data-testid="logout">Sign out</button>
          </div>
        }
      </div>
    </header>
    <main class="container"><router-outlet /></main>
  `,
  styles: [
    `
      .nav {
        background: var(--midnight);
        position: sticky;
        top: 0;
        z-index: 10;
        box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.06);
      }
      .nav-inner {
        display: flex;
        align-items: center;
        gap: var(--space-5);
        padding-top: var(--space-3);
        padding-bottom: var(--space-3);
      }
      .brand {
        font-family: var(--font-display);
        font-weight: 700;
        font-size: 18px;
        letter-spacing: 0.04em;
        color: #fff;
      }
      .brand:hover {
        color: #fff;
        text-decoration: none;
      }
      .brand-sub {
        color: var(--copper-bright);
        font-weight: 600;
      }
      .links {
        display: flex;
        gap: var(--space-5);
        margin-left: var(--space-2);
      }
      .links a {
        color: rgba(244, 241, 234, 0.68);
        font-weight: 500;
        font-size: 14px;
        padding: 6px 0;
        border-bottom: 2px solid transparent;
        transition: color 0.15s ease, border-color 0.15s ease;
      }
      .links a:hover {
        color: #fff;
        text-decoration: none;
      }
      .links a.active {
        color: #fff;
        border-bottom-color: var(--copper-bright);
      }
      .user {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: var(--space-3);
      }
      .role-chip {
        font: 600 11px/1 var(--font-body);
        letter-spacing: 0.06em;
        color: var(--copper-bright);
        background: rgba(200, 131, 75, 0.14);
        border: 1px solid rgba(200, 131, 75, 0.38);
        padding: 4px 9px;
        border-radius: 999px;
      }
      .who {
        color: rgba(244, 241, 234, 0.6);
        font-size: 13px;
      }
      @media (max-width: 640px) {
        .who {
          display: none;
        }
      }
      .btn.signout {
        --btn-bg: transparent;
        --btn-fg: rgba(244, 241, 234, 0.85);
        min-height: 34px;
        padding: 6px 12px;
        border-color: rgba(255, 255, 255, 0.22);
      }
      .btn.signout:hover {
        background: rgba(255, 255, 255, 0.08);
      }
      .nav a:focus-visible,
      .nav .btn:focus-visible {
        outline: 2px solid var(--copper-bright);
        outline-offset: 3px;
      }
    `,
  ],
})
export class AppComponent {
  auth = inject(AuthService);
}
