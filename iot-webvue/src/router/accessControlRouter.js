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
    path: 'pages/accessControl/visitDetail',
    name: 'VisitDetail',
    component: () => import('@/views/accessControl/listVisitDetail.vue'),
    meta: { requiresAuth: true, title: '访客详情' }
  },
  {
    path: 'pages/accessControl/accessControlAd',
    name: 'AccessControlAd',
    component: () => import('@/views/accessControl/listAccessControlAd.vue'),
    meta: { requiresAuth: true, title: '广告' }
  },
  {
    path: 'pages/accessControl/accessControlAdRel',
    name: 'AccessControlAdRel',
    component: () => import('@/views/accessControl/accessControlAdRel.vue'),
    meta: { requiresAuth: true, title: '广告门禁' }
  },
  {
    path: 'pages/accessControl/callingPlatformManage',
    name: 'CallingPlatformManage',
    component: () => import('@/views/accessControl/callingPlatformManage.vue'),
    meta: { requiresAuth: true, title: '云呼平台' }
  },
  {
    path: 'pages/accessControl/callingDeviceManage',
    name: 'CallingDeviceManage',
    component: () => import('@/views/accessControl/callingDeviceManage.vue'),
    meta: { requiresAuth: true, title: '呼叫设备' }
  },
  {
    path: 'pages/accessControl/callingContactManage',
    name: 'CallingContactManage',
    component: () => import('@/views/accessControl/callingContactManage.vue'),
    meta: { requiresAuth: true, title: '通讯录' }
  },
  {
    path: 'pages/accessControl/intercomLog',
    name: 'IntercomLog',
    component: () => import('@/views/accessControl/listIntercomLog.vue'),
    meta: { requiresAuth: true, title: '呼叫记录' }
  }
]

export default accessControlRoutes
