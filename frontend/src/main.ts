import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router/index'
import store from './store/index.js'  // 确保路径正确
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 按需导入 Ant Design Vue 组件
import Empty from 'ant-design-vue/es/empty'
import Avatar from 'ant-design-vue/es/avatar'
import Tooltip from 'ant-design-vue/es/tooltip'
import message from 'ant-design-vue/es/message'
import Modal from 'ant-design-vue/es/modal'

// 修复 Ant Design Vue 样式导入
// 对于 Ant Design Vue 4.x
import 'ant-design-vue/dist/reset.css'
// 或者对于 Ant Design Vue 3.x 及以下版本
// import 'ant-design-vue/dist/antd.min.css'

const app = createApp(App)

// 注册需要的组件
app.use(Empty)
app.use(Avatar)
app.use(Tooltip)
app.use(Modal)

// 全局挂载 message 方法
app.config.globalProperties.$message = message

// 确保正确的注册顺序
app.use(ElementPlus)
app.use(store)  // 确保在 router 之前注册 store
app.use(router)

app.mount('#app')