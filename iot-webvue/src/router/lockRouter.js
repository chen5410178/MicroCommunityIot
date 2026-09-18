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
  },
  {
    path: 'pages/lock/lockMachineManage',
    name: 'LockMachineManage',
    component: () => import('@/views/lock/lockMachineManage.vue'),
    meta: { requiresAuth: true, title: '门锁设备' }
  },
  {
    path: 'pages/lock/lockMachineDetail',
    name: 'LockMachineDetail',
    component: () => import('@/views/lock/lockMachineDetail.vue'),
    meta: { requiresAuth: true, title: '门锁详情' }
  },
  {
    path: 'pages/lock/lockInoutManage',
    name: 'LockInoutManage',
    component: () => import('@/views/lock/lockInoutManage.vue'),
    meta: { requiresAuth: true, title: '门锁进出记录' }
  },
  {
    path: 'pages/lock/lockPersonManage',
    name: 'LockPersonManage',
    component: () => import('@/views/lock/lockPersonManage.vue'),
    meta: { requiresAuth: true, title: '门锁人员' }
  },
  {
    path: 'pages/lock/addLockPerson',
    name: 'AddLockPerson',
    component: () => import('@/views/lock/addLockPerson.vue'),
    meta: { requiresAuth: true, title: '添加授权' }
  }
]

export default lockRoutes
