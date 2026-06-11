const attendanceRoutes = [
  {
    path: 'pages/attendance/attendanceCheckIn',
    name: 'AttendanceCheckIn',
    component: () => import('@/views/attendance/attendanceCheckIn.vue'),
    meta: { requiresAuth: true, title: '考勤打卡记录' }
  },
  {
    path: 'pages/attendance/attendanceMachine',
    name: 'AttendanceMachine',
    component: () => import('@/views/attendance/attendanceMachine.vue'),
    meta: { requiresAuth: true, title: '考勤设备' }
  },
  {
    path: 'pages/attendance/attendanceMachineLog',
    name: 'AttendanceMachineLog',
    component: () => import('@/views/attendance/attendanceMachineLog.vue'),
    meta: { requiresAuth: true, title: '考勤设备日志' }
  },
  {
    path: 'pages/attendance/attendanceStaff',
    name: 'AttendanceStaff',
    component: () => import('@/views/attendance/attendanceStaffManage.vue'),
    meta: { requiresAuth: true, title: '考勤员工' }
  }
]

export default attendanceRoutes
