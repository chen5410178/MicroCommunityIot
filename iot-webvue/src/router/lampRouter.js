const lampRoutes = [
  {
    path: 'pages/lamp/lampMachineFactoryManage',
    name: 'LampMachineFactoryManage',
    component: () => import('@/views/lamp/listLampMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '灯杆设备厂商' }
  },
  {
    path: 'pages/lamp/listLampMachineFactorySpecManage',
    name: 'ListLampMachineFactorySpecManage',
    component: () => import('@/views/lamp/listLampMachineFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '灯杆厂家规格' }
  },
  {
    path: 'pages/lamp/telCallLog',
    name: 'TelCallLog',
    component: () => import('@/views/lamp/listTelCallLog.vue'),
    meta: { requiresAuth: true, title: '呼叫记录' }
  },
  {
    path: 'pages/lamp/telMachine',
    name: 'TelMachine',
    component: () => import('@/views/lamp/listTelMachine.vue'),
    meta: { requiresAuth: true, title: '话机管理' }
  },
  {
    path: 'pages/lamp/telMachineUser',
    name: 'TelMachineUser',
    component: () => import('@/views/lamp/telMachineUser.vue'),
    meta: { requiresAuth: true, title: '话机客服' }
  },
  {
    path: 'pages/lamp/telSmsLog',
    name: 'TelSmsLog',
    component: () => import('@/views/lamp/listTelSmsLog.vue'),
    meta: { requiresAuth: true, title: '短信记录' }
  },
  {
    path: 'pages/lamp/telTts',
    name: 'TelTts',
    component: () => import('@/views/lamp/listTelTts.vue'),
    meta: { requiresAuth: true, title: '话机语音' }
  }
]

export default lampRoutes
