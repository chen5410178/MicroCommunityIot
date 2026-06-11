import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layouts/mainLayout.vue'
import devRouter from './devRouter'
import adminRouter from './adminRouter'
import accessControlRouter from './accessControlRouter'
import propertyRouter from './propertyRouter'

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
    children: [
      {
        path: '',
        redirect: '/pages/index/index'
      },
      {
        path: 'pages/index/index',
        name: 'Dashboard',
        component: () => import('@/views/index/dashboard.vue'),
        meta: { requiresAuth: true, title: '首页' }
      },
      ...devRouter,
      ...adminRouter,
      ...accessControlRouter,
      ...propertyRouter,
      {
        path: 'pages/frame/systemInfoManage',
        name: 'SystemInfoManage',
        component: () => import('@/views/frame/systemInfoManage.vue'),
        meta: { requiresAuth: true, title: '系统配置' }
      },
      {
        path: 'pages/frame/changeStaffPwd',
        name: 'ChangeStaffPwd',
        component: () => import('@/views/frame/changeStaffPwd.vue'),
        meta: { requiresAuth: true, title: '修改密码' }
      },
      {
        path: 'pages/frame/registerProtocol',
        name: 'RegisterProtocol',
        component: () => import('@/views/frame/registerProtocol/registerProtocol.vue'),
        meta: { requiresAuth: true, title: '注册协议' }
      },
      {
        path: 'pages/log/mqttLog',
        name: 'MqttLog',
        component: () => import('@/views/log/mqttLog.vue'),
        meta: { requiresAuth: true, title: 'mqtt日志' }
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
    if (err.name !== 'NavigationDuplicated' && err.name !== 'NavigationRedirected') throw err
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
