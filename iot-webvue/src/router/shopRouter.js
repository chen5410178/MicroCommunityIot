const shopRoutes = [
  {
    path: 'pages/shop/listShopManage',
    name: 'ListShopManage',
    component: () => import('@/views/shop/listShopManage.vue'),
    meta: { requiresAuth: true, title: '商铺管理' }
  },
  {
    path: 'pages/shop/shopBindOwner',
    name: 'ShopBindOwner',
    component: () => import('@/views/shop/shopBindOwner.vue'),
    meta: { requiresAuth: true, title: '商铺移交' }
  },
  {
    path: 'pages/shop/shopUnbindOwner',
    name: 'ShopUnbindOwner',
    component: () => import('@/views/shop/shopUnbindOwner.vue'),
    meta: { requiresAuth: true, title: '商铺退租' }
  },
  {
    path: 'pages/shop/shopDetail',
    name: 'ShopDetail',
    component: () => import('@/views/shop/shopDetail.vue'),
    meta: { requiresAuth: true, title: '商铺详情' }
  }
]

export default shopRoutes
