

---

## 🐳 Docker

O backend possui um `Dockerfile` (build multi-stage: Maven → JRE 17) e é orquestrado junto com o banco e o frontend pelo `docker-compose.yml` na raiz do repositório. Consulte o guia completo em [**DOCKER.md**](../../DOCKER.md).# 🔧 Backend — Bancada Smart 4.0

API REST desenvolvida com **Spring Boot** responsável por toda a lógica de negócio, persistência de dados e comunicação direta com os CLPs Siemens via protocolo S7.

---

## 📦 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 4.0.5 | Framework web |
| Spring Data JPA | — | Acesso ao banco de dados |
| Spring MVC | — | Camada REST |
| Spring Validation | — | Validação de entradas |
| Thymeleaf | — | Templates HTML (views auxiliares) |
| MySQL Connector | — | Driver JDBC |
| Lombok | — | Redução de boilerplate |
| Protocolo S7 | Implementação própria | Comunicação com CLPs Siemens |

---

## 🗂️ Arquitetura

O backend adota uma arquitetura em camadas bem definidas, separando responsabilidades e facilitando a manutenção:

```
com.smart.appsa/
│
├── clpcomm/              # Comunicação com CLPs Siemens
│   ├── S7ProtocolClient  # Implementação do protocolo S7 sobre TCP/IP
│   ├── PlcConnector      # Abstração de conexão (connect, read, write, disconnect)
│   └── PlcConnectionService  # Gerenciamento de conexões ativas (multi-thread)
│
├── controller/           # Camada de entrada HTTP (REST)
│   ├── PedidoController    → /api/pedidos
│   ├── EstoqueController   → /api/estoque
│   ├── ExpedicaoController → /api/expedicao
│   ├── ClpConfigController → /api/config/clp
│   └── SmartController     → /api/smart (leituras, SSE, ping, readonly)
│
├── service/              # Regras de negócio
│   ├── PedidoService
│   ├── EstoqueService
│   ├── ExpedicaoService
│   ├── LaminaService
│   └── BlocoService
│
├── model/                # Entidades JPA
│   ├── Pedido
│   ├── Estoque
│   ├── Expedicao
│   ├── Bloco
│   ├── Lamina
│   └── enums/            # Enumerações de domínio (cores, status, posições)
│
├── repository/           # Interfaces Spring Data JPA
│
├── dto/
│   ├── request/          # Objetos de entrada (recebidos pelo cliente)
│   └── response/         # Objetos de saída (enviados ao cliente)
│
├── mapper/               # Conversão entre entidades e DTOs
│
├── exception/            # Hierarquia de exceções de domínio
│   ├── core/             # Exceções base (AppException, BusinessException, etc.)
│   ├── handler/          # GlobalExceptionHandler (tratamento centralizado)
│   └── [exceções específicas de negócio]
│
└── config/
    └── DataInitializer   # Seed de dados iniciais ao subir a aplicação
```

### Camada de Comunicação com CLPs

O módulo `clpcomm` é a parte mais crítica do sistema. Ele implementa do zero o protocolo **S7** da Siemens (ISO-on-TCP, porta 102), permitindo leitura e escrita de variáveis diretamente nos blocos de dados (DBs) dos CLPs sem depender de bibliotecas externas de terceiros.

A classe `PlcConnectionService` gerencia as conexões de forma concorrente, mantendo threads de leitura ativas para cada estação monitorada.

---

## 🗄️ Banco de Dados

O banco utilizado é o **MySQL**. O schema é criado manualmente antes da primeira execução.

**Nome do banco:** `DB_SA_SMART40`

**Tabelas principais:**

| Tabela | Descrição |
|---|---|
| `T_SA_PEDIDO` | Ordens de produção com status, tipo e rastreamento |
| `T_SA_ESTOQUE` | Posições físicas do estoque e cor de cada bloco |
| `T_SA_EXPEDICAO` | Slots de expedição e vínculo com pedidos |
| `T_SA_BLOCO` | Configuração dos blocos (cor, andares, lâminas) |
| `T_SA_LAMINA` | Lâminas individuais de cada bloco |

---

## ⚙️ Pré-requisitos

- **Java 17** ou superior
- **Maven** (ou use o `mvnw` incluso no projeto)
- **MySQL 8+** em execução local

> 💡 **Prefere não instalar Java/MySQL na máquina?** Suba tudo (banco + backend + frontend) com Docker Compose seguindo o guia [**DOCKER.md**](../../DOCKER.md).

---

## 🚀 Executando o Backend (Manual de Instalação)

### 1. Configurar o banco de dados

Crie o schema executando o script SQL incluído no projeto:

```bash
mysql -u root -p < src/main/resources/script.sql
```

Isso irá **recriar** o banco `DB_SA_SMART40` (o script começa com `DROP DATABASE IF EXISTS`) com todas as tabelas e constraints necessárias.

> O schema também é mantido pelo JPA em tempo de execução (`spring.jpa.hibernate.ddl-auto=update`), mas rodar o `script.sql` garante o estado inicial correto na primeira instalação.

### 2. Configurar as variáveis de ambiente

As credenciais do banco **não ficam no `application.properties`** — elas são lidas de um arquivo `.env` na raiz de `backend/planta-smart-4.0/` (carregado pela biblioteca `spring-dotenv`).

Crie o arquivo `.env`:

```env
# Usuário e senha do MySQL
DB_USER=root
DB_PASSWORD=sua_senha

# Porta do servidor Spring Boot (opcional — padrão 8080)
PORT=8080
```

| Variável | Obrigatória | Descrição |
|---|---|---|
| `DB_USER` | ✅ | Usuário do MySQL |
| `DB_PASSWORD` | ✅ | Senha do MySQL |
| `PORT` | ❌ | Porta HTTP do backend (padrão `8080`) |

O `application.properties` referencia essas variáveis (`${DB_USER}`, `${DB_PASSWORD}`, `${PORT:8080}`) e já aponta para `jdbc:mysql://localhost:3306/db_sa_smart40`. Os **IPs dos CLPs** também ficam no `application.properties` (`clp.ips.*`) — ajuste-os para a topologia da sua rede.

### 3. Executar a aplicação

**Com Maven Wrapper (recomendado):**

```bash
# No diretório backend/planta-smart-4.0/
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

**Com Maven instalado:**

```bash
mvn spring-boot:run
```

**Gerando o JAR e executando:**

```bash
./mvnw clean package -DskipTests
java -jar target/appsa-0.0.1-SNAPSHOT.jar
```

A API estará disponível em: **`http://localhost:8080`**

---

## 📖 Documentação da API (Swagger / OpenAPI)

Com a aplicação em execução, a documentação interativa de **todos os endpoints** é gerada automaticamente pelo **springdoc-openapi**:

| Recurso | URL |
|---|---|
| **Swagger UI** (interface interativa) | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** (contrato) | http://localhost:8080/v3/api-docs |

Pela Swagger UI é possível **visualizar e testar** cada endpoint diretamente pelo navegador (botão *Try it out*), inspecionando parâmetros, corpos de requisição e respostas. Os metadados (título, descrição, versão) são definidos em `config/OpenApiConfig.java`.

---

## 📡 Endpoints da API

### Pedidos — `/api/pedidos`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/pedidos` | Criar um novo pedido |
| `GET` | `/api/pedidos` | Listar todos os pedidos |
| `GET` | `/api/pedidos/{id}` | Buscar pedido por ID |
| `GET` | `/api/pedidos/op/{op}` | Buscar pedido pela ordem de produção |
| `GET` | `/api/pedidos/expedicao/{id}` | Listar pedidos de um slot de expedição |
| `PUT` | `/api/pedidos/{id}` | Atualizar um pedido |
| `PUT` | `/api/pedidos/start-production/{id}` | Iniciar a produção do pedido (envia ao CLP) |
| `DELETE` | `/api/pedidos/{id}` | Excluir um pedido |

### Estoque — `/api/estoque`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/estoque` | Consultar todo o estoque |
| `GET` | `/api/estoque/{id}` | Buscar posição de estoque por ID |
| `GET` | `/api/estoque/disponivel` | Listar posições disponíveis |
| `GET` | `/api/estoque/indisponivel` | Listar posições ocupadas |
| `PUT` | `/api/estoque` | Atualizar em lote posições/cores do estoque |

### Expedição — `/api/expedicao`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/expedicao` | Consultar todos os slots de expedição |
| `GET` | `/api/expedicao/{id}` | Buscar slot de expedição por ID |
| `PUT` | `/api/expedicao` | Atualizar em lote os slots de expedição |

### Configuração dos CLPs — `/api/config/clp`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/config/clp/ips` | Obter o mapa de IPs dos CLPs |
| `GET` | `/api/config/clp/ips/{clp}` | Obter o IP de um CLP específico |
| `PUT` | `/api/config/clp/ips/{clp}` | Atualizar o IP de um CLP |
| `PUT` | `/api/config/clp/ips` | Atualizar todos os IPs de uma vez |

### Supervisão / CLPs — `/api/smart`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/smart/start-readings` | Inicia as leituras dos CLPs (recebe `{ estoque, processo, montagem, expedicao }`) |
| `POST` | `/api/smart/stop-readings` | Interrompe as leituras e fecha as conexões |
| `GET` | `/api/smart/data/{clp}` | Último dado bruto (hex) lido de um CLP |
| `GET` | `/api/smart/stream` | **SSE** multiplexado com eventos de todas as estações |
| `GET` | `/api/smart/stream/{bancada}` | **SSE** de uma única estação |
| `POST` | `/api/smart/ping` | Verifica a conectividade (TCP:102) de cada CLP |
| `POST` | `/api/smart/reset-status` | Zera os campos de status de produção |
| `POST` | `/api/smart/readonly?value=true\|false` | Ativa/desativa o modo somente-leitura |
| `GET` | `/api/smart/readonly` | Consulta o estado do modo somente-leitura |

> Todos os endpoints aceitam e retornam **JSON** (exceto os streams SSE, que emitem `text/event-stream`). O CORS é habilitado globalmente para integração com o frontend.

---

## 🧩 Modelos de Domínio

### Enumerações principais

**StatusPedido:** representa o ciclo de vida de um pedido (`EM_PROCESSO`, `EXPEDIDO`, `CONCLUIDO`)

**TipoPedido:** tipo de produto a ser fabricado (`TIPO_1`, `TIPO_2`, `TIPO_3`)

**CorBloco / CorEstoque / CorLamina / CorTampa:** identificadores de cor das peças físicas na bancada

**AndarBloco / PosicaoLamina:** posição física dos componentes dentro de cada bloco

---

## 🔌 Integração com CLPs

Para que a supervisão em tempo real funcione, os CLPs Siemens das estações devem estar acessíveis via rede (IP/TCP, porta 102). Configure os endereços IPs dos PLCs em `src/main/resources/application.properties` (`clp.ips.estoque`, `clp.ips.processo`, `clp.ips.montagem`, `clp.ips.expedicao`) — ou em tempo de execução via `PUT /api/config/clp/ips` — conforme a topologia da rede da bancada.

A comunicação é **bidirecional**: o backend lê tags dos CLPs para exibir o status atual das estações e escreve tags para comandar operações como iniciar ou pausar uma etapa.

---

## 🧪 Testes

```bash
./mvnw test
```