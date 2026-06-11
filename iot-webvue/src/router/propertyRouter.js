const propertyRoutes = [
  {
    path: 'pages/machine/machineLocation',
    name: 'MachineLocation',
    component: () => import('@/views/machine/listMachineLocation.vue'),
    meta: { requiresAuth: true, title: '设备位置' }
  },
  {
    path: 'pages/property/listOwner',
    name: 'ListOwner',
    component: () => import('@/views/property/listOwner.vue'),
    meta: { requiresAuth: true, title: '人员信息' }
  },
  {
    path: 'pages/owner/ownerDetail',
    name: 'OwnerDetail',
    component: () => import('@/views/owner/ownerDetail.vue'),
    meta: { requiresAuth: true, title: '人员详情' }
  },
  {
    path: 'pages/owner/appUser',
    name: 'ListAppUser',
    component: () => import('@/views/owner/listAppUser.vue'),
    meta: { requiresAuth: true, title: '房屋认证' }
  }
]

export default propertyRoutes
