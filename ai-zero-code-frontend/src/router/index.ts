import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import UserManage from '../views/admin/UserManage.vue'
import UserLogin from '../views/user/UserLogin.vue'
import UserRegister from '../views/user/UserRegister.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLogin,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegister,
    },
  ],
})

export default router
