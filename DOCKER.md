# Deploy com Docker — Planta Smart 4.0

Este guia cobre como empacotar e rodar o sistema completo (frontend + backend + banco) com Docker Compose, sem precisar instalar Node, Java ou MySQL na máquina.

---

## Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows/Mac) ou Docker Engine + Docker Compose (Linux)
- Acesso à rede onde os CLPs estão

---

## Estrutura dos arquivos Docker

```
planta-smart-4.0/
├── docker-compose.yml                        # orquestra os 3 serviços
├── .env.example                              # template de variáveis — copie para .env
├── frontend/planta-smart-4.0/
│   └── Dockerfile                            # build Vite + serve estático
└── backend/planta-smart-4.0/
    └── Dockerfile                            # build Maven + runtime JRE
```

---

## 1. Configurar as variáveis de ambiente

Copie o template e preencha com os valores reais:

Rode:

```bash
cp .env.example .env
```

Ou copie manualmente para o arquivo `.env`.

Abra o `.env` e ajuste:

```env
# URL que o NAVEGADOR usará para chamar o backend
# Em produção, coloque o IP/hostname da máquina onde o backend vai rodar
VITE_BASE_URL=http://192.168.1.100:8080

# Porta do backend Spring Boot
PORT=8080

# Banco de dados
DB_USER=user
DB_PASSWORD=password
MYSQL_ROOT_PASSWORD=password
MYSQL_DATABASE=db_sa_smart40
```

> **Atenção `VITE_BASE_URL`**: esta variável é embutida no bundle JavaScript em *build-time*.
> Se mudar o IP do backend depois do build, precisará reconstruir a imagem do frontend.

---

## 2. Subir tudo

Na raiz do projeto (onde está o `docker-compose.yml`):

```bash
docker compose up --build
```

Na primeira vez, o Docker vai:
1. Baixar as imagens base (Node 22, Maven 3.9, JRE 17, MySQL 8, nginx)
2. Compilar o frontend (`npm run build`)
3. Compilar o backend (`mvn package`)
4. Subir os 3 containers em ordem: `db` → `backend` → `frontend`

Após subir:

| Serviço   | Endereço               |
|-----------|------------------------|
| Frontend  | http://localhost:3000  |
| Backend   | http://localhost:8080  |
| MySQL     | localhost:3307         |

---

## 3. Comandos 

```bash
# Subir (sem rebuild)
docker compose up -d

# Parar
docker compose down

# Parar e apagar o banco (apaga os dados)
docker compose down -v

# Rebuild somente do frontend (ex: mudou VITE_BASE_URL)
docker compose build frontend
docker compose up -d frontend

# Rebuild somente do backend
docker compose build backend
docker compose up -d backend

# Ver logs em tempo real
docker compose logs -f

# Ver logs de um serviço específico
docker compose logs -f frontend
docker compose logs -f backend
```

---

## 4. Adicionar novas variáveis de ambiente

### Variável no frontend (React/Vite)

1. Adicione com prefixo `VITE_` no `.env`:
   ```env
   VITE_MINHA_VAR=valor
   ```

2. Declare o `ARG` e o `ENV` no `frontend/planta-smart-4.0/Dockerfile`:
   ```dockerfile
   ARG VITE_MINHA_VAR
   ENV VITE_MINHA_VAR=$VITE_MINHA_VAR
   ```

3. Passe via `args` no `docker-compose.yml`:
   ```yaml
   frontend:
     build:
       args:
         VITE_BASE_URL: ${VITE_BASE_URL}
         VITE_MINHA_VAR: ${VITE_MINHA_VAR}   # adicione aqui
   ```

4. Use no código TypeScript:
   ```ts
   const valor = import.meta.env.VITE_MINHA_VAR;
   ```

5. Rebuild obrigatório após qualquer alteração:
   ```bash
   docker compose build frontend && docker compose up -d frontend
   ```

### Variável no backend (Spring Boot)

1. Adicione no `.env`:
   ```env
   MINHA_VAR_BACKEND=valor
   ```

2. Passe via `environment` no `docker-compose.yml`:
   ```yaml
   backend:
     environment:
       MINHA_VAR_BACKEND: ${MINHA_VAR_BACKEND}
   ```

3. Use no `application.properties`:
   ```properties
   minha.propriedade=${MINHA_VAR_BACKEND}
   ```

4. Variáveis de ambiente do backend são lidas em *runtime* — não precisa rebuild, só restart:
   ```bash
   docker compose up -d backend
   ```

