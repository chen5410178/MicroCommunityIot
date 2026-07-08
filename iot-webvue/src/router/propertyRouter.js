const propertyRoutes = [
  {
    path: 'pages/machine/machineLocation',
    name: 'MachineLocation',
    component: () => import('@/views/machine/machineLocation.vue'),
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
    component: () => import('@/views/owner/appUser.vue'),
    meta: { requiresAuth: true, title: '房屋认证' }
  },
  {
    path: 'pages/common/enterCommunity',
    name: 'EnterCommunity',
    component: () => import('@/views/common/enterCommunity.vue'),
    meta: { requiresAuth: true, title: '入驻小区' }
  },
  {
    path: 'pages/property/room',
    name: 'Room',
    component: () => import('@/views/property/room.vue'),
    meta: { requiresAuth: true, title: '房屋管理' }
  },
  {
    path: 'pages/owner/account',
    name: 'Account',
    component: () => import('@/views/owner/account.vue'),
    meta: { requiresAuth: true, title: '账户管理' }
  },
  {
    path: 'pages/property/roomDetail',
    name: 'RoomDetail',
    component: () => import('@/views/property/roomDetail.vue'),
    meta: { requiresAuth: true, title: '房屋详情' }
  },
  {
    path: 'pages/property/addRoomView',
    name: 'AddRoomView',
    component: () => import('@/views/property/addRoomView.vue'),
    meta: { requiresAuth: true, title: '添加房屋' }
  },
  {
    path: 'pages/owner/roomBindOwner',
    name: 'RoomBindOwner',
    component: () => import('@/views/owner/roomBindOwner.vue'),
    meta: { requiresAuth: true, title: '房屋绑定人员' }
  },
  {
    path: 'pages/owner/roomUnBindOwner',
    name: 'RoomUnBindOwner',
    component: () => import('@/views/owner/roomUnBindOwner.vue'),
    meta: { requiresAuth: true, title: '房屋解绑人员' }
  }
]

export default propertyRoutes
