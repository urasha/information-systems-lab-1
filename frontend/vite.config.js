import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/~s413022/',
  plugins: [vue()],
  define: {
    global: 'window'
  }
})
