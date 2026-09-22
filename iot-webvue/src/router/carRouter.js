const carRoutes = [
  {
    path: 'pages/car/parkingSpaceMachine',
    name: 'ParkingSpaceMachine',
    component: () => import(/* webpackChunkName: "car-parkingSpaceMachine" */ '@/views/car/parkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '车位地磁' }
  },
  {
    path: 'pages/car/addParkingSpaceMachine',
    name: 'AddParkingSpaceMachine',
    component: () => import(/* webpackChunkName: "car-addParkingSpaceMachine" */ '@/views/car/addParkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '添加地磁' }
  },
  {
    path: 'pages/car/editParkingSpaceMachine',
    name: 'EditParkingSpaceMachine',
    component: () => import(/* webpackChunkName: "car-editParkingSpaceMachine" */ '@/views/car/editParkingSpaceMachine.vue'),
    meta: { requiresAuth: true, title: '修改地磁' }
  },
  {
    path: 'pages/car/parkingSpace',
    name: 'ParkingSpace',
    component: () => import(/* webpackChunkName: "car-parkingSpace" */ '@/views/car/parkingSpace.vue'),
    meta: { requiresAuth: true, title: '停车位' }
  },
  {
    path: 'pages/car/barrier',
    name: 'Barrier',
    component: () => import(/* webpackChunkName: "car-barrier" */ '@/views/car/barrier.vue'),
    meta: { requiresAuth: true, title: '道闸摄像头' }
  },
  {
    path: 'pages/car/listOwnerCar',
    name: 'ListOwnerCar',
    component: () => import(/* webpackChunkName: "car-listOwnerCar" */ '@/views/car/listOwnerCar.vue'),
    meta: { requiresAuth: true, title: '业主车辆' }
  },
  {
    path: 'pages/car/carDetail',
    name: 'CarDetail',
    component: () => import(/* webpackChunkName: "car-carDetail" */ '@/views/car/carDetail.vue'),
    meta: { requiresAuth: true, title: '车辆详情' }
  },
  {
    path: 'pages/car/hireParkingSpace',
    name: 'HireParkingSpace',
    component: () => import(/* webpackChunkName: "car-hireParkingSpace" */ '@/views/car/hireParkingSpace.vue'),
    meta: { requiresAuth: true, title: '添加车辆' }
  },
  {
    path: 'pages/car/listOwnerCarMember',
    name: 'ListOwnerCarMember',
    component: () => import(/* webpackChunkName: "car-listOwnerCarMember" */ '@/views/car/listOwnerCarMember.vue'),
    meta: { requiresAuth: true, title: '成员车辆' }
  },
  {
    path: 'pages/car/carMonthCard',
    name: 'CarMonthCard',
    component: () => import(/* webpackChunkName: "car-carMonthCard" */ '@/views/car/carMonthCard.vue'),
    meta: { requiresAuth: true, title: '停车月卡' }
  },
  {
    path: 'pages/car/carMonthOrder',
    name: 'CarMonthOrder',
    component: () => import(/* webpackChunkName: "car-carMonthOrder" */ '@/views/car/carMonthOrder.vue'),
    meta: { requiresAuth: true, title: '月卡订单' }
  },
  {
    path: 'pages/car/carBlackWhite',
    name: 'CarBlackWhite',
    component: () => import(/* webpackChunkName: "car-carBlackWhite" */ '@/views/car/carBlackWhite.vue'),
    meta: { requiresAuth: true, title: '黑白名单' }
  },
  {
    path: 'pages/car/tempInnerCar',
    name: 'TempInnerCar',
    component: () => import(/* webpackChunkName: "car-tempInnerCar" */ '@/views/car/tempInnerCar.vue'),
    meta: { requiresAuth: true, title: '内部临时车' }
  },
  {
    path: 'pages/car/parkingArea',
    name: 'ParkingArea',
    component: () => import(/* webpackChunkName: "car-parkingArea" */ '@/views/car/parkingArea.vue'),
    meta: { requiresAuth: true, title: '停车区域' }
  },
  {
    path: 'pages/car/parkingBox',
    name: 'ParkingBox',
    component: () => import(/* webpackChunkName: "car-parkingBox" */ '@/views/car/parkingBox.vue'),
    meta: { requiresAuth: true, title: '道闸岗亭' }
  },
  {
    path: 'pages/car/parkingBoxArea',
    name: 'ParkingBoxArea',
    component: () => import(/* webpackChunkName: "car-parkingBoxArea" */ '@/views/car/parkingBoxArea.vue'),
    meta: { requiresAuth: true, title: '共享岗亭' }
  },
  {
    path: 'pages/car/tempCarFeeConfig',
    name: 'TempCarFeeConfig',
    component: () => import(/* webpackChunkName: "car-tempCarFeeConfig" */ '@/views/car/tempCarFeeConfig.vue'),
    meta: { requiresAuth: true, title: '临车收费' }
  },
  {
    path: 'pages/car/parkingRegion',
    name: 'ParkingRegion',
    component: () => import(/* webpackChunkName: "car-parkingRegion" */ '@/views/car/parkingRegion.vue'),
    meta: { requiresAuth: true, title: '区域管理' }
  },
  {
    path: 'pages/car/parkingAreaTotalControl',
    name: 'ParkingAreaTotalControl',
    component: () => import(/* webpackChunkName: "car-parkingAreaTotalControl" */ '@/views/car/parkingAreaTotalControl.vue'),
    meta: { requiresAuth: true, title: '停车场总控台' }
  },
  {
    path: 'pages/car/parkingAreaControl',
    name: 'ParkingAreaControl',
    component: () => import(/* webpackChunkName: "car-parkingAreaControl" */ '@/views/car/parkingAreaControl.vue'),
    meta: { requiresAuth: true, title: '岗亭控制台' }
  },
  {
    path: 'pages/car/listCarInoutHis',
    name: 'ListCarInoutHis',
    component: () => import(/* webpackChunkName: "car-listCarInoutHis" */ '@/views/car/listCarInoutHis.vue'),
    meta: { requiresAuth: true, title: '车辆出入历史记录' }
  },
  {
    path: 'pages/car/listCarInoutEvasionConfig',
    name: 'ListCarInoutEvasionConfig',
    component: () => import(/* webpackChunkName: "car-listCarInoutEvasionConfig" */ '@/views/car/listCarInoutEvasionConfig.vue'),
    meta: { requiresAuth: true, title: '逃费检测设置' }
  },
  {
    path: 'pages/car/listCarInoutEvasion',
    name: 'ListCarInoutEvasion',
    component: () => import(/* webpackChunkName: "car-listCarInoutEvasion" */ '@/views/car/listCarInoutEvasion.vue'),
    meta: { requiresAuth: true, title: '车辆逃费记录' }
  },
  {
    path: 'pages/car/mqttSendLog',
    name: 'MqttSendLog',
    component: () => import(/* webpackChunkName: "car-mqttSendLog" */ '@/views/car/mqttSendLog.vue'),
    meta: { requiresAuth: true, title: 'MQTT下发日志' }
  },
  {
    path: 'pages/car/rentParkingSpace',
    name: 'RentParkingSpace',
    component: () => import(/* webpackChunkName: "car-rentParkingSpace" */ '@/views/car/rentParkingSpace.vue'),
    meta: { requiresAuth: true, title: '出租车位' }
  },
  {
    path: 'pages/car/sellParkingSpace',
    name: 'SellParkingSpace',
    component: () => import(/* webpackChunkName: "car-sellParkingSpace" */ '@/views/car/sellParkingSpace.vue'),
    meta: { requiresAuth: true, title: '出售车位' }
  },
  {
    path: 'pages/car/updateParkingSpaceCar',
    name: 'UpdateParkingSpaceCar',
    component: () => import(/* webpackChunkName: "car-updateParkingSpaceCar" */ '@/views/car/updateParkingSpaceCar.vue'),
    meta: { requiresAuth: true, title: '修改车辆' }
  }
]

export default carRoutes
