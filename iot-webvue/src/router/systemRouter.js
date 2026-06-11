const systemRoutes = [
  {
    path: 'pages/frame/orgManage',
    name: 'OrgManage',
    component: () => import('@/views/frame/orgManage.vue'),
    meta: { requiresAuth: true, title: '组织管理' }
  },
  {
    path: 'pages/frame/role',
    name: 'RoleManage',
    component: () => import('@/views/frame/role/role.vue'),
    meta: { requiresAuth: true, title: '角色管理' }
  },
  {
    path: 'pages/frame/staffDetail',
    name: 'StaffDetail',
    component: () => import('@/views/frame/staffDetail.vue'),
    meta: { requiresAuth: true, title: '员工详情' }
  },
  {
    path: 'pages/charge/workLicenseMap',
    name: 'WorkLicenseMap',
    component: () => import('@/views/charge/workLicenseMap.vue'),
    meta: { requiresAuth: true, title: '员工位置' }
  },
  {
    path: 'pages/charge/workLicenseLineMap',
    name: 'WorkLicenseLineMap',
    component: () => import('@/views/charge/workLicenseLineMap.vue'),
    meta: { requiresAuth: true, title: '员工轨迹' }
  },
  {
    path: 'pages/charge/workLicenseLog',
    name: 'WorkLicenseLog',
    component: () => import('@/views/charge/workLicenseLog.vue'),
    meta: { requiresAuth: true, title: '工牌日志' }
  },
  {
    path: 'pages/property/paymentPool',
    name: 'PaymentPool',
    component: () => import('@/views/property/paymentPool.vue'),
    meta: { requiresAuth: true, title: '支付配置' }
  },
  // ===== 公众号管理 =====
  {
    path: 'pages/property/publicWeChatManage',
    name: 'PublicWeChatManage',
    component: () => import('@/views/property/listPublicWeChat.vue'),
    meta: { requiresAuth: true, title: '公众号管理' }
  },
  {
    path: 'pages/property/wechatMenuManage',
    name: 'WechatMenuManage',
    component: () => import('@/views/property/listWechatMenu.vue'),
    meta: { requiresAuth: true, title: '微信菜单' }
  },
  {
    path: 'pages/property/wechatSmsTemplateManage',
    name: 'WechatSmsTemplateManage',
    component: () => import('@/views/property/listWechatSmsTemplate.vue'),
    meta: { requiresAuth: true, title: '微信模板' }
  }
]

export default systemRoutes
