import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import request from './utils/request'
import { Auth } from './utils/auth'

Vue.use(ElementUI)
Vue.config.productionTip = false

// 将axios请求方法挂载到Vue原型上，方便在组件中使用
Vue.prototype.$http = request

// 将Auth对象挂载到Vue原型上，方便在组件中使用
Vue.prototype.$auth = Auth

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
