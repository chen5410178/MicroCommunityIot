const propertyRoutes = [
  {
    path: 'pages/machine/machineLocation',
    name: 'MachineLocation',
    component: () => import(/* webpackChunkName: "property-machineLocation" */ '@/views/machine/machineLocation.vue'),
    meta: { requiresAuth: true, title: '设备位置' }
  },
  {
    path: 'pages/property/listOwner',
    name: 'ListOwner',
    component: () => import(/* webpackChunkName: "property-listOwner" */ '@/views/property/listOwner.vue'),
    meta: { requiresAuth: true, title: '人员信息' }
  },
  {
    path: 'pages/owner/ownerDetail',
    name: 'OwnerDetail',
    component: () => import(/* webpackChunkName: "property-ownerDetail" */ '@/views/owner/ownerDetail.vue'),
    meta: { requiresAuth: true, title: '人员详情' }
  },
  {
    path: 'pages/owner/appUser',
    name: 'ListAppUser',
    component: () => import(/* webpackChunkName: "property-appUser" */ '@/views/owner/appUser.vue'),
    meta: { requiresAuth: true, title: '房屋认证' }
  },
  {
    path: 'pages/common/enterCommunity',
    name: 'EnterCommunity',
    component: () => import(/* webpackChunkName: "property-enterCommunity" */ '@/views/common/enterCommunity.vue'),
    meta: { requiresAuth: true, title: '入驻小区' }
  },
  {
    path: 'pages/property/room',
    name: 'Room',
    component: () => import(/* webpackChunkName: "property-room" */ '@/views/property/room.vue'),
    meta: { requiresAuth: true, title: '房屋管理' }
  },
  {
    path: 'pages/owner/account',
    name: 'Account',
    component: () => import(/* webpackChunkName: "property-account" */ '@/views/owner/account.vue'),
    meta: { requiresAuth: true, title: '账户管理' }
  },
  {
    path: 'pages/owner/accountDetail',
    name: 'AccountDetail',
    component: () => import(/* webpackChunkName: "property-accountDetail" */ '@/views/owner/accountDetail.vue'),
    meta: { requiresAuth: true, title: '账户明细' }
  },
  {
    path: 'pages/property/roomDetail',
    name: 'RoomDetail',
    component: () => import(/* webpackChunkName: "property-roomDetail" */ '@/views/property/roomDetail.vue'),
    meta: { requiresAuth: true, title: '房屋详情' }
  },
  {
    path: 'pages/property/addRoomView',
    name: 'AddRoomView',
    component: () => import(/* webpackChunkName: "property-addRoomView" */ '@/views/property/addRoomView.vue'),
    meta: { requiresAuth: true, title: '添加房屋' }
  },
  {
    path: 'pages/owner/roomBindOwner',
    name: 'RoomBindOwner',
    component: () => import(/* webpackChunkName: "property-roomBindOwner" */ '@/views/owner/roomBindOwner.vue'),
    meta: { requiresAuth: true, title: '房屋绑定人员' }
  },
  {
    path: 'pages/owner/roomUnBindOwner',
    name: 'RoomUnBindOwner',
    component: () => import(/* webpackChunkName: "property-roomUnBindOwner" */ '@/views/owner/roomUnBindOwner.vue'),
    meta: { requiresAuth: true, title: '房屋解绑人员' }
  },
  {
    path: 'pages/property/downloadTempFile',
    name: 'DownloadTempFile',
    component: () => import(/* webpackChunkName: "property-downloadTempFile" */ '@/views/property/downloadTempFile.vue'),
    meta: { requiresAuth: true, title: '下载中心' }
  },
  {
    path: 'pages/property/invoiceManage',
    name: 'InvoiceManage',
    component: () => import(/* webpackChunkName: "property-invoiceManage" */ '@/views/property/invoiceManage.vue'),
    meta: { requiresAuth: true, title: '发票管理' }
  },
  {
    path: 'pages/property/invoiceApply',
    name: 'InvoiceApply',
    component: () => import(/* webpackChunkName: "property-invoiceApply" */ '@/views/property/invoiceApply.vue'),
    meta: { requiresAuth: true, title: '申请开票' }
  }
]

export default propertyRoutes
