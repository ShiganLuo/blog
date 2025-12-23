import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv, type ConfigEnv } from 'vite' // 引入类型
import vue from '@vitejs/plugin-vue'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'path'

// https://vite.dev/config/
export default defineConfig(({ mode }: ConfigEnv) => {
  // 获取当前运行目录
  const root = process.cwd()
  // 加载环境变量：会根据 mode 加载对应的 .env.development 或 .env.production
  const env = loadEnv(mode, root)
  
  // 结构出你需要的值（建议在 .env 文件中定义这些变量）
  const { VITE_PORT, VITE_API_URL, VITE_BASE_URL } = env
  console.log(`🚀 API_URL = ${VITE_API_URL}`)
  console.log(`🚀 PORT = ${VITE_PORT}`)
  return {
    // 部署的基础路径
    base: VITE_BASE_URL || '/',
    
    plugins: [
      vue(),
      // SVG 插件配置
      createSvgIconsPlugin({
        // 建议使用 path.resolve 确保路径正确
        iconDirs: [path.resolve(root, "src/icons/svg")],
        symbolId: 'icon-[dir]-[name]',
        inject: 'body-last',
        customDomId: '__svg__icons__dom__',
      }),
    ],

    resolve: {
      alias: {
        // 使用 fileURLToPath 配合 URL，不再需要单独写 resolvePath 函数
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },

    css: {
      preprocessorOptions: {
        scss: {
          // 注意：如果使用了 Sass 1.8.0+，建议加上 api: 'modern-compiler'
          additionalData: `@use "@/styles/base.scss";`,
        },
      },
    },

    server: {
      // 如果环境变量没定义，默认 3000
      port: VITE_PORT ? Number(VITE_PORT) : 3000,
      host: "0.0.0.0",
      open: true,
      hmr: {
        overlay: false
      },
      // 设置代理
      proxy: {
        '/api': {
          // 动态读取环境变量中的后端地址
          target: VITE_API_URL || 'http://127.0.0.1:8080',
          changeOrigin: true,
          secure: false,
          // rewrite: (path) => path.replace(/^\/api/, "")
        }
      }
    },
    
    // 生产环境打包优化
    build: {
      chunkSizeWarningLimit: 2000,
      // 可以在这里对齐“能跑的项目”里的 terser 压缩配置
    }
  }
})