# ARCA — STATE

**Status:** not started
**Next:** milestone 1 — Schema + Liquibase (double-entry tables, per-entry sum-to-zero constraint, seed demo roles)

_Living doc. Update after each milestone: what shipped, what's in flight, what's blocked._

## Now
- Repo scaffolded at GATE 1 — license, `.gitignore`, README, CI skeleton, `CLAUDE.md`, `.env.example`, living docs. No app code yet.

## Next
- **Milestone 1:** `users`, `teams`, `expenses`, `receipts`, `accounts`, `journal_entries`, `journal_lines` with a per-entry sum-to-zero constraint; seed EMPLOYEE / MANAGER / ADMIN demo accounts.

## Done
- GATE 1 — repository hygiene, CI, and living docs.

## Blocked
- _none_

## Deploy
- **Homepage:** TODO — set to the Vercel URL once the frontend is live (milestone 6).
- Frontend → Vercel · Backend → Render (Docker) + UptimeRobot · DB → Neon · Receipts → Supabase Storage.
