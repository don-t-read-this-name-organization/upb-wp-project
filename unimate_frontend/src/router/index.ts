import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
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
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
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
      path: '/timetable',
      name: 'timetable',
      component: () => import('../views/TimetableView.vue'),
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('../views/NotesView.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    if (user) {
      next('/')
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
