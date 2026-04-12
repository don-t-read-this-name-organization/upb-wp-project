import { createRouter, createWebHistory } from 'vue-router'
import { getAccessToken } from '@/utils/apiClient'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/news',
      name: 'news-public',
      component: () => import('../views/NewsPageView.vue'),
    },
    {
      path: '/links',
      name: 'links',
      component: () => import('../views/LinksView.vue'),
    },
    {
      path: '/professors',
      name: 'professors',
      component: () => import('../views/ProfessorsView.vue'),
    },
    {
      path: '/kanban',
      name: 'kanban',
      component: () => import('../views/KanbanView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/quotes',
      name: 'quotes',
      component: () => import('../views/QuotesView.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/news',
      name: 'news',
      component: () => import('../views/NewsView.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/timetable',
      name: 'timetable',
      component: () => import('../views/TimetableView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('../views/NotesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/files',
      name: 'files',
      component: () => import('../views/FilesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  const isLoggedIn = !!user && !!getAccessToken()
  const isAdmin = user?.role === 'ADMIN'

  if (isAdmin && to.path === '/') {
    next('/admin')
  } else if (to.meta.requiresAdmin) {
    if (!isAdmin) {
      next(isLoggedIn ? '/' : '/login')
    } else {
      next()
    }
  } else if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
