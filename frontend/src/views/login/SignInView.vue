<template>
  <div>
    <div id="login-logo">
      <div id="imgaa">
        <img src="@/assets/img/logo.svg" alt="" />
      </div>
    </div>
    <form @submit.prevent="handleLogin" id="login_form">
      <!-- username input -->
      <TextInput
        v-model="username"
        :id="'username'"
        :text="'Username'"
        :errorMessage="usernameError"
      />

      <!-- password input -->
      <TextInput
        v-model="password"
        :id="'password'"
        :text="'Password'"
        :type="'password'"
        :errorMessage="passwordError"
      />

      <!-- checker input-->
      <div v-show="overFail" id="failBox">
        <TextInput
          v-model="checker"
          :id="'checker'"
          :text="'인증 번호'"
          :value="checker"
          :disabled="true"
        />

        <TextInput
          v-model="checkerNum"
          :id="'checkerNum'"
          :text="'인증 번호 입력'"
          :errorMessage="checkerError"
        />
      </div>

      <button id="login-btn" class="uk-button" type="submit">Login</button>
    </form>

    <div id="find-wrap">
      <ul>
        <li><a>비밀번호 찾기</a></li>
        <li><a>아이디 찾기</a></li>
        <li>
          <router-link to="/auth/signup" class="uk-link-text">회원가입</router-link>
        </li>
      </ul>
    </div>

    <div id="social-login" style="display: flex; gap: 20px; align-items: center">
      <!-- 네이버 로그인 버튼 -->
      <button
        id="naver-login"
        @click="handleNaverLogin"
        style="
          background-color: #03c75a;
          color: white;
          border: none;
          border-radius: 4px;
          padding: 10px 20px;
          font-size: 16px;
          cursor: pointer;
          display: flex;
          align-items: center;
        "
      >
        <img src="" alt="N" style="width: 24px; height: 24px; margin-right: 10px" />
        네이버 로그인
      </button>

      <!-- 카카오 로그인 버튼 -->
      <button
        id="kakao-login"
        @click="handleKakaoLogin"
        style="
          background-color: #fee500;
          color: #000000;
          border: none;
          border-radius: 12px;
          padding: 10px 20px;
          font-size: 16px;
          cursor: pointer;
          display: flex;
          align-items: center;
        "
      >
        <img
          src="https://cdn.simpleicons.org/kakaotalk/FFCD00"
          alt="Kakao"
          style="width: 24px; height: 24px; margin-right: 10px"
        />
        카카오 로그인
      </button>
    </div>
  </div>
</template>

<script>
import TextInput from "@/components/input/TextInput.vue";
import axios from "axios";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

export default {
  components: {
    TextInput,
  },

  data() {
    return {
      username: "",
      usernameError: "",

      password: "",
      passwordError: "",

      checker: "",
      checkerNum: "",
      checkerError: "",

      overFail: false,
      isLocked: false,
    };
  },

  methods: {
    async handleLogin() {
      try {
        const response = await axios.post(`${apiBaseUrl}/api/v1/auth/token`, {
          username: this.username,
          password: this.password,
        });

        const { accessToken, refreshToken } = response.data;

        // 토큰을 localStorage에 저장
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        this.$router.push("/");
      } catch (error) {
        if (error.response && error.response.status === 403) {
          alert("비밀번호 실패횟수 초과로 계정이 잠겼습니다.");
        } else {
          alert("Login failed!");
        }
      }
    },

    // refreshToken으로 accessToken을 갱신하는 메서드
    async refreshAccessToken() {
      const refreshToken = localStorage.getItem("refreshToken");

      if (!refreshToken) return false;

      try {
        const response = await axios.post(`${apiBaseUrl}/api/v1/auth/token/refresh`, {
          refreshToken,
        });

        const { accessToken, refreshToken } = response.data;

        // 갱신된 accessToken을 localStorage에 저장
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        // 토큰 갱신 후 재시도
        return true;
      } catch (error) {
        console.error("Refresh token failed", error);
        return false;
      }
    },

    handleNaverLogin() {
      const clientId = "G012m6B6UnjMyJQjyeYv";
      const redirectUri = `${window.location.origin}/login/oauth2/code/naver`;
      const state = crypto.randomUUID();
      window.location.href = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&state=${state}`;
    },

    handleKakaoLogin() {
      const clientId = "3df24cab62d018e352caeb9654d23112";
      const redirectUri = `${window.location.origin}/login/oauth2/code/kakao`;
      const state = crypto.randomUUID();
      window.location.href = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&state=${state}`;
    },
  },
  mounted() {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) {
      // this.$router.push("/auth/signin"); // 로그인 페이지로 리디렉션
    } else {
      // refreshToken이 존재하면 accessToken 갱신 시도
      this.refreshAccessToken().then((success) => {
        if (!success) {
          this.$router.push("/auth/signin");
        } else {
          this.$router.push("/");
        }
      });
    }
  },
};
</script>

<style scoped>
#login-logo {
  display: flex;
  justify-content: center;
  align-items: center;
}

#imgaa {
  width: 100px;
  margin-bottom: 30px;
  font-size: 24px;
  text-align: center;
}

#login-form {
  width: 450px;
  padding: 40px 24px 24px 24px;
  border-radius: 10px;
  border: 1px solid #ccc;
}

.message {
  font-size: 12px;
  text-align: left;
  width: 100%;
  display: block;
  position: absolute;
  top: -17px;
  left: 0;
}

#login-btn {
  width: 100%;
  border-radius: 10px;
  font-weight: bold;
  background-color: #007bff;
  color: #fff;
}

#failBox {
  display: flex;
  gap: 5px;
}

#find-wrap {
  margin: 0 auto;
  text-align: center;
  padding: 24px 0px;
}

#find-wrap ul {
  display: flex;
  justify-content: center;
}
#find-wrap li {
  position: relative;
}

#find-wrap li:not(:first-child) {
  padding-left: 26px;
}

#find-wrap li:not(:first-child)::before {
  content: "";
  position: absolute;
  top: 6px;
  left: 12px;
  width: 1px;
  height: 12px;
  border-radius: 0.5px;
  background-color: #ccc;
}
</style>
