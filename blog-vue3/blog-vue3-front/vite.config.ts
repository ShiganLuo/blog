import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv, type ConfigEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import viteCompression from 'vite-plugin-compression'

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
        iconDirs: [path.resolve(root, "src/icons/svg")],
        symbolId: 'icon-[dir]-[name]',
        inject: 'body-last',
        customDomId: '__svg__icons__dom__',
      }),
      // Gzip压缩
      viteCompression({
        threshold: 10240, // 大于10KB的文件进行压缩
        algorithm: 'gzip',
        ext: '.gz',
      }),
      // 自动导入Vue/Pinia/VueRouter API
      AutoImport({
        imports: ['vue', 'vue-router', 'pinia'],
        resolvers: [ElementPlusResolver()],
        dts: 'src/auto-imports.d.ts',
      }),
      // 组件自动注册（Element Plus按需引入）
      Components({
        resolvers: [ElementPlusResolver()],
        dts: 'src/components.d.ts',
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
          additionalData: `@use "@/styles/base.scss";`,
        },
      },
    },

    // 依赖预构建优化
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'pinia',
        'axios',
        'element-plus',
        '@element-plus/icons-vue',
        'gsap',
      ],
    },

    server: {
      // 如果环境变量没定义，默认 3000
      port: Number(VITE_PORT),
      host: true, // 监听所有网卡
      open: true,
      hmr: {
        overlay: false
      },
      // 设置代理
      proxy: {
        '/dev-api': {
          // 开发环境
          target: VITE_API_URL,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/dev-api/, '/api')
        },
        '/api': {
          // 生产环境
          target: VITE_API_URL,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '/api')
        }
      },
    },
    
    // 生产环境打包优化
    build: {
      chunkSizeWarningLimit: 2000,
      // Terser压缩配置
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true, // 移除console
          drop_debugger: true, // 移除debugger
        },
      },
      // 代码分割配置
      rollupOptions: {
        output: {
          // 手动分割chunk
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            'element-plus': ['element-plus'],
            'utils': ['axios', 'crypto-js', 'js-base64'],
          },
          // chunk文件命名格式
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
        },
      },
    }
  }
})