# ARCA

[![CI](https://github.com/eliegeorgioelkhoury/arca/actions/workflows/ci.yml/badge.svg)](https://github.com/eliegeorgioelkhoury/arca/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> B2B expense and reimbursement built on a **real double-entry ledger**. Every approved expense posts one balanced debit/credit pair, and the trial balance always sums to zero.

**Status:** 🚧 Scaffolding (GATE 1). See [STATE.md](STATE.md) · [ROADMAP.md](ROADMAP.md).

<!-- TODO: replace with a looping capture of submit → approve → ledger balancing; drop the file at docs/demo.gif -->
![ARCA demo — coming soon](https://placehold.co/1200x600/0f766e/f8fafc?text=ARCA+demo+GIF+coming+soon)

## Screenshots
<!-- TODO: add real captures under docs/ and swap these placeholders -->
| Submit expense | Manager approval | Admin analytics | Trial balance |
|---|---|---|---|
| ![Submit](https://placehold.co/300x200?text=Submit) | ![Approve](https://placehold.co/300x200?text=Approve) | ![Analytics](https://placehold.co/300x200?text=Analytics) | ![Trial+balance](https://placehold.co/300x200?text=Trial+balance) |

## Features
- Submit expenses with receipt upload (**EMPLOYEE**).
- Approve / reject with comments; each approval posts a balanced journal entry (**MANAGER**).
- Admin analytics — spend by category / month / team, CSV export, trial-balance panel (**ADMIN**).
- JWT auth with role-based route guards and API role checks.
- Double-entry invariant enforced in the database and covered by tests (a journal entry must sum to zero).
- "Sign in as demo" for all three roles; idempotent seed and a reset path so demo state can't be wrecked.

## Stack
- **Frontend:** Angular · TypeScript · RxJS · JWT HTTP interceptor · role route guards
- **Backend:** Java · Spring Boot (layered controller/service/repository) · Bean Validation · global exception handler · OpenAPI · DB-free `/health`
- **Database:** PostgreSQL (Neon) with Liquibase migrations
- **Files:** Supabase Storage (receipts)
- **CI:** GitHub Actions — build, lint, test

## Architecture
```
Angular (Vercel) ──HTTPS──▶ Spring Boot API (Render · Docker) ──▶ PostgreSQL (Neon)
                                    │
                                    └──▶ Supabase Storage (receipts)
```
- **Double-entry core:** `accounts`, `journal_entries`, `journal_lines` with a per-entry sum-to-zero constraint. Approving an expense posts one balanced entry — a debit and an equal credit.
- **Warm API, sleeping DB:** `/health` returns 200 without touching the database, so UptimeRobot keeps the API warm while Neon scales to zero. First real query after idle resumes Neon in ~1–2s.
- Milestone-by-milestone breakdown in [ROADMAP.md](ROADMAP.md).

## Run locally
> Not scaffolded yet — GATE 1 is repo hygiene + CI. These are the intended steps; they firm up in milestones 2 and 4.

```bash
# 1. Clone
git clone git@github.com:eliegeorgioelkhoury/arca.git && cd arca

# 2. Configure environment
cp .env.example .env        # fill in local values

# 3. Backend (Spring Boot) — http://localhost:8080, Swagger at /swagger-ui
cd backend && ./mvnw spring-boot:run

# 4. Frontend (Angular) — http://localhost:4200
cd ../frontend && npm ci && npm start
```

## Project layout
```
arca/
├── frontend/   # Angular app                    (milestone 4)
├── backend/    # Spring Boot API + Liquibase     (milestones 1–3)
├── infra/      # Dockerfile, deploy config       (milestone 6)
└── docs/       # demo GIF, screenshots, arch notes
```

## License
[MIT](LICENSE) © 2026 Elie El Khoury
