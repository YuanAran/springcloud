import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/Home.vue'
import About from '@/views/About.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import UserManagement from '@/views/UserManagement.vue'
import Dashboard from '@/views/Dashboard.vue'
import ChangePassword from '@/views/ChangePassword.vue'
import {getToken} from "@/utils/auth";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true },
        redirect: '/home/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard,
                meta: { requiresAuth: true }
            },
            {
                path: 'user-management',
                name: 'UserManagement',
                component: UserManagement,
                meta: { requiresAuth: true }
            },
            {
                path: 'about',
                name: 'AboutSystem',
                component: About,
                meta: { requiresAuth: true }
            },
            {
                path: 'change-password',
                name: 'ChangePassword',
                component: ChangePassword,
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = getToken()

    // 如果没有token且不是登录或注册页面，跳转到登录页
    if (!token && to.path !== '/login' && to.path !== '/register') {
        next('/login')
        return
    }

    // 如果有token且访问登录或注册页面，跳转到用户管理页面
    if (token && (to.path === '/login' || to.path === '/register')) {
        next('/home/dashboard')
        return
    }

    // 其他情况正常跳转
    next()
})

export default router
