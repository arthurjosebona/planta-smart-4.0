# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Web frontend for **Bancada Smart 4.0** — a React + TypeScript SPA for supervising and operating an industrial bench (estações), managing orders (pedidos), inventory (estoque), and shipping (expedição) in real time, including 3D visualization with Three.js. Requires **Node 22.x**. The backend is expected at `http://localhost:8080`.

## Commands

```bash
npm run dev          # Vite dev server with --host (exposes on LAN); default http://localhost:5173
npm run build        # Production build to dist/
npm run preview       # Preview the production build
npm run lint         # ESLint over the repo
npm run format       # Prettier --write .
npm run format:check # Prettier --check .
```

There is **no test framework configured** — do not assume one exists.

## Architecture

Layered Architecture + MVVM in the presentation layer. Dependencies flow inward: `presentation → service → infrastructure → domain`. The domain layer has no external/framework dependencies.

- **`src/domain/`** — business core. `entities/` (Pedido, Estoque, Bloco, Lamina, Expedicao, ModuloIP), `enums/` (mirror backend enums), `repositories/` (interfaces / contracts), `valueObjects/`, `error/` (ApiError, HttpError).
- **`src/infrastructure/`** — concrete implementations and API integration. `http/HttpClient.ts` (fetch wrapper, base URL hardcoded to `http://localhost:8080`, throws `HttpError` on non-OK), `dtos/request|response/`, `mappers/` (DTO ↔ domain entity, both directions), `repositories/` (implement the domain interfaces).
- **`src/service/`** — application services orchestrating use cases (PedidoService, EstoqueService, ExpedicaoService, ConexaoService, CacheService).
- **`src/presentation/`** — UI. `components/` follow **Atomic Design** (`atoms/`, `molecules/`, `organisms/`, `template/`); `pages/` follow MVVM.

### MVVM page pattern

Each page directory contains three files (e.g. `Pedidos/`):
- **`<Page>Model.ts`** — the view state type plus an `<Page>ModelInitial` default.
- **`use<Page>ViewModel.ts`** — a hook holding the Model in `useState`, calling services (pulled from the DI container), and handling load/error. State updates use functional `setModel((s) => ({ ...s, ... }))`. Errors are narrowed with `instanceof HttpError`.
- **`<Page>View.tsx`** — presentational, consumes the view model hook.

### Dependency injection

`src/config/diContainer.ts` instantiates singletons (httpClient → repositories → services) and exports them. Pages import these instances directly (e.g. `import { pedidoService } from '@config/diContainer'`) rather than constructing their own.

### Routing

`App.tsx` maps over `src/presentation/router/routes` inside a `BrowserRouter`. Add pages by registering a route there.

## Conventions

- **Path aliases** (defined in `vite.config.js`, kept in sync with `tsconfig.json`): `@entities`, `@enums`, `@repositories`, `@valueObjects`, `@error`, `@components`, `@pages`, `@router`, `@utils`, `@service`, `@config`, `@http`, `@dtos`, `@repositoriesImp`, `@styles`, `@assets`. Use them instead of relative paths.
- Domain/business terms are in **Portuguese** (Pedido, Estoque, Bloco, Lamina, Expedicao, Estações); match this when adding entities/fields.
- Styling is **Tailwind CSS v4** (via `@tailwindcss/vite`), with `clsx` for conditional classes.
- 3D views use `three` + `@react-three/fiber` + `@react-three/drei`.
- To point at a different backend, change the `baseURL` in `infrastructure/http/HttpClient.ts`.
