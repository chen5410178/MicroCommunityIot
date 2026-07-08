const carRoutes = [
  {
    path: 'pages/car/parkingSpaceMachine',
    name: 'ParkingSpaceMachine',
    component: () => import('@/views/car/parkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '车位地磁' }
  },
  {
    path: 'pages/car/addParkingSpaceMachine',
    name: 'AddParkingSpaceMachine',
    component: () => import('@/views/car/addParkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '添加地磁' }
  },
  {
    path: 'pages/car/editParkingSpaceMachine',
    name: 'EditParkingSpaceMachine',
    component: () => import('@/views/car/editParkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '修改地磁' }
  },
  {
    path: 'pages/car/parkingSpace',
    name: 'ParkingSpace',
    component: () => import('@/views/car/parkingSpace.vue'),
    meta: { requiresAuth: true, title: '停车位' }
  },
  {
    path: 'pages/car/barrier',
    name: 'Barrier',
    component: () => import('@/views/car/barrier.vue'),
    meta: { requiresAuth: true, title: '道闸摄像头' }
  },
  {
    path: 'pages/car/listOwnerCar',
    name: 'ListOwnerCar',
    component: () => import('@/views/car/listOwnerCar.vue'),
    meta: { requiresAuth: true, title: '业主车辆' }
  },
  {
    path: 'pages/car/carDetail',
    name: 'CarDetail',
    component: () => import('@/views/car/carDetail.vue'),
    meta: { requiresAuth: true, title: '车辆详情' }
  },
  {
    path: 'pages/car/hireParkingSpace',
    name: 'HireParkingSpace',
    component: () => import('@/views/car/hireParkingSpace.vue'),
    meta: { requiresAuth: true, title: '添加车辆' }
  },
  {
    path: 'pages/car/carAddParkingSpace',
    name: 'CarAddParkingSpace',
    component: () => import('@/views/car/carAddParkingSpace.vue'),
    meta: { requiresAuth: true, title: '续租车位' }
  },
  {
    path: 'pages/car/listOwnerCarMember',
    name: 'ListOwnerCarMember',
    component: () => import('@/views/car/listOwnerCarMember.vue'),
    meta: { requiresAuth: true, title: '成员车辆' }
  },
  {
    path: 'pages/car/carMonthCard',
    name: 'CarMonthCard',
    component: () => import('@/views/car/carMonthCard.vue'),
    meta: { requiresAuth: true, title: '停车月卡' }
  },
  {
    path: 'pages/car/carMonthOrder',
    name: 'CarMonthOrder',
    component: () => import('@/views/car/carMonthOrder.vue'),
    meta: { requiresAuth: true, title: '月卡订单' }
  },
  {
    path: 'pages/car/carBlackWhite',
    name: 'CarBlackWhite',
    component: () => import('@/views/car/carBlackWhite.vue'),
    meta: { requiresAuth: true, title: '黑白名单' }
  },
  {
    path: 'pages/car/tempInnerCar',
    name: 'TempInnerCar',
    component: () => import('@/views/car/tempInnerCar.vue'),
    meta: { requiresAuth: true, title: '内部临时车' }
  },
  {
    path: 'pages/car/parkingArea',
    name: 'ParkingArea',
    component: () => import('@/views/car/parkingArea.vue'),
    meta: { requiresAuth: true, title: '停车区域' }
  },
  {
    path: 'pages/car/parkingBox',
    name: 'ParkingBox',
    component: () => import('@/views/car/parkingBox.vue'),
    meta: { requiresAuth: true, title: '道闸岗亭' }
  },
  {
    path: 'pages/car/tempCarFeeConfig',
    name: 'TempCarFeeConfig',
    component: () => import('@/views/car/tempCarFeeConfig.vue'),
    meta: { requiresAuth: true, title: '临车收费' }
  },
  {
    path: 'pages/car/parkingRegion',
    name: 'ParkingRegion',
    component: () => import('@/views/car/parkingRegion.vue'),
    meta: { requiresAuth: true, title: '区域管理' }
  },
  {
    path: 'pages/car/parkingAreaTotalControl',
    name: 'ParkingAreaTotalControl',
    component: () => import('@/views/car/parkingAreaTotalControl.vue'),
    meta: { requiresAuth: true, title: '停车场总控台' }
  },
  {
    path: 'pages/car/parkingAreaControl',
    name: 'ParkingAreaControl',
    component: () => import('@/views/car/parkingAreaControl.vue'),
    meta: { requiresAuth: true, title: '岗亭控制台' }
  }
]

export default carRoutes
