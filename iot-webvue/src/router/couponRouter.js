const couponRoutes = [
  {
    path: 'pages/coupon/listParkingCoupon',
    name: 'ListParkingCoupon',
    component: () => import('@/views/coupon/listParkingCoupon.vue'),
    meta: { requiresAuth: true, title: '停车券' }
  },
  {
    path: 'pages/coupon/listParkingCouponCar',
    name: 'ListParkingCouponCar',
    component: () => import('@/views/coupon/listParkingCouponCar.vue'),
    meta: { requiresAuth: true, title: '车辆停车券' }
  },
  {
    path: 'pages/coupon/listParkingCouponOrder',
    name: 'ListParkingCouponOrder',
    component: () => import('@/views/coupon/listParkingCouponOrder.vue'),
    meta: { requiresAuth: true, title: '停车券订单' }
  },
  {
    path: 'pages/coupon/listParkingCouponCarOrder',
    name: 'ListParkingCouponCarOrder',
    component: () => import('@/views/coupon/listParkingCouponCarOrder.vue'),
    meta: { requiresAuth: true, title: '停车券核销' }
  },
  {
    path: 'pages/coupon/listCommunityShop',
    name: 'ListCommunityShop',
    component: () => import('@/views/coupon/listCommunityShop.vue'),
    meta: { requiresAuth: true, title: '小区商家' }
  },
  {
    path: 'pages/coupon/parkingCoupon',
    name: 'ParkingCoupon',
    component: () => import('@/views/coupon/listParkingCoupon.vue'),
    meta: { requiresAuth: true, title: '停车券' }
  },
  {
    path: 'pages/coupon/parkingCouponCar',
    name: 'ParkingCouponCar',
    component: () => import('@/views/coupon/listParkingCouponCar.vue'),
    meta: { requiresAuth: true, title: '车辆停车券' }
  },
  {
    path: 'pages/coupon/parkingCouponOrder',
    name: 'ParkingCouponOrder',
    component: () => import('@/views/coupon/listParkingCouponOrder.vue'),
    meta: { requiresAuth: true, title: '停车券订单' }
  },
  {
    path: 'pages/coupon/parkingCouponCarOrder',
    name: 'ParkingCouponCarOrder',
    component: () => import('@/views/coupon/listParkingCouponCarOrder.vue'),
    meta: { requiresAuth: true, title: '停车券核销' }
  },
  {
    path: 'pages/coupon/communityShop',
    name: 'CommunityShop',
    component: () => import('@/views/coupon/listCommunityShop.vue'),
    meta: { requiresAuth: true, title: '小区商家' }
  }
]

export default couponRoutes
