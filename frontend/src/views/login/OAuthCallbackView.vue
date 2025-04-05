<template>
  <div>
    <h1>OAuth Callback...</h1>
  </div>
</template>

<script setup>
import axios from "axios";
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

onMounted(async () => {
  const code = route.query.code;
  const state = route.query.state;
  const provider = route.params.provider;

  if (!code || !provider) {
    alert("OAuth 인가에 필요한 정보가 없습니다.");
    return router.push("/");
  }

  try {
    // 토큰 발급 요청
    const response = await axios.post("http://localhost:18081/api/v1/auth/oauth/login", {
      provider: provider,
      code: code,
      state: state,
    });

    // 토큰 정보 추출
    const { accessToken, refreshToken } = response.data;

    // 토큰 저장
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    // 홈으로 리디렉션
    router.push("/");
  } catch (e) {
    console.error(e);
    alert("로그인에 실패했습니다.");
    /*
     * 소셜 로그인 실패 시 처리
     */
    router.push("/");
  }
});
</script>
