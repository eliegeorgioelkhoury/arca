import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Role, TokenResponse, User } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly base = environment.apiBaseUrl;

  private readonly TOKEN_KEY = 'arca_token';
  private readonly USER_KEY = 'arca_user';

  readonly user = signal<User | null>(this.loadUser());
  readonly isAuthenticated = computed(() => this.user() !== null);

  token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  login(email: string, password: string) {
    return this.http
      .post<TokenResponse>(`${this.base}/api/auth/login`, { email, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  demoLogin(role: Role) {
    return this.http
      .post<TokenResponse>(`${this.base}/api/auth/demo/${role}`, {})
      .pipe(tap((res) => this.setSession(res)));
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.user.set(null);
    this.router.navigate(['/login']);
  }

  hasRole(...roles: Role[]): boolean {
    const u = this.user();
    return u !== null && roles.includes(u.role);
  }

  private setSession(res: TokenResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res.user));
    this.user.set(res.user);
  }

  private loadUser(): User | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  }
}
