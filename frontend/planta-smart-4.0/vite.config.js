import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@entities': path.resolve(__dirname, './src/domain/entities'),
      '@repositories': path.resolve(__dirname, './src/domain/repositories'),
      '@enums': path.resolve(__dirname, './src/domain/enums'),
      '@components': path.resolve(__dirname, './src/presentation/components'),
      '@pages': path.resolve(__dirname, './src/presentation/pages'),
      '@router': path.resolve(__dirname, './src/presentation/router'),
      '@valueObjects': path.resolve(__dirname, './src/domain/valueObjects'),
      '@http': path.resolve(__dirname, './src/infrastructure/http'),
      '@dtos': path.resolve(__dirname, './src/infrastructure/dtos'),
      '@service': path.resolve(__dirname, './src/service'),
      '@config': path.resolve(__dirname, './src/config'),
      '@repositoriesImp': path.resolve(__dirname, './src/infrastructure/repositories'),
      '@error': path.resolve(__dirname, './src/domain/error'),
      '@styles': path.resolve(__dirname, './src/styles'),
    },
  },
});
