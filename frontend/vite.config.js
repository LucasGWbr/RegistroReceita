import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  // Carrega as variáveis do .env
  const env = loadEnv(mode, process.cwd(), '');

  console.log("URL DO BACKEND:", env.VITE_API_URL);
  return {
    plugins: [react()],

    // 1. Proxy para o ambiente de DESENVOLVIMENTO (npm run dev - Porta 5173)
    server: {
      proxy: {
        '/api': {
          target: env.VITE_API_URL,
          changeOrigin: true,
        }
      }
    },

    // 2. Proxy para o ambiente de PREVIEW/BUILD (npm run preview - Porta 4173)
    preview: {
      proxy: {
        '/api': {
          target: env.VITE_API_URL,
          changeOrigin: true,
        }
      }
    }
  }
})