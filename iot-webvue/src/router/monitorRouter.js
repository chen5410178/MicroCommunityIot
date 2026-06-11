const monitorRoutes = [
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
  }
]

export default monitorRoutes
