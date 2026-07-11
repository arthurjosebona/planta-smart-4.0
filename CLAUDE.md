# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Management system for the **Bancada Smart 4.0**, a didactic industrial production line with four chained stations: **Estoque** (stock) → **Processo** (process) → **Montagem** (assembly) → **Expedição** (shipping). The system reads/writes tags on **Siemens PLCs (CLPs)** in real time over the **S7 protocol** (ISO-on-TCP, port 102), implemented from scratch — no TIA Portal or third-party S7 library required.

Monorepo: `backend/planta-smart-4.0/` (Spring Boot) and `frontend/planta-smart-4.0/` (React). Note the nested directory: the actual projects live one level down from `backend/` and `frontend/`.

## Commands

Backend (run from `backend/planta-smart-4.0/`):
```bash
./mvnw spring-boot:run                 # run the API (http://localhost:8080)
./mvnw test                            # run all tests
./mvnw test -Dtest=ClassName#method    # run a single test
./mvnw clean package -DskipTests       # build JAR -> target/appsa-0.0.1-SNAPSHOT.jar
```

Frontend (run from `frontend/planta-smart-4.0/`):
```bash
npm install
npm run dev            # Vite dev server (--host, exposed on LAN)
npm run build
npm run lint           # eslint
npm run format         # prettier --write
```

Full stack (DB + backend + frontend) via Docker Compose from repo root: `docker compose up` — see `DOCKER.md`.

## Configuration

- **Backend DB credentials are NOT in `application.properties`** — they come from a `.env` file in `backend/planta-smart-4.0/` (loaded by `spring-dotenv`): `DB_USER`, `DB_PASSWORD`, optional `PORT` (default 8080). DB is `db_sa_smart40` on MySQL. `spring.jpa.hibernate.ddl-auto=update`, but `src/main/resources/script.sql` (which DROPs and recreates the schema) is the source of truth for initial state.
- **PLC IPs are hardcoded in `application.properties`** under `clp.ips.*` (estoque/processo/montagem/expedicao) plus `clp.endpoints.esp32-tampas.*`. Adjust these to match the local network topology when the PLCs aren't reachable at the committed IPs.
- **Frontend API base URL** comes from `VITE_BASE_URL` (`import.meta.env`) in `HttpClient.ts` — set it in a frontend `.env`. There is no Vite dev proxy.
- API docs (springdoc-openapi) at `/swagger-ui.html` when running.

## Architecture

### Backend (`com.smart.appsa`, Java 17, Spring Boot 4.0.5)
Layered: `controller/` (REST) → `service/` (business rules) → `repository/` (Spring Data JPA) → `model/` (JPA entities). `dto/request` + `dto/response` cross the HTTP boundary, converted by `mapper/`. Domain exceptions live under `exception/core` and `exception/` with centralized handling in `exception/handler/GlobalExceptionHandler`.

**`clpcomm/` is the critical module** — the hand-rolled S7 protocol layer:
- `S7ProtocolClient` — S7 over TCP/IP.
- `PlcConnector` — connect/read/write/disconnect abstraction.
- `PlcConnectionService` — manages concurrent, long-lived connections with per-station reader threads.

The frontend receives live PLC readings via **SSE** (`service/clp/SseService`, exposed through `SmartController` at `/api/smart`). `DataInitializer` (config) seeds initial data at startup.

Entities: `Pedido`, `Estoque`, `Expedicao`, `Bloco`, `Lamina` (tables `T_SA_*`). A `Bloco` has multiple `Lamina`s; `Estoque` positions hold colored blocks; `Expedicao` slots link to `Pedido`s.

REST roots: `/api/pedidos`, `/api/estoque`, `/api/expedicao`, `/api/config/clp`, `/api/smart`. Starting production (`PUT /api/pedidos/start-production/{id}`) writes to the PLC.

### Frontend (React 19 + TS, Vite 8, Tailwind 4, Three.js/R3F)
Clean-architecture layering with path aliases (defined in `vite.config.js`):
- `domain/` — `entities`, `enums`, `valueObjects`, `repositories` (interfaces), `error`.
- `infrastructure/` — `dtos`, `http/HttpClient`, `mappers`, `repositories` (concrete impls of the domain interfaces).
- `service/` — application services that depend on repository interfaces.
- `presentation/` — `components`, `pages`, `utils`; routing in `router/`.
- `config/diContainer.ts` — **manual dependency injection**: wires `HttpClient` → repositories → services as singletons. Import services from here rather than instantiating them.

Three.js / React Three Fiber powers the 3D visualization of the bench and blocks (`assets/bloco`, `assets/laminas`).

Use the `@`-aliases (`@service`, `@http`, `@entities`, `@components`, etc.) for imports — do not use deep relative paths.

## Conventions

- Commit messages use Conventional Commits with scopes, often `feat(backend)`, `refactor`, `wip(backend)`.
- Domain vocabulary is Portuguese (pedido, estoque, expedição, bloco, lâmina, montagem); keep new domain names consistent with it.
