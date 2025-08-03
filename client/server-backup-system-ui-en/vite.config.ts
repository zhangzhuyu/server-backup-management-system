// vite.config.js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        // 匹配所有以 /api 开头的请求
        target: 'http://localhost:50016', // 目标后端服务器
        changeOrigin: true, // 必须 true
        // **** 添加路径重写 (rewrite) ****
        rewrite: (path) => {
          console.log(`代理重写前路径: ${path}`) // 打印原始路径，方便调试
          // 将路径中开头的 /api 替换为 /api/ts-dc-backup
          const newPath = path.replace(/^\/api/, '/api/ts-dc-backup')
          console.log(`代理重写后路径: ${newPath}`) // 打印重写后的路径
          return newPath
        },
        // **** 重写结束 ****
      },
    },
  },
})
