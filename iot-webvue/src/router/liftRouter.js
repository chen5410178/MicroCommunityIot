const liftRoutes = [
  {
    path: 'pages/lift/liftMachineAc',
    name: 'LiftMachineAc',
    component: () => import('@/views/lift/liftMachineAc.vue'),
    meta: { requiresAuth: true, title: '电梯门禁' }
  },
  {
    path: 'pages/lift/liftMachineDetail',
    name: 'LiftMachineDetail',
    component: () => import('@/views/lift/liftMachineDetail.vue'),
    meta: { requiresAuth: true, title: '电梯详情' }
  },
  {
    path: 'pages/lift/liftWarn',
    name: 'LiftWarn',
    component: () => import('@/views/lift/liftWarn.vue'),
    meta: { requiresAuth: true, title: '电梯告警' }
  },
  {
    path: 'pages/lift/liftCameraManage',
    name: 'LiftCameraManage',
    component: () => import('@/views/lift/liftCamera.vue'),
    meta: { requiresAuth: true, title: '电梯相机' }
  },
  {
    path: 'pages/lift/liftMachineReservation',
    name: 'LiftMachineReservation',
    component: () => import('@/views/lift/liftMachineReservation.vue'),
    meta: { requiresAuth: true, title: '电梯预约' }
  },
  {
    path: 'pages/lift/liftMachineLogManage',
    name: 'LiftMachineLogManage',
    component: () => import('@/views/lift/liftMachineLogManage.vue'),
    meta: { requiresAuth: true, title: '电梯日志' }
  },
  {
    path: 'pages/lift/liftMachineManage',
    name: 'LiftMachineManage',
    component: () => import('@/views/lift/liftMachineManage.vue'),
    meta: { requiresAuth: true, title: '电梯设备' }
  },
  {
    path: 'pages/lift/liftRideLog',
    name: 'LiftRideLog',
    component: () => import('@/views/lift/liftRideLog.vue'),
    meta: { requiresAuth: true, title: '乘梯记录' }
  },
  {
    path: 'pages/lift/liftCamera',
    name: 'LiftCamera',
    component: () => import('@/views/lift/liftCamera.vue'),
    meta: { requiresAuth: true, title: '电梯相机' }
  }
]

export default liftRoutes
