//import './assets/main.css';

import UIkit from 'uikit'; // JS 추가
import 'uikit/dist/css/uikit.min.css'; // CSS 추가
import Icons from 'uikit/dist/js/uikit-icons';
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// 전역 CSS 불러오기
import '@/assets/css/base.css'; // base.css 경로
import '@/assets/css/fonts.css'; // fonts.css 경로

// UIkit 아이콘 플러그인 로드
UIkit.use(Icons);

const app = createApp(App)

app.use(router)

app.mount('#app')
