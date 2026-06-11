const carRoutes = [
  {
    path: 'pages/car/parkingSpace',
    name: 'ParkingSpace',
    component: () => import('@/views/car/parkingSpace.vue'),
    meta: { requiresAuth: true, title: '停车位' }
  }
]

export default carRoutes
