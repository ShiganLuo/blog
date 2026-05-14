import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { initApp } from './app-init'
// Element Plus暗黑主题CSS（按需引入插件会自动处理组件样式）
import 'element-plus/theme-chalk/dark/css-vars.css'
// 自定义css
import '@/assets/css/iconFont/iconfont.css'

// svg
import 'virtual:svg-icons-register'

// 指令
import vCopy from './directives/copy'
import image from './directives/imageLoading'

const app = createApp(App)
const pinia = createPinia()

app.directive('copy', vCopy)
app.directive('image', image)

app.use(pinia)
app.use(router)

app.mount('#app')
initApp()
