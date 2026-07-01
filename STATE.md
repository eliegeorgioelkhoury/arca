# ARCA — STATE

**Status:** in progress — milestones 1–5 built (Spring Boot 4.1); **milestone 6 deploy config prepared — not yet deployed**
**Next:** wire the hosting accounts/secrets and deploy (Vercel · Render · Neon · Supabase)

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
- **M6 (config, not deployed)** — `prod` Spring profile reads Neon from env + runs Liquibase on boot; `SupabaseReceiptStorageService` (`@Profile("prod")`) uploads to the private `receipts` bucket and returns signed URLs (Local filesystem stays for dev); Angular reads its API base URL from `API_BASE_URL` at build time; Dockerfile caps the heap via `JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=70`; a CI `image` job confirms the backend image builds.

## Next
- **Milestone 6 — Deploy (do it).** Config is ready. Create the accounts + set secrets: Angular → Vercel (`API_BASE_URL`); Spring Boot → Render (Docker `infra/Dockerfile`, `SPRING_PROFILES_ACTIVE=prod`) + UptimeRobot on `/health`; Postgres → Neon; receipts → Supabase Storage (private `receipts` bucket). Verify no cold start; set each repo's homepage.

## Blocked
- Nothing blocks the code. Deploying needs the user's own hosting accounts + secrets (no real secrets are committed — see `.env.example` and `frontend/.env.example`).

## Deploy notes
- **Homepage:** TODO — set to the Vercel URL once the frontend is live (milestone 6).
- `.env.example` documents required env; real secrets live only in host dashboards / GitHub Actions secrets.
- Receipt storage is a swappable interface (`ReceiptStorageService`): local filesystem in dev, Supabase Storage at deploy.
