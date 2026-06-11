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
  }
]

export default liftRoutes
