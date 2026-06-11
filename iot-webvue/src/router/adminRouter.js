const adminRoutes = [
  {
    path: 'pages/common/cityAreaManage',
    name: 'CityAreaManage',
    component: () => import('@/views/common/cityAreaManage.vue'),
    meta: { requiresAuth: true, title: '地区管理' }
  },
  {
    path: 'pages/common/communityManage',
    name: 'CommunityManage',
    component: () => import('@/views/common/communityManage.vue'),
    meta: { requiresAuth: true, title: '小区管理' }
  },
  {
    path: 'pages/admin/propertyCompanyManage',
    name: 'PropertyCompanyManage',
    component: () => import('@/views/admin/listPropertyCompany.vue'),
    meta: { requiresAuth: true, title: '物业公司管理' }
  },
  {
    path: 'pages/admin/propertyCommunityManage',
    name: 'PropertyCommunityManage',
    component: () => import('@/views/admin/listPropertyCommunity.vue'),
    meta: { requiresAuth: true, title: '管理小区' }
  },
  {
    path: 'pages/lift/liftMachineFactoryManage',
    name: 'LiftMachineFactoryManage',
    component: () => import('@/views/lift/liftMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '电梯厂家' }
  },
  {
    path: 'pages/lift/liftMachineFactorySpecManage',
    name: 'LiftMachineFactorySpecManage',
    component: () => import('@/views/lift/liftMachineFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '电梯厂家规格' }
  },
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
    path: 'pages/meter/meterMachineFactoryManage',
    name: 'MeterMachineFactoryManage',
    component: () => import('@/views/meter/meterMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '水电厂家' }
  },
  {
    path: 'pages/meter/meterMachineFactorySpecManage',
    name: 'MeterMachineFactorySpecManage',
    component: () => import('@/views/meter/meterMachineFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '水电厂家规格' }
  },
  {
    path: 'pages/meter/instrumentFactoryManage',
    name: 'InstrumentFactoryManage',
    component: () => import('@/views/meter/listInstrumentFactoryManage.vue'),
    meta: { requiresAuth: true, title: '仪表厂家' }
  },
  {
    path: 'pages/meter/instrumentFactorySpecManage',
    name: 'InstrumentFactorySpecManage',
    component: () => import('@/views/meter/listInstrumentFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '厂家规格' }
  },
  {
    path: 'pages/charge/chargeMachineFactoryManage',
    name: 'ChargeMachineFactoryManage',
    component: () => import('@/views/charge/chargeMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '充电厂家' }
  },
  {
    path: 'pages/charge/chargeMachineFactorySpecManage',
    name: 'ChargeMachineFactorySpecManage',
    component: () => import('@/views/charge/chargeMachineFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '充电厂家规格' }
  },
  {
    path: 'pages/charge/listWorkLicenseFactory',
    name: 'ListWorkLicenseFactory',
    component: () => import('@/views/charge/listWorkLicenseFactory.vue'),
    meta: { requiresAuth: true, title: '工牌厂家' }
  },
  {
    path: 'pages/3d/dtModal',
    name: 'DtModalManage',
    component: () => import('@/views/3d/listDtModal.vue'),
    meta: { requiresAuth: true, title: '3D模型管理' }
  }
]

export default adminRoutes
