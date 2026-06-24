# 🖥️ Frontend — Bancada Smart 4.0

Interface web desenvolvida com **React + TypeScript** para supervisão e operação da Bancada Smart. Permite visualizar o estado das estações em tempo real, gerenciar pedidos e acompanhar o fluxo de produção.

---

## 📦 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| React | 19 | Biblioteca de UI |
| TypeScript | — | Tipagem estática |
| Vite | 8 | Build tool e dev server |
| Tailwind CSS | 4 | Estilização utilitária |
| React Router DOM | 7 | Roteamento SPA |
| Three.js | 0.184 | Renderização 3D |
| React Three Fiber | 9 | Integração Three.js com React |
| @react-three/drei | 10 | Utilitários 3D (câmera, controles, etc.) |
| clsx | 2 | Utilitário para classes condicionais |
| Prettier / ESLint | — | Formatação e qualidade de código |

---

## 🗂️ Arquitetura

O frontend adota **Layered Architecture** com **Atomic Design** adaptada para React, com separação entre domínio, infraestrutura e apresentação. Isso garante que as regras de negócio do frontend sejam independentes de frameworks e da API.

```
src/
│
├── domain/                   # Núcleo da aplicação (sem dependências externas)
│   ├── entities/             # Modelos de negócio (Pedido, Estoque, Bloco, Lamina, Expedicao)
│   ├── enums/                # Enumerações espelhando o backend
│   ├── repositories/         # Interfaces dos repositórios (contratos)
│   ├── valueObjects/         # Objetos de valor imutáveis (ConfigBloco, ConfigLamina)
│   └── error/                # Classes de erro de domínio (ApiError, HttpError)
│
├── infrastructure/           # Implementações concretas e integração com a API
│   ├── http/
│   │   └── HttpClient.ts     # Cliente HTTP genérico (fetch wrapper com tipagem)
│   ├── dtos/
│   │   ├── request/          # Objetos de entrada para a API
│   │   └── response/         # Objetos de resposta da API
│   ├── mappers/              # Conversão DTO ↔ Entidade de domínio
│   └── repositories/         # Implementações dos contratos de repositório
│
├── service/                  # Serviços de aplicação (orquestram casos de uso)
│   ├── EstoqueService.ts
│   ├── ExpedicaoService.ts
│   └── PedidoService.ts
│
├── config/
│   └── diContainer.ts        # Container de injeção de dependências manual
│
├── presentation/             # Tudo relacionado à UI
│   ├── components/
│   │   ├── atoms/            # Componentes elementares (botão, badge, título, ícone...)
│   │   ├── molecules/        # Composições de atoms (card header, nav link, seções...)
│   │   ├── organisms/        # Blocos funcionais completos (formulário, header, OrderViewer...)
│   │   └── template/         # Layout base da aplicação (AppTemplate)
│   └── pages/
│       ├── Dashboard/        # Página principal com visão da bancada
│       │   ├── DashboardModel.ts        # Tipagem dos dados da página
│       │   ├── DashboardView.tsx        # Componente de renderização
│       │   └── useDashboardViewModel.ts # Hook com lógica e estado
│       ├── Pedidos/          # Listagem e gerenciamento de pedidos
│       └── Store/            # Visualização e gestão do estoque
│
├── router/
│   └── routes.tsx            # Definição das rotas da aplicação
│
└── styles/
    └── global.css            # Estilos globais e variáveis CSS
```

### Padrão MVVM nas Páginas

Cada página segue o padrão **Model / View / ViewModel**:

- **Model** (`.ts`): define as interfaces e tipos de dados da página
- **View** (`.tsx`): componente React responsável apenas pela renderização
- **ViewModel** (`use*ViewModel.ts`): hook customizado com estado, efeitos e lógica de interação

Esse padrão mantém os componentes de UI simples e reutilizáveis, concentrando a lógica nos hooks.

### Aliases de Importação

O projeto usa aliases configurados no `vite.config.js` para evitar caminhos relativos longos:

| Alias | Caminho real |
|---|---|
| `@entities` | `src/domain/entities` |
| `@enums` | `src/domain/enums` |
| `@repositories` | `src/domain/repositories` |
| `@components` | `src/presentation/components` |
| `@pages` | `src/presentation/pages` |
| `@service` | `src/service` |
| `@http` | `src/infrastructure/http` |
| `@dtos` | `src/infrastructure/dtos` |
| `@repositoriesImp` | `src/infrastructure/repositories` |
| `@config` | `src/config` |
| `@styles` | `src/styles` |

---

## 🖼️ Páginas e Funcionalidades

### Store (`/store`)
Formulário de criação do pedido com visualização do pedido em 3D.

### Pedidos (`/pedidos`)
Listagem de todos os pedidos com informações de status, tipo, cor da tampa e timestamps. 

### Estoque (`/estoque`)
Visualização das 28 posições físicas do estoque com a cor de cada bloco armazenado. Permite atualizar as informações de cada posição.

---

## ⚙️ Pré-requisitos

- **Node.js 22.x** (versão exigida no `package.json`)
- **npm** ou **yarn**
- Backend em execução em `http://localhost:8088`

---

## 🚀 Executando o Frontend

### 1. Instalar as dependências

```bash
# No diretório frontend/planta-smart-4.0/
npm install
```

### 2. Iniciar o servidor de desenvolvimento

```bash
npm run dev
```

O Vite irá expor a aplicação em `http://localhost:5173` (ou outra porta disponível) e em todas as interfaces de rede da máquina (`--host`), permitindo acesso via IP local.

### 3. Build para produção

```bash
npm run build
```

Os arquivos otimizados serão gerados em `dist/`. Para pré-visualizar o build:

```bash
npm run preview
```

---

## 🧹 Qualidade de Código

**Verificar formatação:**
```bash
npm run format:check
```

**Aplicar formatação automática (Prettier):**
```bash
npm run format
```

**Executar o linter (ESLint):**
```bash
npm run lint
```

---

## 🌐 Integração com o Backend

O `HttpClient` (em `src/infrastructure/http/HttpClient.ts`) centraliza todas as chamadas HTTP. A URL base aponta para `http://localhost:8080/api` por padrão.

Se o backend estiver rodando em outro endereço ou porta, ajuste a configuração no `HttpClient`.
