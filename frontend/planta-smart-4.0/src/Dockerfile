# ─── Stage 1: build ───────────────────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

# Copia os manifests primeiro para aproveitar o cache de camadas
COPY package.json package-lock.json ./

# Instala dependências de forma determinística (respeita package-lock.json)
RUN npm ci

# Copia o restante do código e gera o build de produção
COPY . .
RUN npm run build

# ─── Stage 2: serve ───────────────────────────────────────────────────────────
FROM node:22-alpine AS runner

WORKDIR /app

# Instala o `serve` para servir os arquivos estáticos gerados
RUN npm install -g serve

# Copia apenas os artefatos de build do estágio anterior
COPY --from=builder /app/dist ./dist

EXPOSE 3000

# Serve o conteúdo da pasta dist na porta 3000
CMD ["serve", "-s", "dist", "-l", "3000"]