import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { CreateExpense, Expense, ExpenseStatus } from './models';

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/expenses`;

  list(status?: ExpenseStatus) {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<Expense[]>(this.base, { params });
  }

  pending() {
    return this.http.get<Expense[]>(`${this.base}/pending`);
  }

  get(id: number) {
    return this.http.get<Expense>(`${this.base}/${id}`);
  }

  create(expense: CreateExpense) {
    return this.http.post<Expense>(this.base, expense);
  }

  uploadReceipt(id: number, file: File) {
    const form = new FormData();
    form.append('file', file);
    return this.http.post(`${this.base}/${id}/receipt`, form);
  }

  approve(id: number, comment?: string) {
    return this.http.post<Expense>(`${this.base}/${id}/approve`, { comment });
  }

  reject(id: number, comment: string) {
    return this.http.post<Expense>(`${this.base}/${id}/reject`, { comment });
  }
}
