import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
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
      },
    ]
  },
  {
    path: '/login/oauth2/code/:provider',
    component: () => import('@/views/login/OAuthCallbackView.vue')
  },
]

async function tryRefreshToken() {
  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) return false;

  try {
    const response = await fetch('/api/v1/auth/token/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken })
    });

    if (!response.ok) return false;

    const { accessToken, refreshToken } = response.data;
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    return true;

  } catch (err) {
    console.error('refresh error', err);
    return false;
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const isPublicPath = to.path.startsWith("/auth") || to.path.startsWith("/login/oauth2/code");

  if (isPublicPath) {
    return next();
  }

  // 인증 경로
  const accessToken = localStorage.getItem("accessToken");

  if (!accessToken) {
    const refreshed = await tryRefreshToken();
    if (!refreshed) {
      return next("/auth/signin");
    }
  }

  next();
});

export default router
