import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect : '/auth/signin',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/about',
    component: () => import('@/views/AboutView.vue'),
  },
  {
    path: '/auth',
    component: () => import('@/views/login/AuthView.vue'),
    children: [
      {
        path: 'signin',
        component: () => import('@/views/login/SignInView.vue')
      },
      {
        path: 'signup',
        component: () => import('@/views/login/SignUpView.vue')
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
