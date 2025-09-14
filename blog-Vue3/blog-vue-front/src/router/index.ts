// 明确区分类型导入和值导入
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw, Router } from 'vue-router' // 类型使用 type-only import

// 自动加载路由模块 (使用类型断言)
const moduleRoutes = import.meta.glob<{
  default: RouteRecordRaw[]
}>('./modules/**/*.ts', { eager: true })

const routes: RouteRecordRaw[] = []
Object.values(moduleRoutes).forEach((module) => {
  routes.push(...module.default)
})

// 创建路由实例
const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})


export default router
