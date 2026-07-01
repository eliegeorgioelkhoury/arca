import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { CategorySpend, MonthlySpend, TeamSpend } from './models';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/analytics`;

  byCategory() {
    return this.http.get<CategorySpend[]>(`${this.base}/by-category`);
  }

  byMonth() {
    return this.http.get<MonthlySpend[]>(`${this.base}/by-month`);
  }

  byTeam() {
    return this.http.get<TeamSpend[]>(`${this.base}/by-team`);
  }

  exportCsv() {
    return this.http.get(`${this.base}/export`, { responseType: 'text' });
  }
}
