import request from '@/utils/request'

// ===== 考勤打卡记录 =====
export function listAttendanceCheckin(params) {
  return request({ url: '/attendance.listAttendanceCheckin', method: 'get', params })
}

// ===== 考勤设备 =====
export function listAttendanceMachines(params) {
  return request({ url: '/attendance.listAttendanceMachine', method: 'get', params })
}
export function saveAttendanceMachine(data) {
  return request({ url: '/attendance.saveAttendanceMachine', method: 'post', data })
}
export function updateAttendanceMachine(data) {
  return request({ url: '/attendance.updateAttendanceMachine', method: 'post', data })
}
export function deleteAttendanceMachine(data) {
  return request({ url: '/attendance.deleteAttendanceMachine', method: 'post', data })
}

// ===== 下拉选项加载 =====
export function listHardwareManufacturers(params) {
  return request({ url: '/hm.listHardwareManufacturer', method: 'get', params })
}
export function listMonitorMachines(params) {
  return request({ url: '/monitorMachine.listMonitorMachine', method: 'get', params })
}
