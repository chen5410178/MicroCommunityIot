const meterRoutes = [
  {
    path: 'pages/meter/meterMachineManage',
    name: 'MeterMachineManage',
    component: () => import('@/views/meter/meterMachineManage.vue'),
    meta: { requiresAuth: true, title: '智能水电表' }
  },
  {
    path: 'pages/meter/addMeterMachine',
    name: 'AddMeterMachine',
    component: () => import('@/views/meter/addMeterMachine.vue'),
    meta: { requiresAuth: true, title: '添加水电表' }
  },
  {
    path: 'pages/meter/editMeterMachine',
    name: 'EditMeterMachine',
    component: () => import('@/views/meter/editMeterMachine.vue'),
    meta: { requiresAuth: true, title: '修改水电表' }
  },
  {
    path: 'pages/meter/meterMachineCharge',
    name: 'MeterMachineCharge',
    component: () => import('@/views/meter/meterMachineCharge.vue'),
    meta: { requiresAuth: true, title: '充值记录' }
  },
  {
    path: 'pages/meter/meterDetail',
    name: 'MeterDetail',
    component: () => import('@/views/meter/meterDetail.vue'),
    meta: { requiresAuth: true, title: '水电表详情' }
  },
  {
    path: 'pages/meter/meterTypeManage',
    name: 'MeterTypeManage',
    component: () => import('@/views/meter/meterTypeManage.vue'),
    meta: { requiresAuth: true, title: '水电类型' }
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
    path: 'pages/meter/listAdminMeter',
    name: 'ListAdminMeter',
    component: () => import('@/views/meter/listAdminMeter.vue'),
    meta: { requiresAuth: true, title: '水电表管理' }
  },
  {
    path: 'pages/meter/listAdminMeterRecharge',
    name: 'ListAdminMeterRecharge',
    component: () => import('@/views/meter/listAdminMeterRecharge.vue'),
    meta: { requiresAuth: true, title: '充值明细' }
  },
  {
    path: 'pages/meter/adminMeterDetail',
    name: 'AdminMeterDetail',
    component: () => import('@/views/meter/adminMeterDetail.vue'),
    meta: { requiresAuth: true, title: '水电表详情' }
  },
  {
    path: 'pages/meter/listInstrumentFactoryManage',
    name: 'ListInstrumentFactoryManage',
    component: () => import('@/views/meter/listInstrumentFactoryManage.vue'),
    meta: { requiresAuth: true, title: '仪表厂家' }
  },
  {
    path: 'pages/meter/listInstrumentFactorySpecManage',
    name: 'ListInstrumentFactorySpecManage',
    component: () => import('@/views/meter/listInstrumentFactorySpecManage.vue'),
    meta: { requiresAuth: true, title: '仪表厂家规格' }
  },
  {
    path: 'pages/meter/meterMachineLogManage',
    name: 'MeterMachineLogManage',
    component: () => import('@/views/meter/meterMachineLogManage.vue'),
    meta: { requiresAuth: true, title: '开合闸记录' }
  },
  {
    path: 'pages/meter/meterQrcodeManage',
    name: 'MeterQrcodeManage',
    component: () => import('@/views/meter/meterQrcodeManage.vue'),
    meta: { requiresAuth: true, title: '水电二维码' }
  },
  {
    path: 'pages/meter/meterMachineRechargeDetail',
    name: 'MeterMachineRechargeDetail',
    component: () => import('@/views/meter/meterMachineRechargeDetail.vue'),
    meta: { requiresAuth: true, title: '充值记录' }
  },
  {
    path: 'pages/meter/meterMachineReadDetail',
    name: 'MeterMachineReadDetail',
    component: () => import('@/views/meter/meterMachineReadDetail.vue'),
    meta: { requiresAuth: true, title: '抄表记录' }
  },
  {
    path: 'pages/meter/instrumentTypeManage',
    name: 'InstrumentTypeManage',
    component: () => import('@/views/meter/instrumentTypeManage.vue'),
    meta: { requiresAuth: true, title: '仪表组' }
  },
  {
    path: 'pages/meter/instrumentLogManage',
    name: 'InstrumentLogManage',
    component: () => import('@/views/meter/instrumentLogManage.vue'),
    meta: { requiresAuth: true, title: '仪表上报数据' }
  },
  {
    path: 'pages/event/eventPoolManage',
    name: 'EventPoolManage',
    component: () => import('@/views/event/eventPoolManage.vue'),
    meta: { requiresAuth: true, title: '事件池' }
  },
  {
    path: 'pages/event/addEventPoolManage',
    name: 'AddEventPoolManage',
    component: () => import('@/views/event/addEventPoolManage.vue'),
    meta: { requiresAuth: true, title: '添加事件' }
  },
  {
    path: 'pages/event/eventDetail',
    name: 'EventDetail',
    component: () => import('@/views/event/eventDetail.vue'),
    meta: { requiresAuth: true, title: '事件详情' }
  },
  {
    path: 'pages/event/eventPoolStaffManage',
    name: 'EventPoolStaffManage',
    component: () => import('@/views/event/eventPoolStaffManage.vue'),
    meta: { requiresAuth: true, title: '我的事件' }
  },
  {
    path: 'pages/event/eventRule',
    name: 'EventRule',
    component: () => import('@/views/event/eventRule.vue'),
    meta: { requiresAuth: true, title: '事件规则' }
  },
  {
    path: 'pages/event/addEventObject',
    name: 'AddEventObject',
    component: () => import('@/views/event/addEventObject.vue'),
    meta: { requiresAuth: true, title: '添加事件对象' }
  },
  {
    path: 'pages/event/addEventStaff',
    name: 'AddEventStaff',
    component: () => import('@/views/event/addEventStaff.vue'),
    meta: { requiresAuth: true, title: '添加事件员工' }
  }
]

export default meterRoutes
