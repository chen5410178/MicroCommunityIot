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
  }
]

export default attendanceRoutes
