export type Role = 'EMPLOYEE' | 'MANAGER' | 'ADMIN';
export type ExpenseStatus = 'SUBMITTED' | 'APPROVED' | 'REJECTED';
export type ExpenseCategory = 'TRAVEL' | 'MEALS' | 'SOFTWARE' | 'EQUIPMENT' | 'OFFICE' | 'OTHER';

export const CATEGORIES: ExpenseCategory[] = ['TRAVEL', 'MEALS', 'SOFTWARE', 'EQUIPMENT', 'OFFICE', 'OTHER'];

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: Role;
  teamName?: string | null;
}

export interface TokenResponse {
  token: string;
  user: User;
}

export interface Expense {
  id: number;
  amount: number;
  currency: string;
  category: ExpenseCategory;
  status: ExpenseStatus;
  description?: string;
  merchant?: string;
  spentOn: string;
  submitterId: number;
  submitterName: string;
  teamName?: string | null;
  submittedAt: string;
  decidedAt?: string | null;
  decidedByName?: string | null;
  decisionComment?: string | null;
  journalEntryId?: number | null;
  receiptCount: number;
}

export interface CreateExpense {
  amount: number;
  category: ExpenseCategory;
  currency?: string;
  description?: string;
  merchant?: string;
  spentOn: string;
}

export interface TrialBalanceRow {
  accountCode: string;
  accountName: string;
  type: string;
  normalSide: string;
  debit: number;
  credit: number;
  balance: number;
}

export interface TrialBalance {
  rows: TrialBalanceRow[];
  totalDebit: number;
  totalCredit: number;
  balance: number;
}

export interface JournalLine {
  id: number;
  accountCode: string;
  accountName: string;
  side: 'DEBIT' | 'CREDIT';
  amount: number;
}

export interface JournalEntry {
  id: number;
  entryDate: string;
  memo: string;
  sourceType: string;
  sourceId: number;
  lines: JournalLine[];
  totalDebit: number;
  totalCredit: number;
}

export interface CategorySpend {
  category: string;
  total: number;
  count: number;
}

export interface MonthlySpend {
  month: string;
  total: number;
  count: number;
}

export interface TeamSpend {
  team: string;
  total: number;
  count: number;
}
