# 🔧 Backend — Bancada Smart 4.0

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
│   └── PageController      → Roteamento de views Thymeleaf
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

---

## 🚀 Executando o Backend

### 1. Configurar o banco de dados

Crie o schema executando o script SQL incluído no projeto:

```bash
mysql -u root -p < src/main/resources/script.sql
```

Isso irá recriar o banco `DB_SA_SMART40` com todas as tabelas necessárias.

### 2. Ajustar as configurações

Edite o arquivo `src/main/resources/application.properties` conforme seu ambiente:

```properties
# URL do banco
spring.datasource.url=jdbc:mysql://localhost:3306/db_sa_smart40

# Credenciais (altere se necessário)
spring.datasource.username=root
spring.datasource.password=root

# Porta do servidor (padrão: 8080)
server.port=8080
```

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

## 📡 Endpoints da API

### Pedidos — `/api/pedidos`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/pedidos` | Criar um novo pedido |
| `GET` | `/api/pedidos` | Listar todos os pedidos |
| `GET` | `/api/pedidos/{id}` | Buscar pedido por ID |
| `PUT` | `/api/pedidos/{id}/status` | Marcar pedido como concluído |

### Estoque — `/api/estoque`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/estoque` | Consultar estado atual do estoque |
| `PUT` | `/api/estoque/{id}` | Atualizar posição/cor de um slot |

### Expedição — `/api/expedicao`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/expedicao` | Consultar slots de expedição |

> Todos os endpoints aceitam e retornam **JSON** e possuem `@CrossOrigin(origins = "*")` habilitado para integração com o frontend.

---

## 🧩 Modelos de Domínio

### Enumerações principais

**StatusPedido:** representa o ciclo de vida de um pedido (`EM_PROCESSO`, `EXPEDIDO`, `CONCLUIDO`)

**TipoPedido:** tipo de produto a ser fabricado (`TIPO_1`, `TIPO_2`, `TIPO_3`)

**CorBloco / CorEstoque / CorLamina / CorTampa:** identificadores de cor das peças físicas na bancada

**AndarBloco / PosicaoLamina:** posição física dos componentes dentro de cada bloco

---

## 🔌 Integração com CLPs

Para que a supervisão em tempo real funcione, os CLPs Siemens das estações devem estar acessíveis via rede (IP/TCP, porta 102). Configure os endereços IPs dos PLCs no código de inicialização conforme a topologia da rede da bancada.

A comunicação é **bidirecional**: o backend lê tags dos CLPs para exibir o status atual das estações e escreve tags para comandar operações como iniciar ou pausar uma etapa.

---

## 🧪 Testes

```bash
./mvnw test
```