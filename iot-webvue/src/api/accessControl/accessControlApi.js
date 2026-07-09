import request from '@/utils/request'

// ===== 门禁设备 =====
export function listAdminAccessControl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.listAdminAccessControl',
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

export function saveAccessControl(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.saveAccessControl',
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

export function updateAccessControl(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.updateAccessControl',
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

export function deleteAccessControl(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.deleteAccessControl',
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

// ===== 门禁设备操作 =====
export function openAccessControlDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.openAccessControlDoor',
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

export function restartAccessControlDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.restartAccessControlDoor',
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

export function startAccessControlDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.StartAccessControlDoor',
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

export function stopAccessControlDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.StopAccessControlDoor',
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

export function reSendAccessControlFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.reSendAccessControlFace',
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

// ===== 门禁人员(按设备) =====
export function listAdminAccessControlFace(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFace.listAdminAccessControlFace',
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

// ===== 门禁人员（独立页面） =====
export function listAccessControlFace(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFace.listAccessControlFace',
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

export function saveAccessControlFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFace.saveAccessControlFace',
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

export function deleteAccessControlFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFace.deleteAccessControlFace',
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

export function listAccessControl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.listAccessControl',
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

// ===== 楼栋授权(按设备) =====
export function listAdminAccessControlFloor(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFloor.listAdminAccessControlFloor',
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

export function saveAccessControlFloor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFloor.saveAccessControlFloor',
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

export function deleteAccessControlFloor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlFloor.deleteAccessControlFloor',
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

// ===== 部门授权(按设备) =====
export function listAdminAccessControlOrg(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlOrg.listAdminAccessControlOrg',
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

// ===== 下发日志(按设备) =====
export function listAdminAccessControlLog(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlLog.listAdminAccessControlLog',
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

// ===== 下发日志(全局) =====
export function listAccessControlLogs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlLog.listAccessControlLog',
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

export function listAccessControlLogParam(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlLog.listAccessControlLogParam',
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

// ===== 进出记录(按设备) =====
export function listAdminAccessControlInout(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlInout.listAdminAccessControlInout',
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

// ===== 访客类型 =====
export function listVisitTypes(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visitType.listVisitType',
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

export function saveVisitType(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visitType.saveVisitType',
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

export function updateVisitType(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visitType.updateVisitType',
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

export function deleteVisitType(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visitType.deleteVisitType',
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

// ===== 下拉选项加载 =====
export function listMachineLocation(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/location.listMachineLocation',
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

export function listHardwareManufacturer(params) {
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

export function listMonitorMachine(params) {
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

// ===== 部门授权（独立页面，按组织视角） =====
export function listAccessControlOrg(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlOrg.listAccessControlOrg',
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

export function saveAccessControlOrg(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlOrg.saveAccessControlOrg',
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

export function deleteAccessControlOrg(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlOrg.deleteAccessControlOrg',
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

// ===== 组织树 =====
export function listOrgTree(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/org.listOrgTree',
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

// ===== 人脸二维码 =====
export function listPersonFaceQrcodes(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.listPersonFaceQrcode',
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

export function savePersonFaceQrcode(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.savePersonFaceQrcode',
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

export function updatePersonFaceQrcode(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.updatePersonFaceQrcode',
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

export function deletePersonFaceQrcode(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.deletePersonFaceQrcode',
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

// ===== 通行码设置 =====
export function listPassQrcodeSettings(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.listPassQrcodeSetting',
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

export function savePassQrcodeSetting(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.savePassQrcodeSetting',
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

export function updatePassQrcodeSetting(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.updatePassQrcodeSetting',
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

export function deletePassQrcodeSetting(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.deletePassQrcodeSetting',
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

// ===== 通行码 =====
export function listPassQrcodes(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.listPassQrcode',
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

export function auditPassQrcode(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.auditPassQrcode',
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

export function deletePassQrcode(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/passcode.deletePassQrcode',
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

// ===== 访客管理 =====
export function listVisits(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.listVisit',
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

export function saveVisit(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.saveVisit',
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

export function updateVisit(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.updateVisit',
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

export function deleteVisit(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.deleteVisit',
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

export function propertyAuthVisit(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.propertyAuthVisit',
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

export function getVisitQrCodeUrl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/visit.getVisitQrCodeUrl',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAccessControlInout(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControlInout.listAccessControlInout',
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

// ===== 广告管理 =====
export function listAccessControlAds(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.listAccessControlAd',
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

export function saveAccessControlAd(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.saveAccessControlAd',
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

export function updateAccessControlAd(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.updateAccessControlAd',
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

export function deleteAccessControlAd(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.deleteAccessControlAd',
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

// ===== 广告门禁关联 =====
export function listAccessControlAdRels(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.listAccessControlAdRel',
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

export function saveAccessControlAdRel(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.saveAccessControlAdRel',
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

export function deleteAccessControlAdRel(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ad.deleteAccessControlAdRel',
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

// ===== 门禁统计 =====
export function queryAcInoutStatistics(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.queryAcInoutStatistics',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryAcLogStatistics(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.queryAcLogStatistics',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryAcVisitStatistics(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.queryAcVisitStatistics',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryAcFaceStatistics(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/accessControl.queryAcFaceStatistics',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 云呼平台 =====
export function listCallingPlatform(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingPlatform.listCallingPlatform',
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

export function saveCallingPlatform(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingPlatform.saveCallingPlatform',
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

export function updateCallingPlatform(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingPlatform.updateCallingPlatform',
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

export function deleteCallingPlatform(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingPlatform.deleteCallingPlatform',
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

// ===== 呼叫设备 =====
export function listCallingDevice(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.listCallingDevice',
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

export function saveCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.saveCallingDevice',
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

export function updateCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.updateCallingDevice',
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

export function deleteCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.deleteCallingDevice',
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

// ===== 通讯录 =====
export function listCallingContact(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingContact.listCallingContact',
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

export function saveCallingContact(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingContact.saveCallingContact',
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

export function deleteCallingContact(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingContact.deleteCallingContact',
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

// ===== 呼叫记录 =====
export function listIntercomLog(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.getIntercomLog',
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
