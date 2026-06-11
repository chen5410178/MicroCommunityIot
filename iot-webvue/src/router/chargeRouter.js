const chargeRoutes = [
  {
    path: 'pages/charge/chargeRule',
    name: 'ChargeRule',
    component: () => import('@/views/charge/chargeRule.vue'),
    meta: { requiresAuth: true, title: '充电规则' }
  },
  {
    path: 'pages/charge/chargeMachineManage',
    name: 'ChargeMachineManage',
    component: () => import('@/views/charge/chargeMachineManage.vue'),
    meta: { requiresAuth: true, title: '充电桩管理' }
  },
  {
    path: 'pages/charge/chargeMachinePortManage',
    name: 'ChargeMachinePortManage',
    component: () => import('@/views/charge/chargeMachinePortManage.vue'),
    meta: { requiresAuth: true, title: '充电桩端口' }
  },
  {
    path: 'pages/charge/chargeMachineDetail',
    name: 'ChargeMachineDetail',
    component: () => import('@/views/charge/chargeMachineDetail.vue'),
    meta: { requiresAuth: true, title: '充电桩详情' }
  },
  {
    path: 'pages/charge/chargeMonthOrderManage',
    name: 'ChargeMonthOrderManage',
    component: () => import('@/views/charge/chargeMonthOrderManage.vue'),
    meta: { requiresAuth: true, title: '充电月卡' }
  },
  {
    path: 'pages/charge/chargeStatistics',
    name: 'ChargeStatistics',
    component: () => import('@/views/charge/chargeStatistics.vue'),
    meta: { requiresAuth: true, title: '充电统计' }
  }
]

export default chargeRoutes
