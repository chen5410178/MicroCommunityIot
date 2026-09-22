const accessControlRoutes = [
  {
    path: 'pages/accessControl/accessControlManage',
    name: 'AccessControlManage',
    component: () => import(/* webpackChunkName: "accessControl-accessControlManage" */ '@/views/accessControl/accessControlManage.vue'),
    meta: { requiresAuth: true, title: '门禁设备' }
  },
  {
    path: 'pages/accessControl/aAccessControlDetail',
    name: 'AAccessControlDetail',
    component: () => import(/* webpackChunkName: "accessControl-aAccessControlDetail" */ '@/views/accessControl/aAccessControlDetail.vue'),
    meta: { requiresAuth: true, title: '门禁设备详情' }
  },
  {
    path: 'pages/accessControl/accessControlDetail',
    name: 'AccessControlDetail',
    component: () => import(/* webpackChunkName: "accessControl-accessControlDetail" */ '@/views/accessControl/accessControlDetail.vue'),
    meta: { requiresAuth: true, title: '门禁设备详情(物业)' }
  },
  {
    path: 'pages/accessControl/accessControlFace',
    name: 'AccessControlFace',
    component: () => import(/* webpackChunkName: "accessControl-accessControlFace" */ '@/views/accessControl/accessControlFace.vue'),
    meta: { requiresAuth: true, title: '门禁人员' }
  },
  {
    path: 'pages/accessControl/accessControlPerson',
    name: 'AccessControlPerson',
    component: () => import(/* webpackChunkName: "accessControl-accessControlPerson" */ '@/views/accessControl/accessControlPerson.vue'),
    meta: { requiresAuth: true, title: '人员授权' }
  },
  {
    path: 'pages/accessControl/accessControlInout',
    name: 'AccessControlInout',
    component: () => import(/* webpackChunkName: "accessControl-accessControlInout" */ '@/views/accessControl/accessControlInout.vue'),
    meta: { requiresAuth: true, title: '进出记录' }
  },
  {
    path: 'pages/accessControl/visitTypeManage',
    name: 'VisitTypeManage',
    component: () => import(/* webpackChunkName: "accessControl-visitTypeManage" */ '@/views/accessControl/visitTypeManage.vue'),
    meta: { requiresAuth: true, title: '访客类型' }
  },
  {
    path: 'pages/accessControl/accessControlLog',
    name: 'AccessControlLog',
    component: () => import(/* webpackChunkName: "accessControl-accessControlLog" */ '@/views/accessControl/accessControlLog.vue'),
    meta: { requiresAuth: true, title: '下发日志' }
  },
  {
    path: 'pages/accessControl/hardwareManufacturerAttrManage',
    name: 'HardwareManufacturerAttrManage',
    component: () => import(/* webpackChunkName: "accessControl-hardwareManufacturerAttrManage" */ '@/views/accessControl/hardwareManufacturerAttrManage.vue'),
    meta: { requiresAuth: true, title: '门禁厂家属性' }
  },
  {
    path: 'pages/accessControl/accessControlOrg',
    name: 'AccessControlOrg',
    component: () => import(/* webpackChunkName: "accessControl-accessControlOrg" */ '@/views/accessControl/accessControlOrg.vue'),
    meta: { requiresAuth: true, title: '部门授权' }
  },
  {
    path: 'pages/accessControl/visitDetail',
    name: 'VisitDetail',
    component: () => import(/* webpackChunkName: "accessControl-visitDetail" */ '@/views/accessControl/visitDetail.vue'),
    meta: { requiresAuth: true, title: '访客详情' }
  },
  {
    path: 'pages/accessControl/accessControlAd',
    name: 'AccessControlAd',
    component: () => import(/* webpackChunkName: "accessControl-accessControlAd" */ '@/views/accessControl/accessControlAd.vue'),
    meta: { requiresAuth: true, title: '广告' }
  },
  {
    path: 'pages/accessControl/accessControlAdRel',
    name: 'AccessControlAdRel',
    component: () => import(/* webpackChunkName: "accessControl-accessControlAdRel" */ '@/views/accessControl/accessControlAdRel.vue'),
    meta: { requiresAuth: true, title: '广告门禁' }
  },
  {
    path: 'pages/accessControl/callingPlatformManage',
    name: 'CallingPlatformManage',
    component: () => import(/* webpackChunkName: "accessControl-callingPlatformManage" */ '@/views/accessControl/callingPlatformManage.vue'),
    meta: { requiresAuth: true, title: '云呼平台' }
  },
  {
    path: 'pages/accessControl/callingDeviceManage',
    name: 'CallingDeviceManage',
    component: () => import(/* webpackChunkName: "accessControl-callingDeviceManage" */ '@/views/accessControl/callingDeviceManage.vue'),
    meta: { requiresAuth: true, title: '呼叫设备' }
  },
  {
    path: 'pages/accessControl/callingContactManage',
    name: 'CallingContactManage',
    component: () => import(/* webpackChunkName: "accessControl-callingContactManage" */ '@/views/accessControl/callingContactManage.vue'),
    meta: { requiresAuth: true, title: '通讯录' }
  },
  {
    path: 'pages/accessControl/intercomLog',
    name: 'IntercomLog',
    component: () => import(/* webpackChunkName: "accessControl-intercomLog" */ '@/views/accessControl/intercomLog.vue'),
    meta: { requiresAuth: true, title: '呼叫记录' }
  },
  {
    path: 'pages/accessControl/acInoutStatistics',
    name: 'AcInoutStatistics',
    component: () => import(/* webpackChunkName: "accessControl-acInoutStatistics" */ '@/views/accessControl/acInoutStatistics.vue'),
    meta: { requiresAuth: true, title: '进出统计' }
  },
  {
    path: 'pages/accessControl/accessControlFloor',
    name: 'AccessControlFloor',
    component: () => import(/* webpackChunkName: "accessControl-accessControlFloor" */ '@/views/accessControl/accessControlFloor.vue'),
    meta: { requiresAuth: true, title: '门禁楼层' }
  },
  {
    path: 'pages/accessControl/accessControlShop',
    name: 'AccessControlShop',
    component: () => import(/* webpackChunkName: "accessControl-accessControlShop" */ '@/views/accessControl/accessControlShop.vue'),
    meta: { requiresAuth: true, title: '商铺授权' }
  },
  {
    path: 'pages/accessControl/visitManage',
    name: 'VisitManage',
    component: () => import(/* webpackChunkName: "accessControl-visitManage" */ '@/views/accessControl/visitManage.vue'),
    meta: { requiresAuth: true, title: '访客管理' }
  },
  {
    path: 'pages/accessControl/addVisit',
    name: 'AddVisit',
    component: () => import(/* webpackChunkName: "accessControl-addVisit" */ '@/views/accessControl/addVisit.vue'),
    meta: { requiresAuth: true, title: '登记访客' }
  },
  {
    path: 'pages/accessControl/editVisit',
    name: 'EditVisit',
    component: () => import(/* webpackChunkName: "accessControl-editVisit" */ '@/views/accessControl/editVisit.vue'),
    meta: { requiresAuth: true, title: '修改访客' }
  },
  {
    path: 'pages/accessControl/personFaceQrcode',
    name: 'PersonFaceQrcode',
    component: () => import(/* webpackChunkName: "accessControl-personFaceQrcode" */ '@/views/accessControl/personFaceQrcode.vue'),
    meta: { requiresAuth: true, title: '人脸二维码' }
  },
  {
    path: 'pages/accessControl/personFace',
    name: 'PersonFace',
    component: () => import(/* webpackChunkName: "accessControl-personFace" */ '@/views/accessControl/personFace.vue'),
    meta: { requiresAuth: true, title: '人脸上传' }
  },
  {
    path: 'pages/accessControl/passQrcodeSetting',
    name: 'PassQrcodeSetting',
    component: () => import(/* webpackChunkName: "accessControl-passQrcodeSetting" */ '@/views/accessControl/passQrcodeSetting.vue'),
    meta: { requiresAuth: true, title: '通行码设置' }
  },
  {
    path: 'pages/accessControl/passQrcode',
    name: 'PassQrcode',
    component: () => import(/* webpackChunkName: "accessControl-passQrcode" */ '@/views/accessControl/passQrcode.vue'),
    meta: { requiresAuth: true, title: '通行二维码' }
  }
]

export default accessControlRoutes
