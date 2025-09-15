import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入 Element Plus 的暗黑主题
import "element-plus/theme-chalk/dark/css-vars.css";
// 自定义css
import "@/assets/css/iconFont/iconfont.css";


// svg
import "virtual:svg-icons-register";

// 指令
import vCopy from "./directives/copy";
import image from "./directives/imageLoading";
const app = createApp(App)
const pinia = createPinia()

app.directive("copy", vCopy);
app.directive("image", image);

app.use(pinia)
app.use(router)
// 全局注册不需要再组件内重复引入
app.use(ElementPlus)

app.mount('#app')
