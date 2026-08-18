import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layouts/mainLayout.vue'
import devRouter from './devRouter'
import adminRouter from './adminRouter'
import accessControlRouter from './accessControlRouter'
import propertyRouter from './propertyRouter'
import carRouter from './carRouter'
import chargeRouter from './chargeRouter'
import meterRouter from './meterRouter'
import liftRouter from './liftRouter'
import lampRouter from './lampRouter'
import monitorRouter from './monitorRouter'
import couponRouter from './couponRouter'
import lockRouter from './lockRouter'
import attendanceRouter from './attendanceRouter'
import systemRouter from './systemRouter'
import shopRouter from './shopRouter'

Vue.use(VueRouter)

const routes = [
  {
    path: '/pages/frame/login',
    name: 'Login',
      component: () => import('@/views/frame/login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/pages/frame/sso',
    name: 'SsoPage',
    component: () => import('@/views/frame/sso.vue'),
    meta: { title: 'SSO认证' }
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
        component: () => import('@/views/index/index.vue'),
        meta: { requiresAuth: true, title: '首页' }
      },
      ...devRouter,
      ...adminRouter,
      ...accessControlRouter,
      ...propertyRouter,
      ...carRouter,
      ...chargeRouter,
      ...meterRouter,
      ...liftRouter,
      ...lampRouter,
      ...monitorRouter,
      ...couponRouter,
      ...lockRouter,
      ...attendanceRouter,
      ...systemRouter,
      ...shopRouter,
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
        component: () => import('@/views/frame/registerProtocol.vue'),
        meta: { requiresAuth: true, title: '注册协议' }
      },
      {
        path: 'pages/log/mqttLog',
        name: 'MqttLog',
        component: () => import('@/views/log/mqttLog.vue'),
        meta: { requiresAuth: true, title: 'mqtt日志' }
      },
      {
        path: 'pages/log/assetImportLog',
        name: 'AssetImportLog',
        component: () => import('@/views/log/assetImportLog.vue'),
        meta: { requiresAuth: true, title: '批量导入日志' }
      },
      {
        path: 'pages/log/assetImportLogDetail',
        name: 'AssetImportLogDetail',
        component: () => import('@/views/log/assetImportLogDetail.vue'),
        meta: { requiresAuth: true, title: '导入日志详情' }
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

const whiteList = ['/pages/frame/login', '/pages/frame/sso']

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
