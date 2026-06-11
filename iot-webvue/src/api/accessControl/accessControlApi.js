import request from '@/utils/request'

// ===== 门禁设备 =====
export function listAdminAccessControl(params) {
  return request({ url: '/accessControl.listAdminAccessControl', method: 'get', params })
}

export function saveAccessControl(data) {
  return request({ url: '/accessControl.saveAccessControl', method: 'post', data })
}

export function updateAccessControl(data) {
  return request({ url: '/accessControl.updateAccessControl', method: 'post', data })
}

export function deleteAccessControl(data) {
  return request({ url: '/accessControl.deleteAccessControl', method: 'post', data })
}

// ===== 门禁设备操作 =====
export function openAccessControlDoor(data) {
  return request({ url: '/accessControl.openAccessControlDoor', method: 'post', data })
}

export function restartAccessControlDoor(data) {
  return request({ url: '/accessControl.restartAccessControlDoor', method: 'post', data })
}

export function startAccessControlDoor(data) {
  return request({ url: '/accessControl.StartAccessControlDoor', method: 'post', data })
}

export function stopAccessControlDoor(data) {
  return request({ url: '/accessControl.StopAccessControlDoor', method: 'post', data })
}

export function reSendAccessControlFace(data) {
  return request({ url: '/accessControl.reSendAccessControlFace', method: 'post', data })
}

// ===== 门禁人员(按设备) =====
export function listAdminAccessControlFace(params) {
  return request({ url: '/accessControlFace.listAdminAccessControlFace', method: 'get', params })
}

// ===== 门禁人员（独立页面） =====
export function listAccessControlFace(params) {
  return request({ url: '/accessControlFace.listAccessControlFace', method: 'get', params })
}

export function saveAccessControlFace(data) {
  return request({ url: '/accessControlFace.saveAccessControlFace', method: 'post', data })
}

export function deleteAccessControlFace(data) {
  return request({ url: '/accessControlFace.deleteAccessControlFace', method: 'post', data })
}

export function listAccessControl(params) {
  return request({ url: '/accessControl.listAccessControl', method: 'get', params })
}

// ===== 楼栋授权(按设备) =====
export function listAdminAccessControlFloor(params) {
  return request({ url: '/accessControlFloor.listAdminAccessControlFloor', method: 'get', params })
}

export function saveAccessControlFloor(data) {
  return request({ url: '/accessControlFloor.saveAccessControlFloor', method: 'post', data })
}

export function deleteAccessControlFloor(data) {
  return request({ url: '/accessControlFloor.deleteAccessControlFloor', method: 'post', data })
}

// ===== 部门授权(按设备) =====
export function listAdminAccessControlOrg(params) {
  return request({ url: '/accessControlOrg.listAdminAccessControlOrg', method: 'get', params })
}

// ===== 下发日志(按设备) =====
export function listAdminAccessControlLog(params) {
  return request({ url: '/accessControlLog.listAdminAccessControlLog', method: 'get', params })
}

// ===== 进出记录(按设备) =====
export function listAdminAccessControlInout(params) {
  return request({ url: '/accessControlInout.listAdminAccessControlInout', method: 'get', params })
}

// ===== 下拉选项加载 =====
export function listMachineLocation(params) {
  return request({ url: '/location.listMachineLocation', method: 'get', params })
}

export function listHardwareManufacturer(params) {
  return request({ url: '/hm.listHardwareManufacturer', method: 'get', params })
}

export function listMonitorMachine(params) {
  return request({ url: '/monitorMachine.listMonitorMachine', method: 'get', params })
}
