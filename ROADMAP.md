# ARCA — ROADMAP

Angular + Spring Boot, double-entry ledger. Seeded from the portfolio build plan. One project at a time; nothing deploys without review at the gate.

- [ ] **1 — Schema + Liquibase.** `users`, `teams`, `expenses`, `receipts`, `accounts`, `journal_entries`, `journal_lines` with a per-entry sum-to-zero constraint. Seed EMPLOYEE / MANAGER / ADMIN demo accounts.
- [ ] **2 — Spring Boot API.** Layered controller/service/repository, DTOs + Bean Validation, JWT auth with role checks, global exception handler, OpenAPI, DB-free `/health`. Balanced double-entry posting on every approval.
- [ ] **3 — Tests.** JUnit ledger sum-to-zero invariant test (fails on an unbalanced entry). CI green.
- [ ] **4 — Angular.** Feature modules, RxJS state, JWT HTTP interceptor, role route guards. Screens: submit expense with receipt upload; manager approve/reject with comment; admin analytics (spend by category, month, team) with CSV export; trial-balance panel.
- [ ] **5 — Signature motion.** Numbers count up on load, charts draw in, approval slides a debit and a credit into balance, credit-teal / debit-coral duality throughout, trial balance animates to zero. Honor reduced-motion.
- [ ] **6 — Deploy.** Angular → Vercel; Spring Boot → Render (Docker) + UptimeRobot; Neon; receipts → Supabase Storage. Verify no cold start.
- [ ] **7 — Demo + polish.** "Sign in as demo" for all three roles, reset path, Playwright happy-path test, README with GIF + screenshots + arch note, topics, homepage, pin.
