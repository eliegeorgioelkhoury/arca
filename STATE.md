# ARCA — STATE

**Status:** in progress — milestones 1–5 built and verified locally; **paused before deploy** (milestone 6)
**Next:** milestone 6 — Deploy (awaiting the user's own Vercel / Render / Neon / Supabase accounts and secrets)

_Living doc. Update after each milestone: what shipped, what's in flight, what's blocked._

## Now
- Full stack runs locally, Docker-free: `cd backend && ./mvnw spring-boot:test-run` (embedded Postgres + seeded demo data on :8080) and `cd frontend && npm start` (:4200).
- CI is green on real tests: backend `./mvnw verify` runs the sum-to-zero constraint test on real Postgres (zonky); a Playwright `e2e` job drives submit → approve → balanced trial balance.

## Done
- **GATE 1** — repository hygiene, CI, living docs.
- **M1** — Liquibase schema (`users`, `teams`, `expenses`, `receipts`, `accounts`, `journal_entries`, `journal_lines`) with a DB-level, **deferred per-entry sum-to-zero constraint trigger**; seeded chart of accounts, teams, and EMPLOYEE / MANAGER / ADMIN demo accounts.
- **M2** — Spring Boot API: layered controller/service/repository, DTOs + Bean Validation, JWT auth with role checks, global exception handler, OpenAPI, **DB-free `/health`**. Balanced double-entry posting on every approval (Dr category expense / Cr Accounts Payable). Analytics (by category / month / team) + CSV export; trial balance; idempotent seed + `/api/demo/reset` path.
- **M3** — JUnit + Failsafe integration tests on real Postgres: the sum-to-zero invariant (balanced commits; unbalanced rejected; removing one side rejected), balanced-posting + trial-balance-zero, and API/role smoke tests. Green locally and in CI — **run, not skipped**.
- **M4** — Angular 19 app: JWT HTTP interceptor + role route guards, RxJS/signals state. Screens: submit expense (+ receipt upload), manager approve/reject with comment, admin analytics + CSV export, trial-balance panel. One-click "Sign in as demo" for all three roles.
- **M5 (baseline)** — signature motion: credit-teal / debit-coral duality, count-up on load, trial balance settles to zero; `prefers-reduced-motion` honored and visible keyboard focus throughout.
- **Playwright** (from M7) — happy-path E2E across all three roles: submit → approve → balanced ledger. Green locally and in the CI `e2e` job.

## Next
- **Milestone 6 — Deploy.** Angular → Vercel; Spring Boot → Render (Docker, `infra/Dockerfile`) + UptimeRobot on `/health`; Postgres → Neon; receipts → Supabase Storage. Verify no cold start.

## Blocked
- Deploy is intentionally **paused for review** (GATE 2 stop). No blockers on the build itself; it needs the user's own hosting accounts and secrets.

## Deploy notes
- **Homepage:** TODO — set to the Vercel URL once the frontend is live (milestone 6).
- `.env.example` documents required env; real secrets live only in host dashboards / GitHub Actions secrets.
- Receipt storage is a swappable interface (`ReceiptStorageService`): local filesystem in dev, Supabase Storage at deploy.
