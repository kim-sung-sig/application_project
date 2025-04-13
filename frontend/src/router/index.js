import axios from "axios";
import { createRouter, createWebHistory } from 'vue-router';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

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

async function tryValidAccessToken() {
  const accessToken = localStorage.getItem("accessToken");
  if (!accessToken) return false;

  try {
    const response = await axios.post(`${apiBaseUrl}/api/v1/auth/token/validate`, {
      accessToken: accessToken
    });

    const { isValid } = response.data.data;

    return isValid;
  } catch (err) {
    console.error('refresh error', err);
    return false;
  }
}

async function tryRefreshToken() {
  const savedRefreshToken = localStorage.getItem("refreshToken");
  if (!savedRefreshToken) return false;

  try {
    const response = await axios.post(`${apiBaseUrl}/api/v1/auth/token/refresh`, {
      refreshToken: savedRefreshToken
    });

    const { accessToken, refreshToken } = response.data.data;
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

  if (isPublicPath) return next();

  // 인증 경로
  const accessToken = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  if (accessToken) {
    const isValid = await tryValidAccessToken();
    if (isValid) return next();
  }

  if (refreshToken) {
    const refreshed = await tryRefreshToken();
    if (refreshed) return next();
  }

  return next("/auth/signin");
});

export default router
