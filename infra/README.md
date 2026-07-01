# infra

Infrastructure-as-code for ARCA.

| File | Purpose |
|---|---|
| `Dockerfile` | Multi-stage build of the Spring Boot backend, tuned for a 512 MB instance (`-XX:MaxRAMPercentage=70`, lazy init). Used by the Render deploy (milestone 6). Build from the repo root: `docker build -f infra/Dockerfile -t arca-backend .` |
| `docker-compose.yml` | One-command local stack (Postgres + backend) for development. `docker compose -f infra/docker-compose.yml up --build` |

## Notes
- **Tests need no Docker.** Backend tests use zonky embedded Postgres (a real Postgres binary), so `./mvnw verify` runs the full suite — including the ledger sum-to-zero constraint test — on any machine and in CI.
- **Production topology** (wired up in milestone 6, deploy): Angular → Vercel, Spring Boot → Render (this Dockerfile), Postgres → Neon, receipts → Supabase Storage. `/health` is DB-free so the API stays warm while Neon scales to zero.
