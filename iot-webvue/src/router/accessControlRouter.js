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
    path: 'pages/accessControl/accessControlPerson',
    name: 'AccessControlPerson',
    component: () => import('@/views/accessControl/addAccessControlPerson.vue'),
    meta: { requiresAuth: true, title: '人员授权' }
  },
  {
    path: 'pages/accessControl/accessControlInout',
    name: 'AccessControlInout',
    component: () => import('@/views/accessControl/listAdminInout.vue'),
    meta: { requiresAuth: true, title: '进出记录' }
  }
]

export default accessControlRoutes
