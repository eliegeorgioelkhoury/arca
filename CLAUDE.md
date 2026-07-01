# CLAUDE.md — ARCA

Guidance for Claude Code when working in this repository.

## What this is
ARCA is a B2B expense & reimbursement app built on a **real double-entry ledger**. The defining invariant: every approved expense posts exactly one balanced journal entry (debits equal credits), and the trial balance always sums to zero. Protect that invariant above all else.

## Stack
- **Frontend:** Angular + TypeScript + RxJS. Feature modules, JWT HTTP interceptor, role route guards.
- **Backend:** Java + Spring Boot. Layered controller → service → repository, DTOs + Bean Validation, JWT auth with role checks, global exception handler, OpenAPI. `/health` is DB-free and fast.
- **Database:** PostgreSQL (Neon) with Liquibase migrations.
- **Files:** Supabase Storage (receipts).

## Layout (target)
- `frontend/` — Angular app (milestone 4)
- `backend/` — Spring Boot API + Liquibase migrations (milestones 1–3)
- `infra/` — Dockerfile and deploy config (milestone 6)
- `docs/` — demo GIF, screenshots, architecture notes

> As of GATE 1, only repo hygiene, CI, and docs exist. App code arrives from milestone 1 onward.

## Conventions
- **Commits:** Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, `ci:`, `test:`, `refactor:`).
- **Secrets:** never commit real secrets. `.env.example` is the template; real values live in host dashboards and GitHub Actions secrets.
- **Ledger:** the per-entry sum-to-zero constraint is enforced in the DB and covered by a JUnit test. Never bypass it.
- **Accessibility & motion:** responsive to mobile, visible keyboard focus, honor `prefers-reduced-motion`.

## Commands (once scaffolded)
- Backend: `cd backend && ./mvnw spring-boot:run` · tests `./mvnw -B verify`
- Frontend: `cd frontend && npm ci && npm start` · lint `npm run lint` · test `npm test`

## Living docs
- Current status: [STATE.md](STATE.md)
- Plan: [ROADMAP.md](ROADMAP.md)
