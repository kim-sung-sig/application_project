<template>
  <div>
    <h2 class="uk-text-center uk-margin-bottom">회원가입</h2>
    <form>
      <div class="uk-margin">
        <input class="uk-input" type="text" placeholder="이름" />
      </div>
      <div class="uk-margin">
        <input class="uk-input" type="email" placeholder="이메일" />
      </div>
      <div class="uk-margin">
        <input class="uk-input" type="password" placeholder="비밀번호" />
      </div>
      <button class="uk-button uk-button-success uk-width-1-1">회원가입</button>
    </form>
    <p class="uk-text-center uk-margin-top">
      이미 계정이 있으신가요?
      <router-link to="/auth/signin" class="uk-link-text">로그인</router-link>
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },

  methods: {},
  mounted() {},
};
</script>
