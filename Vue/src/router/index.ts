// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router/auto'
import { routes } from 'vue-router/auto-routes'
import { useAuth } from '~/composables/useAuth'

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局前置守卫
router.beforeEach(async (to) => {
  
  const { checkAuth } = useAuth()
  
  if (to.meta.requiresAuth) {
    const isAuthenticated = await checkAuth()
    if (!isAuthenticated) {
      return {
        path: '/login',
        query: { redirect: to.fullPath }
      }
    }
  }
})

export default router