import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { JournalEntry, TrialBalance } from './models';

@Injectable({ providedIn: 'root' })
export class LedgerService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/ledger`;

  trialBalance() {
    return this.http.get<TrialBalance>(`${this.base}/trial-balance`);
  }

  entry(id: number) {
    return this.http.get<JournalEntry>(`${this.base}/entries/${id}`);
  }
}
