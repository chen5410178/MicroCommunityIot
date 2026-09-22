const chargeRoutes = [
  {
    path: 'pages/charge/chargeRule',
    name: 'ChargeRule',
    component: () => import(/* webpackChunkName: "charge-chargeRule" */ '@/views/charge/chargeRule.vue'),
    meta: { requiresAuth: true, title: '充电规则' }
  },
  {
    path: 'pages/charge/chargeMachineManage',
    name: 'ChargeMachineManage',
    component: () => import(/* webpackChunkName: "charge-chargeMachineManage" */ '@/views/charge/chargeMachineManage.vue'),
    meta: { requiresAuth: true, title: '充电桩管理' }
  },
  {
    path: 'pages/charge/chargeMachinePortManage',
    name: 'ChargeMachinePortManage',
    component: () => import(/* webpackChunkName: "charge-chargeMachinePortManage" */ '@/views/charge/chargeMachinePortManage.vue'),
    meta: { requiresAuth: true, title: '充电桩端口' }
  },
  {
    path: 'pages/charge/chargeMachineDetail',
    name: 'ChargeMachineDetail',
    component: () => import(/* webpackChunkName: "charge-chargeMachineDetail" */ '@/views/charge/chargeMachineDetail.vue'),
    meta: { requiresAuth: true, title: '充电桩详情' }
  },
  {
    path: 'pages/charge/chargeMonthOrderManage',
    name: 'ChargeMonthOrderManage',
    component: () => import(/* webpackChunkName: "charge-chargeMonthOrderManage" */ '@/views/charge/chargeMonthOrderManage.vue'),
    meta: { requiresAuth: true, title: '充电月卡' }
  },
  {
    path: 'pages/charge/chargeStatistics',
    name: 'ChargeStatistics',
    component: () => import(/* webpackChunkName: "charge-chargeStatistics" */ '@/views/charge/chargeStatistics.vue'),
    meta: { requiresAuth: true, title: '充电统计' }
  },
  {
    path: 'pages/charge/chargeUserCoupon',
    name: 'ChargeUserCoupon',
    component: () => import(/* webpackChunkName: "charge-chargeUserCoupon" */ '@/views/charge/chargeUserCoupon.vue'),
    meta: { requiresAuth: true, title: '充电券' }
  },
  {
    path: 'pages/charge/chargeMachineOrder',
    name: 'ChargeMachineOrder',
    component: () => import(/* webpackChunkName: "charge-chargeMachineOrder" */ '@/views/charge/chargeMachineOrder.vue'),
    meta: { requiresAuth: true, title: '充电订单' }
  },
  {
    path: 'pages/charge/chargeMonthCardManage',
    name: 'ChargeMonthCardManage',
    component: () => import(/* webpackChunkName: "charge-chargeMonthCardManage" */ '@/views/charge/chargeMonthCardManage.vue'),
    meta: { requiresAuth: true, title: '充电月卡管理' }
  },
  {
    path: 'pages/charge/chargeMachineOrderAccts',
    name: 'ChargeMachineOrderAccts',
    component: () => import(/* webpackChunkName: "charge-chargeMachineOrderAccts" */ '@/views/charge/chargeMachineOrderAccts.vue'),
    meta: { requiresAuth: true }
  }
]

export default chargeRoutes
