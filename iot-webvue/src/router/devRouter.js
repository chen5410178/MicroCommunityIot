const devRoutes = [
  {
    path: 'pages/dev/appManage',
    name: 'AppManage',
    component: () => import('@/views/dev/appManage.vue'),
    meta: { requiresAuth: true, title: '应用信息' }
  },
  {
    path: 'pages/dev/menuCatalogManage',
    name: 'MenuCatalogManage',
    component: () => import('@/views/dev/menuCatalogManage.vue'),
    meta: { requiresAuth: true, title: '菜单目录' }
  },
  {
    path: 'pages/dev/menuGroupManage',
    name: 'MenuGroupManage',
    component: () => import('@/views/dev/menuGroupManage.vue'),
    meta: { requiresAuth: true, title: '菜单组' }
  },
  {
    path: 'pages/dev/mappingDomainManage',
    name: 'MappingDomainManage',
    component: () => import('@/views/dev/mappingDomainManage.vue'),
    meta: { requiresAuth: true, title: '配置项' }
  },
  {
    path: 'pages/dev/mappingManage',
    name: 'MappingManage',
    component: () => import('@/views/dev/mappingManage.vue'),
    meta: { requiresAuth: true, title: '编码映射' }
  },
  {
    path: 'pages/dev/serviceManage',
    name: 'ServiceManage',
    component: () => import('@/views/dev/serviceManage.vue'),
    meta: { requiresAuth: true, title: '服务信息' }
  },
  {
    path: 'pages/dev/serviceRegisterManage',
    name: 'ServiceRegisterManage',
    component: () => import('@/views/dev/serviceRegisterManage.vue'),
    meta: { requiresAuth: true, title: '服务注册' }
  },
  {
    path: 'pages/dev/addServiceBind',
    name: 'AddServiceBind',
    component: () => import('@/views/dev/addServiceBind.vue'),
    meta: { requiresAuth: true, title: '服务绑定' }
  },
  {
    path: 'pages/dev/serviceImplManage',
    name: 'ServiceImplManage',
    component: () => import('@/views/dev/serviceImplManage.vue'),
    meta: { requiresAuth: true, title: '服务实现' }
  },
  {
    path: 'pages/dev/serviceProvideManage',
    name: 'ServiceProvideManage',
    component: () => import('@/views/dev/serviceProvideManage.vue'),
    meta: { requiresAuth: true, title: '服务提供' }
  },
  {
    path: 'pages/dev/menuManage',
    name: 'MenuManage',
    component: () => import('@/views/dev/menuManage.vue'),
    meta: { requiresAuth: true, title: '菜单维护' }
  },
  {
    path: 'pages/dev/configMenu',
    name: 'ConfigMenuManage',
    component: () => import('@/views/dev/configMenu.vue'),
    meta: { requiresAuth: true, title: '配置菜单' }
  },
  {
    path: 'pages/dev/basePrivilegeManage',
    name: 'BasePrivilegeManage',
    component: () => import('@/views/dev/basePrivilegeManage.vue'),
    meta: { requiresAuth: true, title: '基础权限' }
  },
  {
    path: 'pages/dev/dictManage',
    name: 'DictManage',
    component: () => import('@/views/dev/dictManage.vue'),
    meta: { requiresAuth: true, title: '字典管理' }
  },
  {
    path: 'pages/dev/attrSpecManage',
    name: 'AttrSpecManage',
    component: () => import('@/views/dev/attrSpecManage.vue'),
    meta: { requiresAuth: true, title: '属性管理' }
  },
  {
    path: 'pages/dev/attrValueManage',
    name: 'AttrValueManage',
    component: () => import('@/views/dev/attrValueManage.vue'),
    meta: { requiresAuth: true, title: '属性值管理' }
  },
  {
    path: 'pages/dev/reportCustomGroupManage',
    name: 'ReportCustomGroupManage',
    component: () => import('@/views/dev/reportCustomGroupManage.vue'),
    meta: { requiresAuth: true, title: '报表组' }
  },
  {
    path: 'pages/dev/reportCustomManage',
    name: 'ReportCustomManage',
    component: () => import('@/views/dev/reportCustomManage.vue'),
    meta: { requiresAuth: true, title: '报表信息' }
  },
  {
    path: 'pages/dev/reportCustomComponentManage',
    name: 'ReportCustomComponentManage',
    component: () => import('@/views/dev/reportCustomComponentManage.vue'),
    meta: { requiresAuth: true, title: '报表组件' }
  },
  {
    path: 'pages/dev/cacheManage',
    name: 'CacheManage',
    component: () => import('@/views/dev/cacheManage.vue'),
    meta: { requiresAuth: true, title: '刷新缓存' }
  },
  {
    path: 'pages/dev/jobManage',
    name: 'JobManage',
    component: () => import('@/views/dev/jobManage.vue'),
    meta: { requiresAuth: true, title: '定时任务' }
  },
  {
    path: 'pages/dev/businessTableHisManage',
    name: 'BusinessTableHisManage',
    component: () => import('@/views/dev/businessTableHisManage.vue'),
    meta: { requiresAuth: true, title: '业务轨迹' }
  },
  {
    path: 'pages/dev/businessDatabusManage',
    name: 'BusinessDatabusManage',
    component: () => import('@/views/dev/businessDatabusManage.vue'),
    meta: { requiresAuth: true, title: '业务数据总线' }
  },
  {
    path: 'pages/dev/transactionLogManage',
    name: 'TransactionLogManage',
    component: () => import('@/views/dev/transactionLogManage.vue'),
    meta: { requiresAuth: true, title: '交互日志' }
  },
  {
    path: 'pages/dev/initializeCommunityManage',
    name: 'InitializeCommunityManage',
    component: () => import('@/views/dev/initializeCommunityManage.vue'),
    meta: { requiresAuth: true, title: '初始化小区' }
  },
  {
    path: 'pages/dev/addServiceBind',
    name: 'AddServiceBind',
    component: () => import('@/views/dev/addServiceBind.vue'),
    meta: { requiresAuth: true, title: '服务绑定' }
  }
]

export default devRoutes
