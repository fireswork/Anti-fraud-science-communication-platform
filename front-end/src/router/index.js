import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Post from '@/views/Post.vue'
import Read from '@/views/Read.vue'
import Map from '@/views/MapView.vue'
import About from '@/views/About.vue'
import Report from '@/views/Report.vue'
import Center from '@/views/Center.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import Users from '@/views/Users.vue'
import Videos from '@/views/Videos.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
    },
    {
      path: '/post',
      name: 'post',
      component: Post,
    },
    {
      path: '/read',
      name: 'read',
      component: Read,
    },
    {
      path: '/map',
      name: 'map',
      component: Map,
    },
    {
      path: '/about',
      name: 'about',
      component: About,
    },
    {
      path: '/report',
      name: 'report',
      component: Report,
    },
    {
      path: '/center',
      name: 'center',
      component: Center,
    },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        {
          path: 'users',
          name: 'users',
          component: Users,
        },
        {
          path: 'videos',
          name: 'videos',
          component: Videos,
        },
      ],
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 从localStorage获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const isLoggedIn = !!userInfo.userId // 检查用户是否登录
  const isAdmin = userInfo.role === 'ADMIN' // 检查用户是否是管理员

  // 管理员页面需要管理员权限
  if (to.path.startsWith('/admin') && !isAdmin) {
    if (!isLoggedIn) {
      // 未登录用户重定向到登录页
      next('/login')
    } else {
      // 非管理员用户重定向到首页
      next('/')
    }
  } 
  // 登录页面重定向逻辑 - 已登录用户访问登录页
  else if (to.path === '/login' && isLoggedIn) {
    if (isAdmin) {
      // 管理员重定向到管理页面
      next('/admin/users')
    } else {
      // 普通用户重定向到首页
      next('/')
    }
  } 
  else {
    next()
  }
})

export default router
