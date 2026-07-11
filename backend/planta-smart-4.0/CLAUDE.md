# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean install

# Run (requires .env with DB_USER and DB_PASSWORD)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PedidoServiceTest

# Run a single test method
./mvnw test -Dtest=PedidoServiceTest#deveCriarPedidoComCamposValidos
```

## Environment Setup

Requires a `.env` file at the project root (loaded by `spring-dotenv`):

```
DB_USER=<mysql_user>
DB_PASSWORD=<mysql_password>
PORT=8080   # optional, defaults to 8080
```

Database: MySQL on `localhost:3306/db_sa_smart40`. Schema is auto-managed by JPA (`ddl-auto=update`).

## Architecture

This is a Spring Boot 4.x backend for a smart manufacturing plant. It has two main concerns:

### 1. Business domain (REST API)

Standard Spring layered architecture under `com.smart.appsa`. All REST endpoints are namespaced under `/api/...` (`/api/pedidos`, `/api/estoque`, `/api/expedicao`, `/api/config/clp`, `/api/smart`):
- **Controllers** (`controller/`): `PedidoController` (`/api/pedidos`), `EstoqueController` (`/api/estoque`), `ExpedicaoController` (`/api/expedicao`), `ClpConfigController` (`/api/config/clp`), `SmartController` (`/api/smart`)
- **Services**: `PedidoService`, `EstoqueService`, `ExpedicaoService`, `BlocoService`, `LaminaService` (all in `service/`)
- **Repositories**: Spring Data JPA, MySQL
- **DTOs**: `dto/request/` for inbound, `dto/response/` for outbound, `dto/clp/` for PLC payloads
- **Exceptions** (`exception/`): typed business exceptions handled by `GlobalExceptionHandler`

Core domain entities: `Pedido` → `Bloco` → `Lamina`, `Estoque`, `Expedicao`. A `Pedido` (production order) references blocks from stock and ships to an expedition slot.

### 2. PLC communication layer (`clpcomm` + `service/clp/`)

The app communicates directly with Siemens S7 PLCs over TCP port 102 using a custom `S7ProtocolClient`. Connection pooling is done by `PlcConnectionService` (one `PlcConnector` per IP).

**Reading loop** — uses the **Observer pattern**, orchestrated by `ClpReadingService` (`service/clp/`), not the controller:
- `POST /api/smart/start-readings` accepts a map of `{name → ip}` (keys: `estoque`, `processo`, `montagem`, `expedicao`) and delegates to `ClpReadingService`, which schedules one `scheduleWithFixedDelay` task per station (delay 400–600ms).
- `PlcReaderTask` (`service/clp/reader/`) is the **Subject**: it reads one or more `PlcReadRequest` (DB/offset/size) blocks, concatenates them into one `byte[]`, and notifies its registered `PlcDataObserver`s (a single-block read is just a one-element list).
- Per-station read specs (which DBs, what delay) live in `StationReadConfig`; stations are identified by the `Estacao` enum (`model/enums/`).
- **Observers** registered per task: the station's `*Comm` handler (each `implements PlcDataObserver`) and `PlcDataStore` (which caches the last raw `byte[]` per station).
- Each PLC's raw byte array is passed to a dedicated handler in `service/clp/`:
  - `EstoqueComm` — stock station
  - `ProcessoComm` — process station
  - `MontagemComm` — assembly station
  - `ExpedicaoComm` — dispatch station
- Parsed station state is stored in `@Component` beans under `model/clp/`: `EstoqueInfoClp`, `ExpedicaoInfoClp`, `MontagemInfo`, `ProcessoInfo`, several extending the abstract `EstacaoInfoClp` base class (common fields like `recebidoOp`, `ocupado`, `manual`, `emergencia`, and a `getStatus()` method).

**App-wide state** (`AppStateConfig`): a `@Component` bean (Lombok `@Getter`/`@Setter`) holding `volatile` shared state, replacing the old static fields. Includes `readOnly` (when `true`, the app reads PLC data but never writes back), `pedidoEmCurso`, the per-station `statusEstoque/Processo/Montagem/Expedicao/Producao` bytes, plus `blockFinished`, `aux_expedicao`, `posicaoExpedicaoSolicitada`. `resetarStatus()` zeros the status bytes.

**Writing**: `SmartService` (in `service/`) converts a `PedidoInfoDTO` into a 60-byte block (30 × int16) and writes it to the stock PLC (`writeBlock`), then pulses the start-order flag bit. The `sendBlockBytesToClp` method checks `AppStateConfig.isReadOnly()` before writing.

**SSE streaming** (`GET /api/smart/smartstream/{bancada}`): pushes raw hex strings (from `PlcDataStore`) to clients at ~400ms intervals. The `estoque` stream uses `PlcDataStore.getEstoqueComStatus()`, which appends 6 status bytes (statusEstoque/Processo/Montagem/Expedicao/Producao + pedidoEmCurso flag).

**Other CLP endpoints** on `SmartController` (all under `/api/smart`):
- `GET /api/smart/data/{clp}` — last raw hex read from each PLC (`clp1`..`clp4`)
- `POST /api/smart/stop-readings` — cancels all scheduled tasks and closes connections
- `POST /api/smart/smart/ping` — TCP reachability check on port 102 per IP
- `POST /api/smart/smart/reset-status` — zeros all production status fields
- `POST /api/smart/smart/readonly?value=true|false` / `GET /api/smart/smart/readonly`

**CLP IP configuration** is loaded from `application.properties` via `ClpIpConfig` (`@ConfigurationProperties(prefix = "clp")`). IPs are `clp.ips.estoque`, `.processo`, `.montagem`, `.expedicao`. An auxiliary ESP32 endpoint (`clp.endpoints.esp32-tampas.ip` / `.porta`, HTTP port 80) is also configured here for the lid/tampas device.

### Key naming conventions

- Portuguese is used throughout: method/field names, comments, test names.
- Test names follow `deveXxxQuandoYyy` convention.
- Tests use Mockito (`@ExtendWith(MockitoExtension.class)`) — no Spring context.
