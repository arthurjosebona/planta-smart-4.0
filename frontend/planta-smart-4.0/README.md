# 🖥️ Frontend — Bancada Smart 4.0

Interface web desenvolvida com **React + TypeScript** para supervisão e operação da Bancada Smart. Permite configurar a conexão com os CLPs, criar e gerenciar pedidos, visualizar o estoque e a expedição, e acompanhar o estado das estações em **tempo real** via streaming de eventos (SSE).

---

## 📦 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| React | 19 | Biblioteca de UI |
| TypeScript | — | Tipagem estática |
| Vite | 8 | Build tool e dev server |
| Tailwind CSS | 4 | Estilização utilitária (via `@tailwindcss/vite`) |
| CSS Modules | — | Estilos com escopo por componente (`*.module.css`) |
| React Router DOM | 7 | Roteamento SPA |
| Three.js | 0.184 | Renderização 3D |
| React Three Fiber | 9 | Integração Three.js com React |
| @react-three/drei | 10 | Utilitários 3D (câmera, controles, etc.) |
| clsx | 2 | Utilitário para classes condicionais |
| Prettier / ESLint | 3 / 10 | Formatação e qualidade de código |
| Server-Sent Events | nativo | Streaming em tempo real (`EventSource`) |

---

## 🗂️ Arquitetura

O frontend adota uma **Layered Architecture** com **Atomic Design** adaptada para React, separando **domínio**, **infraestrutura**, **serviços de aplicação** e **apresentação**. Isso mantém as regras de negócio do frontend independentes de frameworks e dos detalhes da API.

```
src/
│
├── domain/                   # Núcleo da aplicação (sem dependências externas)
│   ├── entities/             # Modelos de negócio (Pedido, Bloco, Lamina, Estoque,
│   │                         #   Expedicao, ModuloIP, EstacaoStream)
│   ├── enums/                # Enumerações espelhando o backend (CorBloco, CorTampa,
│   │                         #   StatusPedido, StatusEstacao, TipoPedido, etc.)
│   ├── repositories/         # Interfaces dos repositórios (contratos)
│   ├── valueObjects/         # Objetos de valor imutáveis (ConfigBloco, ConfigLamina)
│   └── error/                # Classes de erro de domínio (ApiError, HttpError)
│
├── infrastructure/           # Implementações concretas e integração com a API
│   ├── http/
│   │   └── HttpClient.ts      # Cliente HTTP genérico (wrapper de fetch com tipagem)
│   ├── dtos/
│   │   ├── request/           # Objetos de entrada para a API
│   │   └── response/          # Objetos de resposta da API (inclui DTOs de stream SSE)
│   ├── mappers/               # Conversão DTO ↔ Entidade de domínio
│   └── repositories/          # Implementações dos contratos (Pedido, Estoque,
│                              #   Expedicao, Conexao)
│
├── service/                  # Serviços de aplicação (orquestram casos de uso)
│   ├── PedidoService.ts
│   ├── EstoqueService.ts
│   ├── ExpedicaoService.ts
│   ├── ConexaoService.ts      # Conexão/leitura dos CLPs, ping e modo somente-leitura
│   └── CacheService.ts        # Cache em memória de dados de apoio
│
├── config/
│   ├── diContainer.ts         # Container de injeção de dependências manual
│   └── blockModel.ts          # Metadados/configuração dos modelos de bloco
│
├── contexts/                 # Estado global compartilhado (React Context)
│   ├── EstoqueContext.tsx     # Estado e edição do estoque
│   ├── ExpedicaoContext.tsx   # Estado e edição da expedição
│   ├── MonitorContext.tsx     # Streaming em tempo real das estações (SSE / EventSource)
│   └── PingContext.tsx        # Status online/offline dos CLPs (polling a cada 10s)
│
├── presentation/             # Tudo relacionado à UI
│   ├── components/
│   │   ├── atoms/             # Componentes elementares (botão, badge, chip, swatch...)
│   │   ├── molecules/         # Composições de atoms (nav link, cards, seções, views...)
│   │   ├── organisms/         # Blocos funcionais completos (Header, OrderForm,
│   │   │                      #   OrderViewer, ClpMonitorGrid, modais...)
│   │   └── template/          # Layout base da aplicação (AppTemplate)
│   ├── pages/                 # Páginas no padrão MVVM (Model / View / ViewModel)
│   ├── hook/                  # Hooks compartilhados (ex.: useConexaoStatus)
│   └── utils/                 # Utilitários de apresentação (ex.: pedidoToStoreModel)
│
├── router/
│   └── routes.tsx            # Definição das rotas da aplicação
│
├── styles/
│   └── global.css            # Estilos globais e variáveis CSS
│
├── assets/                   # Imagens da bancada, blocos, lâminas, padrões e ícones
├── App.tsx                   # Composição dos Providers + Router
└── main.tsx                  # Ponto de entrada (ReactDOM)
```

### Padrão MVVM nas Páginas

Cada página segue o padrão **Model / View / ViewModel**:

- **Model** (`*Model.ts`): define as interfaces e tipos de dados da página
- **View** (`*View.tsx`): componente React responsável apenas pela renderização
- **ViewModel** (`use*ViewModel.ts`): hook customizado com estado, efeitos e lógica de interação

Esse padrão mantém os componentes de UI simples e reutilizáveis, concentrando a lógica nos hooks. A maioria dos ViewModels consome o estado global a partir dos **Contexts** e delega operações aos **Services** resolvidos pelo `diContainer`.

### Estilização

A estilização combina **Tailwind CSS 4** (utilitários) com **CSS Modules** — cada componente que precisa de estilos próprios tem um arquivo `*.module.css` ao lado, garantindo escopo local de classes. Variáveis e estilos globais ficam em `src/styles/global.css`.

### Aliases de Importação

O projeto usa aliases configurados no `vite.config.js` para evitar caminhos relativos longos:

| Alias | Caminho real |
|---|---|
| `@entities` | `src/domain/entities` |
| `@enums` | `src/domain/enums` |
| `@repositories` | `src/domain/repositories` |
| `@valueObjects` | `src/domain/valueObjects` |
| `@error` | `src/domain/error` |
| `@http` | `src/infrastructure/http` |
| `@dtos` | `src/infrastructure/dtos` |
| `@mappers` | `src/infrastructure/mappers` |
| `@repositoriesImp` | `src/infrastructure/repositories` |
| `@service` | `src/service` |
| `@config` | `src/config` |
| `@contexts` | `src/contexts` |
| `@components` | `src/presentation/components` |
| `@pages` | `src/presentation/pages` |
| `@utils` | `src/presentation/utils` |
| `@router` | `src/presentation/router` |
| `@styles` | `src/styles` |
| `@assets` | `src/assets` |

---

## 🖼️ Páginas e Funcionalidades

A navegação fica no `Header` e um indicador de status mostra se a bancada está conectada. A rota raiz (`/`) redireciona para **IPs** (`/ips`).

### IPs / Home (`/ips`)
Tela inicial de **configuração da conexão com os CLPs**. Permite definir a faixa de IP dos módulos, conectar à bancada (aplica os IPs e inicia as leituras que alimentam os streams) e alternar o **modo somente-leitura**. As mudanças de IP e o toggle de read-only usam atualização otimista com reversão em caso de falha.

### Store (`/store`)
Formulário de **criação/edição de pedido** com **visualização 3D** do bloco montado (Three.js / React Three Fiber). Permite escolher o número de blocos, a cor da tampa e a cor/padrão das lâminas de cada andar.

### Pedidos (`/pedidos`)
**Listagem de todos os pedidos** com status, tipo, cor da tampa e timestamps. Permite **atualizar** e **excluir** pedidos por meio de um modal (`PedidoModal`).

### Dashboard (`/dashboard`)
**Visão operacional** da bancada combinando estoque e expedição. Permite editar posições do estoque, gerenciar slots de expedição, abrir o detalhe de um slot (`ExpedicaoDetalheModal`) e **iniciar a produção** de um pedido.

### Estações (`/estacoes`)
Visão das **quatro estações** (Estoque, Processo, Montagem, Expedição) com o status derivado em tempo real — combinando o ping de conexão (online/offline) com o `status` recebido via stream (ocupado/pausado).

### Monitor (`/monitor`)
**Monitoramento em tempo real dos CLPs** via SSE. Exibe os dados de cada estação (`ClpMonitorGrid` / `ClpStationCard`) conforme chegam pelos eventos `estoque`, `processo`, `montagem` e `expedicao`.

---

## 🔌 Tempo real (SSE) e estado global

O estado compartilhado é provido por Contexts compostos em `App.tsx`:

- **`MonitorContext`** abre uma conexão `EventSource` com o backend e escuta os eventos `estoque`, `processo`, `montagem` e `expedicao`, mapeando cada payload para entidades de domínio (`EstacaoStream`).
- **`PingContext`** faz polling do status dos CLPs a cada **10 segundos** (`ConexaoService.pingAll`), expondo um mapa `online/offline` por estação.
- **`EstoqueContext`** e **`ExpedicaoContext`** mantêm o estado e as operações de edição do estoque e da expedição.

---

## ⚙️ Pré-requisitos

- **Node.js 22.x** (versão exigida no `package.json`)
- **npm** ou **yarn**
- Backend em execução em `http://localhost:8088`

---

## 🚀 Executando o Frontend

> Todos os comandos rodam no diretório `frontend/planta-smart-4.0/`.

### 1. Instalar as dependências

```bash
npm install
```

### 2. Iniciar o servidor de desenvolvimento

```bash
npm run dev
```

O Vite expõe a aplicação em `http://localhost:5173` (ou outra porta disponível) e em todas as interfaces de rede da máquina (`--host`), permitindo acesso via IP local.

### 3. Build para produção

```bash
npm run build
```

Os arquivos otimizados são gerados em `dist/`. Para pré-visualizar o build:

```bash
npm run preview
```

---

## 🐳 Docker

Há um `Dockerfile` (em `src/Dockerfile`) com build multi-stage: o primeiro estágio gera o build de produção com `node:22-alpine` e o segundo serve o conteúdo de `dist/` com `serve` na porta **3000**.
## Comandos para iniciar o Docker

### 1. Cria uma imagem Docker a partir do arquivo Dockerfile
docker build -t smart-frontend .

### 2. Cria e inicia um container usando a imagem smart-frontend.

docker run -p 3000:3000 smart-frontend 
# Depois acessa em http://localhost:3000.

```bash
# a partir da pasta que contém o Dockerfile
docker build -t planta-smart-frontend .
docker run -p 3000:3000 planta-smart-frontend
```

---

## 🧹 Qualidade de Código

**Verificar formatação (Prettier):**

```bash
npm run format:check
```

**Aplicar formatação automática:**

```bash
npm run format
```

**Executar o linter (ESLint):**

```bash
npm run lint
```

---

## 🌐 Integração com o Backend

- **API REST:** o `HttpClient` (`src/infrastructure/http/HttpClient.ts`) centraliza as chamadas HTTP (`GET`/`POST`/`PUT`/`PATCH`/`DELETE`) e aponta para `http://localhost:8080` por padrão. Ele trata respostas `204 No Content` e endpoints que respondem em texto puro (ex.: `POST /api/smart/readonly`), além de encapsular erros em `HttpError`. Para apontar para outro endereço, ajuste a `baseURL` no `HttpClient`.

- **Streaming (SSE):** o `MonitorContext` consome o stream de eventos da bancada. A URL padrão é `http://localhost:8088/api/smart/stream` e pode ser sobrescrita pela variável de ambiente **`VITE_SSE_URL`** (ex.: em um arquivo `.env` na raiz do projeto):

  ```env
  VITE_SSE_URL=http://localhost:8088/api/smart/stream
  ```
