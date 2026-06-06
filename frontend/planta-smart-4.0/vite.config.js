import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@entities': path.resolve(__dirname, './src/domain/entities'),
      '@repositories': path.resolve(__dirname, './src/domain/repositories'),
      '@enums': path.resolve(__dirname, './src/domain/enums'),
      '@usecases': path.resolve(__dirname, './src/data/usecases'),
      '@components': path.resolve(__dirname, './src/presentation/components'),
      '@pages': path.resolve(__dirname, './src/presentation/pages'),
      '@router': path.resolve(__dirname, './src/presentation/router'),
      '@valueObjects': path.resolve(__dirname, './src/domain/valueObjects'),
      '@http': path.resolve(__dirname, './src/infrastructure/http'),
      '@dtos': path.resolve(__dirname, './src/infrastructure/dtos'),
    },
  },
});
