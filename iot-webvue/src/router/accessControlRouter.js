const accessControlRoutes = [
  {
    path: 'pages/accessControl/accessControlManage',
    name: 'AccessControlManage',
    component: () => import('@/views/accessControl/listAccessControlManage.vue'),
    meta: { requiresAuth: true, title: '门禁设备' }
  },
  {
    path: 'pages/accessControl/aAccessControlDetail',
    name: 'AAccessControlDetail',
    component: () => import('@/views/accessControl/aAccessControlDetail.vue'),
    meta: { requiresAuth: true, title: '门禁设备详情' }
  },
  {
    path: 'pages/accessControl/accessControlFace',
    name: 'AccessControlFace',
    component: () => import('@/views/accessControl/listAccessControlFace.vue'),
    meta: { requiresAuth: true, title: '门禁人员' }
  },
  {
    path: 'pages/accessControl/accessControlPerson',
    name: 'AccessControlPerson',
    component: () => import('@/views/accessControl/addAccessControlPerson.vue'),
    meta: { requiresAuth: true, title: '人员授权' }
  },
  {
    path: 'pages/accessControl/accessControlInout',
    name: 'AccessControlInout',
    component: () => import('@/views/accessControl/accessControlInout.vue'),
    meta: { requiresAuth: true, title: '进出记录' }
  },
  {
    path: 'pages/accessControl/visitTypeManage',
    name: 'VisitTypeManage',
    component: () => import('@/views/accessControl/listVisitType.vue'),
    meta: { requiresAuth: true, title: '访客类型' }
  },
  {
    path: 'pages/accessControl/accessControlLog',
    name: 'AccessControlLog',
    component: () => import('@/views/accessControl/listAccessControlLog.vue'),
    meta: { requiresAuth: true, title: '下发日志' }
  },
  {
    path: 'pages/accessControl/hardwareManufacturerAttrManage',
    name: 'HardwareManufacturerAttrManage',
    component: () => import('@/views/accessControl/listHardwareManufacturerAttr.vue'),
    meta: { requiresAuth: true, title: '门禁厂家属性' }
  },
  {
    path: 'pages/accessControl/accessControlOrg',
    name: 'AccessControlOrg',
    component: () => import('@/views/accessControl/listAccessControlOrg.vue'),
    meta: { requiresAuth: true, title: '部门授权' }
  },
  {
    path: 'pages/accessControl/visitManage',
    name: 'VisitManage',
    component: () => import('@/views/accessControl/listVisitManage.vue'),
    meta: { requiresAuth: true, title: '访客人员' }
  },
  {
    path: 'pages/accessControl/addVisit',
    name: 'AddVisit',
    component: () => import('@/views/accessControl/addVisit.vue'),
    meta: { requiresAuth: true, title: '登记访客' }
  },
  {
    path: 'pages/accessControl/editVisit',
    name: 'EditVisit',
    component: () => import('@/views/accessControl/editVisit.vue'),
    meta: { requiresAuth: true, title: '修改访客' }
  },
  {
    path: 'pages/accessControl/visitDetail',
    name: 'VisitDetail',
    component: () => import('@/views/accessControl/listVisitDetail.vue'),
    meta: { requiresAuth: true, title: '访客详情' }
  }
]

export default accessControlRoutes
