import request from '@/utils/request'

// ===== 考勤打卡记录 =====
export function listAttendanceCheckin(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.listAttendanceCheckin',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 考勤设备 =====
export function listAttendanceMachines(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.listAttendanceMachine',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAttendanceMachine(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.listAttendanceMachine',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveAttendanceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.saveAttendanceMachine',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateAttendanceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.updateAttendanceMachine',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteAttendanceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.deleteAttendanceMachine',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 考勤人员 =====
export function listAttendanceStaffs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.listAttendanceStaff',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteAttendanceStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.deleteAttendanceStaff',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 设备日志 =====
export function listAttendanceMachineLog(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/attendance.listAttendanceMachineLog',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 下拉选项加载 =====
export function listHardwareManufacturers(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.listHardwareManufacturer',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMonitorMachines(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/monitorMachine.listMonitorMachine',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
