import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/user': {
            target: 'https://auris.hazenix.top',
        changeOrigin: true,
        secure: true,
      },
      '/api': {
          target: 'https://auris.hazenix.top',
        changeOrigin: true,
        secure: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
