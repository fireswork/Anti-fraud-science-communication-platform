import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Post from '@/views/Post.vue'
import Read from '@/views/Read.vue'
import Map from '@/views/MapView.vue'
import About from '@/views/About.vue'
import Report from '@/views/Report.vue'
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
  const token = localStorage.getItem('token')
  const isAdmin = localStorage.getItem('isAdmin') === 'true'

  if (to.meta.requiresAuth && !token) {
    // 如果需要认证但没有token，重定向到登录页
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    // 如果需要管理员权限但不是管理员，重定向到首页
    next('/')
  } else {
    next()
  }
})

export default router
