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
    path: '/pages/frame/changeStaffPwd',
    name: 'ChangeStaffPwd',
    component: () => import('@/views/frame/changeStaffPwd.vue'),
    meta: { requiresAuth: true, title: '修改密码' }
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
        path: 'pages/dev/listAppManage',
        name: 'ListAppManage',
        component: () => import('@/views/dev/listAppManage.vue'),
        meta: { requiresAuth: true, title: '应用信息' }
      },
      {
        path: 'pages/dev/listMenuCatalogManage',
        name: 'MenuCatalogManage',
        component: () => import('@/views/dev/listMenuCatalogManage.vue'),
        meta: { requiresAuth: true, title: '菜单目录' }
      },
      {
        path: 'pages/dev/listMenuGroupManage',
        name: 'MenuGroupManage',
        component: () => import('@/views/dev/listMenuGroupManage.vue'),
        meta: { requiresAuth: true, title: '菜单组' }
      },
      {
        path: 'pages/dev/listMappingDomainManage',
        name: 'ListMappingDomainManage',
        component: () => import('@/views/dev/listMappingDomainManage.vue'),
        meta: { requiresAuth: true, title: '配置项' }
      },
      {
        path: 'pages/dev/businessTableHisManage',
        name: 'ListBusinessTableHisManage',
        component: () => import('@/views/dev/listBusinessTableHisManage.vue'),
        meta: { requiresAuth: true, title: '业务轨迹' }
      },
      {
        path: 'pages/dev/listServiceManage',
        name: 'ListServiceManage',
        component: () => import('@/views/dev/listServiceManage.vue'),
        meta: { requiresAuth: true, title: '服务信息' }
      },
      {
        path: 'pages/dev/listServiceRegisterManage',
        name: 'ListServiceRegisterManage',
        component: () => import('@/views/dev/listServiceRegisterManage.vue'),
        meta: { requiresAuth: true, title: '服务注册' }
      },
      {
        path: 'pages/dev/listServiceImplManage',
        name: 'ListServiceImplManage',
        component: () => import('@/views/dev/listServiceImplManage.vue'),
        meta: { requiresAuth: true, title: '服务实现' }
      },
      {
        path: 'pages/dev/listServiceProvideManage',
        name: 'ListServiceProvideManage',
        component: () => import('@/views/dev/listServiceProvideManage.vue'),
        meta: { requiresAuth: true, title: '服务提供' }
      },
      {
        path: 'pages/dev/listMenuManage',
        name: 'ListMenuManage',
        component: () => import('@/views/dev/listMenuManage.vue'),
        meta: { requiresAuth: true, title: '菜单维护' }
      },
      {
        path: 'pages/dev/listConfigMenuManage',
        name: 'ListConfigMenuManage',
        component: () => import('@/views/dev/listConfigMenuManage.vue'),
        meta: { requiresAuth: true, title: '配置菜单' }
      },
      {
        path: 'pages/dev/listBasePrivilegeManage',
        name: 'ListBasePrivilegeManage',
        component: () => import('@/views/dev/listBasePrivilegeManage.vue'),
        meta: { requiresAuth: true, title: '权限管理' }
      },
      {
        path: 'pages/dev/initializeCommunityManage',
        name: 'InitializeCommunityManage',
        component: () => import('@/views/dev/listInitializeCommunityManage.vue'),
        meta: { requiresAuth: true, title: '初始化小区' }
      },
      {
        path: 'pages/dev/listDictManage',
        name: 'ListDictManage',
        component: () => import('@/views/dev/listDictManage.vue'),
        meta: { requiresAuth: true, title: '字典管理' }
      },
      {
        path: 'pages/dev/businessDatabusManage',
        name: 'ListBusinessDatabusManage',
        component: () => import('@/views/dev/listBusinessDatabusManage.vue'),
        meta: { requiresAuth: true, title: '业务数据总线' }
      },
      {
        path: 'pages/dev/listMappingManage',
        name: 'ListMappingManage',
        component: () => import('@/views/dev/listMappingManage.vue'),
        meta: { requiresAuth: true, title: '编码映射' }
      },
      {
        path: 'pages/dev/listMappingDomainManage',
        name: 'ListMappingDomainManage',
        component: () => import('@/views/dev/listMappingDomainManage.vue'),
        meta: { requiresAuth: true, title: '配置项域' }
      },
      {
        path: 'pages/dev/listAttrSpecManage',
        name: 'ListAttrSpecManage',
        component: () => import('@/views/dev/listAttrSpecManage.vue'),
        meta: { requiresAuth: true, title: '属性管理' }
      },
      {
        path: 'pages/dev/listAttrValueManage',
        name: 'ListAttrValueManage',
        component: () => import('@/views/dev/listAttrValueManage.vue'),
        meta: { requiresAuth: true, title: '属性值管理' }
      },
      {
        path: 'pages/dev/listReportCustomGroupManage',
        name: 'ListReportCustomGroupManage',
        component: () => import('@/views/dev/listReportCustomGroupManage.vue'),
        meta: { requiresAuth: true, title: '报表组' }
      },
      {
        path: 'pages/dev/listReportCustomManage',
        name: 'ListReportCustomManage',
        component: () => import('@/views/dev/listReportCustomManage.vue'),
        meta: { requiresAuth: true, title: '报表信息' }
      },
      {
        path: 'pages/dev/listReportCustomComponentRelManage',
        name: 'ListReportCustomComponentRelManage',
        component: () => import('@/views/dev/listReportCustomComponentRelManage.vue'),
        meta: { requiresAuth: true, title: '报表组件关联' }
      },
      {
        path: 'pages/frame/systemInfoManage',
        name: 'SystemInfoManage',
        component: () => import('@/views/frame/systemInfoManage.vue'),
        meta: { requiresAuth: true, title: '系统配置' }
      },
      {
        path: 'pages/frame/registerProtocol',
        name: 'RegisterProtocol',
        component: () => import('@/views/frame/registerProtocol.vue'),
        meta: { requiresAuth: true, title: '注册协议' }
      },
      {
        path: 'pages/dev/listCacheManage',
        name: 'ListCacheManage',
        component: () => import('@/views/dev/listCacheManage.vue'),
        meta: { requiresAuth: true, title: '刷新缓存' }
      },
      {
        path: 'pages/dev/listJobManage',
        name: 'JobManage',
        component: () => import('@/views/dev/listJobManage.vue'),
        meta: { requiresAuth: true, title: '定时任务' }
      },
      {
        path: 'pages/dev/transactionLogManage',
        name: 'TransactionLogManage',
        component: () => import('@/views/dev/listTransactionLogManage.vue'),
        meta: { requiresAuth: true, title: '交互日志' }
      },
      {
        path: 'pages/frame/registerProtocol',
        name: 'RegisterProtocol',
        component: () => import('@/views/frame/registerProtocol/registerProtocol.vue'),
        meta: { requiresAuth: true, title: '注册协议' }
      },
      {
        path: 'pages/log/listMqttLog',
        name: 'ListMqttLog',
        component: () => import('@/views/log/listMqttLog.vue'),
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
