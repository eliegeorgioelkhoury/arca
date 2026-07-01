import { expect, test } from '@playwright/test';

/**
 * The ARCA happy path across all three roles:
 * employee submits an expense -> manager approves it (posting a balanced journal
 * entry) -> admin sees the trial balance still net to zero.
 */
test('submit -> approve -> ledger stays balanced', async ({ page }) => {
  const merchant = `Playwright ${Date.now()}`;
  const amount = '137.75';

  // 1) Demo sign-in as Employee
  await page.goto('/login');
  await page.getByTestId('demo-EMPLOYEE').click();
  await expect(page).toHaveURL(/\/expenses$/);

  // 2) Submit a new expense
  await page.getByTestId('new-expense').click();
  await expect(page).toHaveURL(/\/expenses\/new$/);
  await page.getByTestId('amount').fill(amount);
  await page.getByTestId('category').selectOption('SOFTWARE');
  await page.getByTestId('merchant').fill(merchant);
  await page.getByTestId('description').fill('End-to-end submitted expense');
  await page.getByTestId('submit-expense').click();

  // Back on the list, it shows as SUBMITTED
  await expect(page).toHaveURL(/\/expenses$/);
  const listRow = page.getByTestId('expense-row').filter({ hasText: merchant }).first();
  await expect(listRow).toBeVisible();
  await expect(listRow.getByTestId('status')).toHaveText('SUBMITTED');

  // 3) Switch to the Manager and approve it
  await page.getByTestId('logout').click();
  await page.getByTestId('demo-MANAGER').click();
  await expect(page).toHaveURL(/\/approvals$/);

  const pendingRow = page.getByTestId('pending-row').filter({ hasText: merchant }).first();
  await expect(pendingRow).toBeVisible();
  const id = await pendingRow.getAttribute('data-id');
  await page.getByTestId(`approve-${id}`).click();

  // The approval reports the balanced posting
  await expect(page.getByTestId('approve-message')).toContainText('balanced journal entry');

  // 4) Switch to the Admin: the trial balance nets to zero
  await page.getByTestId('logout').click();
  await page.getByTestId('demo-ADMIN').click();
  await expect(page).toHaveURL(/\/admin$/);
  await expect(page.getByTestId('balanced-badge')).toBeVisible();
  await expect(page.getByTestId('trial-balance-value')).toHaveText('$0.00');
});
