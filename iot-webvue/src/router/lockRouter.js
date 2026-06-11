const lockRoutes = [
  {
    path: 'pages/lock/lockMachineFactoryManage',
    name: 'LockMachineFactoryManage',
    component: () => import('@/views/lock/lockMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '门锁厂家' }
  },
  {
    path: 'pages/lock/lockMachineFactorySpecManage',
    name: 'LockMachineFactorySpecManage',
    component: () => import('@/views/lock/lockMachineFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '门锁厂家规格' }
  },
  {
    path: 'pages/lock/lockLogManage',
    name: 'LockLogManage',
    component: () => import('@/views/lock/lockLogManage.vue'),
    meta: { requiresAuth: true, title: '下发记录' }
  }
]

export default lockRoutes
