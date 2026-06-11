import request from '@/utils/request'

// ===== 门禁设备 =====
export function listAdminAccessControl(params) {
  return request({ url: '/accessControl.listAdminAccessControl', method: 'get', params })
}

// ===== 门禁人员(按设备) =====
export function listAdminAccessControlFace(params) {
  return request({ url: '/accessControlFace.listAdminAccessControlFace', method: 'get', params })
}

// ===== 楼栋授权(按设备) =====
export function listAdminAccessControlFloor(params) {
  return request({ url: '/accessControlFloor.listAdminAccessControlFloor', method: 'get', params })
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
