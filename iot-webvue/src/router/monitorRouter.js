const monitorRoutes = [
  {
    path: 'pages/monitor/monitorManufactor',
    name: 'MonitorManufactor',
    component: () => import('@/views/monitor/monitorManufactor.vue'),
    meta: { requiresAuth: true, title: '监控平台' }
  },
  {
    path: 'pages/monitor/monitorModelManage',
    name: 'MonitorModelManage',
    component: () => import('@/views/monitor/monitorModelManage.vue'),
    meta: { requiresAuth: true, title: '监控设备型号' }
  },
  {
    path: 'pages/monitor/monitorAreaManage',
    name: 'MonitorAreaManage',
    component: () => import('@/views/monitor/monitorAreaManage.vue'),
    meta: { requiresAuth: true, title: '监控区域管理' }
  },
  {
    path: 'pages/monitor/cameraControlVideo',
    name: 'CameraControlVideo',
    component: () => import('@/views/monitor/cameraControlVideo.vue'),
    meta: { requiresAuth: true, title: '监控预览' }
  },
  {
    path: 'pages/monitor/monitorRecord',
    name: 'MonitorRecord',
    component: () => import('@/views/monitor/monitorRecord.vue'),
    meta: { requiresAuth: true, title: '监控录像' }
  },
  {
    path: 'pages/monitor/monitorMachineModelManage',
    name: 'MonitorMachineModelManage',
    component: () => import('@/views/monitor/monitorMachineModelManage.vue'),
    meta: { requiresAuth: true, title: '监控分析' }
  },
  {
    path: 'pages/monitor/addMonitorMachineModel',
    name: 'AddMonitorMachineModel',
    component: () => import('@/views/monitor/addMonitorMachineModel.vue'),
    meta: { requiresAuth: true, title: '添加监控设备' }
  },
  {
    path: 'pages/monitor/monitorMachineStaff',
    name: 'MonitorMachineStaff',
    component: () => import('@/views/monitor/monitorMachineStaff.vue'),
    meta: { requiresAuth: true, title: '员工授权' }
  },
  {
    path: 'pages/monitor/addMonitorMachineStaff',
    name: 'AddMonitorMachineStaff',
    component: () => import('@/views/monitor/addMonitorMachineStaff.vue'),
    meta: { requiresAuth: true, title: '新增授权' }
  },
  {
    path: 'pages/monitor/monitorMachineOwner',
    name: 'MonitorMachineOwner',
    component: () => import('@/views/monitor/monitorMachineOwner.vue'),
    meta: { requiresAuth: true, title: '业主授权' }
  },
  {
    path: 'pages/monitor/addMonitorMachineOwner',
    name: 'AddMonitorMachineOwner',
    component: () => import('@/views/monitor/addMonitorMachineOwner.vue'),
    meta: { requiresAuth: true, title: '添加业主授权' }
  },
  {
    path: 'pages/ai/listDispatchFace',
    name: 'ListDispatchFace',
    component: () => import('@/views/ai/listDispatchFace.vue'),
    meta: { requiresAuth: true, title: '人员布控' }
  },
  {
    path: 'pages/ai/dispatchLog',
    name: 'DispatchLog',
    component: () => import('@/views/ai/dispatchLog.vue'),
    meta: { requiresAuth: true, title: '布控告警' }
  },
  {
    path: 'pages/monitor/machineCollect',
    name: 'MachineCollect',
    component: () => import('@/views/monitor/machineCollect.vue'),
    meta: { requiresAuth: true, title: '收藏视频' }
  },
  {
    path: 'pages/monitor/monitorMachineManage',
    name: 'MonitorMachineManage',
    component: () => import('@/views/monitor/monitorMachineManage.vue'),
    meta: { requiresAuth: true, title: '监控设备' }
  },
  {
    path: 'pages/monitor/monitorPoolManage',
    name: 'MonitorPoolManage',
    component: () => import('@/views/monitor/monitorPoolManage.vue'),
    meta: { requiresAuth: true, title: '监控池管理' }
  },
  {
    path: 'pages/ai/dispatchFace',
    name: 'DispatchFace',
    component: () => import('@/views/ai/listDispatchFace.vue'),
    meta: { requiresAuth: true, title: '人脸布控' }
  }
]

export default monitorRoutes
