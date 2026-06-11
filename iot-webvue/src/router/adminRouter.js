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
  },
  {
    path: 'pages/meter/adminMeterRecharge',
    name: 'AdminMeterRecharge',
    component: () => import('@/views/meter/listAdminMeterRecharge.vue'),
    meta: { requiresAuth: true, title: '水电充值' }
  },
  {
    path: 'pages/charge/adminChargeOrder',
    name: 'AdminChargeOrder',
    component: () => import('@/views/charge/listAdminChargeOrder.vue'),
    meta: { requiresAuth: true, title: '充电订单' }
  },
  {
    path: 'pages/charge/adminChargeOrderAccts',
    name: 'AdminChargeOrderAccts',
    component: () => import('@/views/charge/listAdminChargeOrderAccts.vue'),
    meta: { requiresAuth: true, title: '扣款明细' }
  },
  {
    path: 'pages/accessControl/adminInout',
    name: 'AdminInout',
    component: () => import('@/views/accessControl/listAdminInout.vue'),
    meta: { requiresAuth: true, title: '门禁出入记录' }
  },
  {
    path: 'pages/car/adminCarInout',
    name: 'AdminCarInout',
    component: () => import('@/views/car/listAdminCarInout.vue'),
    meta: { requiresAuth: true, title: '车辆出入记录' }
  },
  {
    path: 'pages/car/spaceMachineFactory',
    name: 'SpaceMachineFactory',
    component: () => import('@/views/car/listSpaceMachineFactory.vue'),
    meta: { requiresAuth: true, title: '车位设备厂商' }
  },
  {
    path: 'pages/charge/adminChargeMachine',
    name: 'AdminChargeMachine',
    component: () => import('@/views/charge/listAdminChargeMachine.vue'),
    meta: { requiresAuth: true, title: '充电桩管理' }
  },
  {
    path: 'pages/charge/workLicenseFactory',
    name: 'WorkLicenseFactory',
    component: () => import('@/views/charge/listWorkLicenseFactory.vue'),
    meta: { requiresAuth: true, title: '充电桩厂商' }
  },
  {
    path: 'pages/meter/adminMeter',
    name: 'AdminMeter',
    component: () => import('@/views/meter/listAdminMeter.vue'),
    meta: { requiresAuth: true, title: '水电表管理' }
  },
  {
    path: 'pages/lamp/lampMachineFactoryManage',
    name: 'LampMachineFactoryManage',
    component: () => import('@/views/lamp/listLampMachineFactoryManage.vue'),
    meta: { requiresAuth: true, title: '灯杆设备厂商' }
  },
  {
    path: 'pages/monitor/monitorModelManage',
    name: 'MonitorModelManage',
    component: () => import('@/views/monitor/monitorModelManage.vue'),
    meta: { requiresAuth: true, title: '监控设备型号' }
  },
  {
    path: 'pages/3d/dtScript',
    name: 'DtScript',
    component: () => import('@/views/3d/listDtScript.vue'),
    meta: { requiresAuth: true, title: '3D脚本' }
  },
  {
    path: 'pages/accessControl/hardwareManufacturerManage',
    name: 'HardwareManufacturerManage',
    component: () => import('@/views/accessControl/listHardwareManufacturer.vue'),
    meta: { requiresAuth: true, title: '门禁厂家' }
  },
  {
    path: 'pages/admin/dtScene',
    name: 'DtScene',
    component: () => import('@/views/admin/listDtScene.vue'),
    meta: { requiresAuth: true, title: '3D场景' }
  },
  {
    path: 'pages/accessControl/aAccessControl',
    name: 'AAccessControl',
    component: () => import('@/views/accessControl/aAccessControl.vue'),
    meta: { requiresAuth: true, title: '门禁设备' }
  },
  {
    path: 'pages/car/adminBarrier',
    name: 'AdminBarrier',
    component: () => import('@/views/car/listAdminBarrier.vue'),
    meta: { requiresAuth: true, title: '道闸摄像头' }
  },
  {
    path: 'pages/common/cityArea',
    name: 'CityArea',
    component: () => import('@/views/common/cityAreaManage.vue'),
    meta: { requiresAuth: true, title: '地区管理' }
  },
  {
    path: 'pages/meter/adminMeterDetail',
    name: 'AdminMeterDetail',
    component: () => import('@/views/meter/adminMeterDetail.vue'),
    meta: { requiresAuth: true, title: '水电表详情' }
  },
  {
    path: 'pages/charge/adminChargeDetail',
    name: 'AdminChargeDetail',
    component: () => import('@/views/charge/adminChargeDetail.vue'),
    meta: { requiresAuth: true, title: '充电详情' }
  },
  {
    path: 'pages/accessControl/aAccessControlDetail',
    name: 'AAccessControlDetail',
    component: () => import('@/views/accessControl/aAccessControlDetail.vue'),
    meta: { requiresAuth: true, title: '门禁设备详情' }
  }
]

export default adminRoutes
