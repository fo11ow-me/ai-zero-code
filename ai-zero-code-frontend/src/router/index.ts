import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import UserManage from '../views/admin/UserManage.vue'
import AppManage from '../views/admin/AppManage.vue'
import ChatManage from '../views/admin/ChatManage.vue'
import UserLogin from '../views/user/UserLogin.vue'
import UserRegister from '../views/user/UserRegister.vue'
import AppChat from '../views/app/AppChat.vue'
import AppEdit from '../views/app/AppEdit.vue'

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
    {
      path: '/admin/appManage',
      name: '应用管理',
      component: AppManage,
    },
    {
      path: '/admin/chatManage',
      name: '对话管理',
      component: ChatManage,
    },
    {
      path: '/app/chat/:id',
      name: '应用对话',
      component: AppChat,
    },
    {
      path: '/app/edit/:id',
      name: '编辑应用',
      component: AppEdit,
    },
  ],
})

export default router
