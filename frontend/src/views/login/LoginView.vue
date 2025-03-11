<template>
<div>
    <h1>Login</h1>
    <form @submit.prevent="handleLogin">
        <input v-model="username" type="text" placeholder="Username" required />
        <input v-model="password" type="password" placeholder="Password" required />
        <button type="submit">Login</button>
    </form>
</div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            username: "",
            password: "",
        };
    },

    methods: {
        async handleLogin() {
            try {
                const response = await axios.post("/api/v1/auth/token", {
                    username: this.username,
                    password: this.password,
                });

                const {
                    accessToken,
                    refreshToken
                } = response.data;

                // 토큰을 localStorage에 저장
                localStorage.setItem("accessToken", accessToken);
                localStorage.setItem("refreshToken", refreshToken);

                this.$router.push("/");
            } catch (error) {
                error.status()
                console.error("Login failed", error);
                alert("Login failed!");
            }
        },
        // refreshToken으로 accessToken을 갱신하는 메서드
        async refreshAccessToken() {
            const refreshToken = localStorage.getItem("refreshToken");
            if (refreshToken) {
                try {
                    const response = await axios.post("/api/v1/auth/refresh",
                        {
                            refreshToken,
                        }
                    );

                    const {
                        accessToken
                    } = response.data;

                    // 갱신된 accessToken을 localStorage에 저장
                    localStorage.setItem("accessToken", accessToken);

                    // 토큰 갱신 후 재시도
                    return true;
                } catch (error) {
                    console.error("Refresh token failed", error);
                    return false;
                }
            }
            return false;
        },
    },
    mounted() {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
            this.$router.push("/login"); // 로그인 페이지로 리디렉션
        }
        else {
            // refreshToken이 존재하면 accessToken 갱신 시도
            this.refreshAccessToken().then((success) => {
                if (!success) {
                    this.$router.push("/login");
                }
            });
        }
    },
};
</script>
