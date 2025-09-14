import { fileURLToPath, URL } from 'node:url'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import path from 'path';
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
      // 添加 SVG 插件配置
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), "src/icons/svg")], // 你的 SVG 图标目录
      symbolId: 'icon-[dir]-[name]',
      inject: 'body-last',
      customDomId: '__svg__icons__dom__',
    }),
  ],
  css: {
    preprocessorOptions: {
      // 引入全局scss
      scss: {
        additionalData: `@use "@/styles/base.scss";`,
      },
    },
  },
  base: '/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 3000,
    host: "0.0.0.0",
    open: true,
    hmr: {
      overlay: false
    },
    // 设置代理
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
        // rewrite: path => path.replace(/^\/api/, "")
      }
    }
  },
})
