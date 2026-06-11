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

// ===== 下发日志(全局) =====
export function listAccessControlLogs(params) {
  return request({ url: '/accessControlLog.listAccessControlLog', method: 'get', params })
}

export function listAccessControlLogParam(params) {
  return request({ url: '/accessControlLog.listAccessControlLogParam', method: 'get', params })
}

// ===== 进出记录(按设备) =====
export function listAdminAccessControlInout(params) {
  return request({ url: '/accessControlInout.listAdminAccessControlInout', method: 'get', params })
}

// ===== 访客类型 =====
export function listVisitTypes(params) {
  return request({ url: '/visitType.listVisitType', method: 'get', params })
}

export function saveVisitType(data) {
  return request({ url: '/visitType.saveVisitType', method: 'post', data })
}

export function updateVisitType(data) {
  return request({ url: '/visitType.updateVisitType', method: 'post', data })
}

export function deleteVisitType(data) {
  return request({ url: '/visitType.deleteVisitType', method: 'post', data })
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

// ===== 部门授权（独立页面，按组织视角） =====
export function listAccessControlOrg(params) {
  return request({ url: '/accessControlOrg.listAccessControlOrg', method: 'get', params })
}

export function saveAccessControlOrg(data) {
  return request({ url: '/accessControlOrg.saveAccessControlOrg', method: 'post', data })
}

export function deleteAccessControlOrg(data) {
  return request({ url: '/accessControlOrg.deleteAccessControlOrg', method: 'post', data })
}

// ===== 组织树 =====
export function listOrgTree(params) {
  return request({ url: '/org.listOrgTree', method: 'get', params })
}

// ===== 人脸二维码 =====
export function listPersonFaceQrcodes(params) {
  return request({ url: '/personFace.listPersonFaceQrcode', method: 'get', params })
}

export function savePersonFaceQrcode(data) {
  return request({ url: '/personFace.savePersonFaceQrcode', method: 'post', data })
}

export function updatePersonFaceQrcode(data) {
  return request({ url: '/personFace.updatePersonFaceQrcode', method: 'post', data })
}

export function deletePersonFaceQrcode(data) {
  return request({ url: '/personFace.deletePersonFaceQrcode', method: 'post', data })
}

// ===== 通行码设置 =====
export function listPassQrcodeSettings(params) {
  return request({ url: '/passcode.listPassQrcodeSetting', method: 'get', params })
}
export function savePassQrcodeSetting(data) {
  return request({ url: '/passcode.savePassQrcodeSetting', method: 'post', data })
}
export function updatePassQrcodeSetting(data) {
  return request({ url: '/passcode.updatePassQrcodeSetting', method: 'post', data })
}
export function deletePassQrcodeSetting(data) {
  return request({ url: '/passcode.deletePassQrcodeSetting', method: 'post', data })
}

// ===== 通行码 =====
export function listPassQrcodes(params) {
  return request({ url: '/passcode.listPassQrcode', method: 'get', params })
}
export function auditPassQrcode(data) {
  return request({ url: '/passcode.auditPassQrcode', method: 'post', data })
}
export function deletePassQrcode(data) {
  return request({ url: '/passcode.deletePassQrcode', method: 'post', data })
}

// ===== 访客管理 =====
export function listVisits(params) {
  return request({ url: '/visit.listVisit', method: 'get', params })
}
export function saveVisit(data) {
  return request({ url: '/visit.saveVisit', method: 'post', data })
}
export function updateVisit(data) {
  return request({ url: '/visit.updateVisit', method: 'post', data })
}
export function deleteVisit(data) {
  return request({ url: '/visit.deleteVisit', method: 'post', data })
}
export function propertyAuthVisit(data) {
  return request({ url: '/visit.propertyAuthVisit', method: 'post', data })
}
export function getVisitQrCodeUrl(params) {
  return request({ url: '/visit.getVisitQrCodeUrl', method: 'get', params })
}
export function listAccessControlInout(params) {
  return request({ url: '/accessControlInout.listAccessControlInout', method: 'get', params })
}

// ===== 广告管理 =====
export function listAccessControlAds(params) {
  return request({ url: '/ad.listAccessControlAd', method: 'get', params })
}

export function saveAccessControlAd(data) {
  return request({ url: '/ad.saveAccessControlAd', method: 'post', data })
}

export function updateAccessControlAd(data) {
  return request({ url: '/ad.updateAccessControlAd', method: 'post', data })
}

export function deleteAccessControlAd(data) {
  return request({ url: '/ad.deleteAccessControlAd', method: 'post', data })
}

// ===== 广告门禁关联 =====
export function listAccessControlAdRels(params) {
  return request({ url: '/ad.listAccessControlAdRel', method: 'get', params })
}

export function saveAccessControlAdRel(data) {
  return request({ url: '/ad.saveAccessControlAdRel', method: 'post', data })
}

export function deleteAccessControlAdRel(data) {
  return request({ url: '/ad.deleteAccessControlAdRel', method: 'post', data })
}

// ===== 门禁统计 =====
export function queryAcInoutStatistics(data) {
  return request({ url: '/accessControl.queryAcInoutStatistics', method: 'post', data })
}

export function queryAcLogStatistics(data) {
  return request({ url: '/accessControl.queryAcLogStatistics', method: 'post', data })
}

export function queryAcVisitStatistics(data) {
  return request({ url: '/accessControl.queryAcVisitStatistics', method: 'post', data })
}

export function queryAcFaceStatistics(data) {
  return request({ url: '/accessControl.queryAcFaceStatistics', method: 'post', data })
}

// ===== 云呼平台 =====
export function listCallingPlatform(params) {
  return request({ url: '/callingPlatform.listCallingPlatform', method: 'get', params })
}

export function saveCallingPlatform(data) {
  return request({ url: '/callingPlatform.saveCallingPlatform', method: 'post', data })
}

export function updateCallingPlatform(data) {
  return request({ url: '/callingPlatform.updateCallingPlatform', method: 'post', data })
}

export function deleteCallingPlatform(data) {
  return request({ url: '/callingPlatform.deleteCallingPlatform', method: 'post', data })
}

// ===== 呼叫设备 =====
export function listCallingDevice(params) {
  return request({ url: '/callingDevice.listCallingDevice', method: 'get', params })
}

export function saveCallingDevice(data) {
  return request({ url: '/callingDevice.saveCallingDevice', method: 'post', data })
}

export function updateCallingDevice(data) {
  return request({ url: '/callingDevice.updateCallingDevice', method: 'post', data })
}

export function deleteCallingDevice(data) {
  return request({ url: '/callingDevice.deleteCallingDevice', method: 'post', data })
}

// ===== 通讯录 =====
export function listCallingContact(params) {
  return request({ url: '/callingContact.listCallingContact', method: 'get', params })
}

export function saveCallingContact(data) {
  return request({ url: '/callingContact.saveCallingContact', method: 'post', data })
}

export function deleteCallingContact(data) {
  return request({ url: '/callingContact.deleteCallingContact', method: 'post', data })
}

// ===== 呼叫记录 =====
export function listIntercomLog(params) {
  return request({ url: '/ai.getIntercomLog', method: 'get', params })
}
