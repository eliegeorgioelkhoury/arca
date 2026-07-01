# ARCA — ROADMAP

Angular + Spring Boot, double-entry ledger. Seeded from the portfolio build plan. One project at a time; nothing deploys without review at the gate.

- [x] **1 — Schema + Liquibase.** `users`, `teams`, `expenses`, `receipts`, `accounts`, `journal_entries`, `journal_lines` with a per-entry sum-to-zero constraint. Seed EMPLOYEE / MANAGER / ADMIN demo accounts. _(Deferred constraint trigger; seeded chart of accounts + teams.)_
- [x] **2 — Spring Boot API.** Layered controller/service/repository, DTOs + Bean Validation, JWT auth with role checks, global exception handler, OpenAPI, DB-free `/health`. Balanced double-entry posting on every approval.
- [x] **3 — Tests.** JUnit ledger sum-to-zero invariant test (fails on an unbalanced entry). CI green. _(Real Postgres via zonky; also balanced-posting + API/role tests.)_
- [x] **4 — Angular.** Feature structure, RxJS/signals state, JWT HTTP interceptor, role route guards. Screens: submit expense with receipt upload; manager approve/reject with comment; admin analytics (spend by category, month, team) with CSV export; trial-balance panel.
- [~] **5 — Signature motion.** **Baseline done:** credit-teal / debit-coral duality, numbers count up on load, trial balance settles to zero, reduced-motion honored. _Deferred to post-deploy polish:_ charts draw-in and the approval slide-a-debit-and-credit-into-balance choreography.
- [ ] **6 — Deploy.** Angular → Vercel; Spring Boot → Render (Docker) + UptimeRobot; Neon; receipts → Supabase Storage. Verify no cold start. **← paused here (GATE 2 stop); awaiting the user's Vercel / Render / Neon / Supabase accounts and secrets.**
- [ ] **7 — Demo + polish.** **Done:** "Sign in as demo" for all three roles, reset path, Playwright happy-path test. **Remaining (after deploy):** README GIF + screenshots + arch note, homepage, pin. _(Topics already set.)_
