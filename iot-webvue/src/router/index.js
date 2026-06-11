import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layouts/mainLayout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/pages/frame/login',
    name: 'Login',
    component: () => import('@/views/frame/login/login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/pages/index/index',
    children: [
      {
        path: 'pages/index/index',
        name: 'Dashboard',
        component: () => import('@/views/index/dashboard.vue'),
        meta: { requiresAuth: true, title: '首页' }
      },
      {
        path: '*',
        component: () => import('@/views/index/placeholder.vue'),
        meta: { requiresAuth: true, title: '' }
      }
    ]
  },
  {
    path: '*',
    redirect: '/pages/index/index'
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') throw err
  })
}

const whiteList = ['/pages/frame/login']

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '小区物联网系统'
  if (whiteList.indexOf(to.path) !== -1) {
    next()
    return
  }
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userInfo = sessionStorage.getItem('iot_webvue_user')
    if (!userInfo) {
      next({ path: '/pages/frame/login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
