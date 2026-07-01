import { defineConfig, devices } from '@playwright/test';

/**
 * E2E config. Boots the full stack (Docker-free):
 *  - backend via `spring-boot:test-run` (embedded Postgres, seeded demo data)
 *  - frontend via `ng serve`
 * Both reuse an already-running server if present (local dev); CI starts them fresh.
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 45_000,
  expect: { timeout: 10_000 },
  fullyParallel: false,
  workers: 1,
  reporter: process.env['CI'] ? 'list' : 'html',
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: [
    {
      command: 'bash -c "cd ../backend && ./mvnw -q spring-boot:test-run"',
      url: 'http://localhost:8080/health',
      reuseExistingServer: true,
      timeout: 180_000,
    },
    {
      command: 'npm start',
      url: 'http://localhost:4200',
      reuseExistingServer: true,
      timeout: 120_000,
    },
  ],
});
