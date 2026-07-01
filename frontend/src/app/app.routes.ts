import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'expenses' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'expenses',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/expenses/expense-list.component').then((m) => m.ExpenseListComponent),
  },
  {
    path: 'expenses/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/expenses/expense-new.component').then((m) => m.ExpenseNewComponent),
  },
  {
    path: 'approvals',
    canActivate: [roleGuard('MANAGER', 'ADMIN')],
    loadComponent: () =>
      import('./features/approvals/approvals.component').then((m) => m.ApprovalsComponent),
  },
  {
    path: 'admin',
    canActivate: [roleGuard('ADMIN')],
    loadComponent: () => import('./features/admin/admin.component').then((m) => m.AdminComponent),
  },
  { path: '**', redirectTo: 'expenses' },
];
