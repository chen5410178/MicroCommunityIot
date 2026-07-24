import request from '@/utils/request'

// ===== 车位地磁 =====
export function listParkingSpaceMachines(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpaceMachine.listParkingSpaceMachine',
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

export function saveParkingSpaceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpaceMachine.saveParkingSpaceMachine',
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

export function updateParkingSpaceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpaceMachine.updateParkingSpaceMachine',
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

export function deleteParkingSpaceMachine(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpaceMachine.deleteParkingSpaceMachine',
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

// ===== 地磁厂家 =====
export function listSpaceMachineFactorys(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/spaceMachineFactory.listSpaceMachineFactory',
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

export function saveSpaceMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/spaceMachineFactory.saveSpaceMachineFactory',
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

export function updateSpaceMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/spaceMachineFactory.updateSpaceMachineFactory',
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

export function deleteSpaceMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/spaceMachineFactory.deleteSpaceMachineFactory',
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

// ===== 道闸摄像头（adminBarrier） =====
export function listAdminBarriers(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.listAdminBarrier',
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

// ===== 道闸摄像头（barrier CRUD） =====
export function listBarriers(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.listBarrier',
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

export function saveBarrier(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.saveBarrier',
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

export function updateBarrier(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.updateBarrier',
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

export function deleteBarrier(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.deleteBarrier',
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

// ===== 岗亭 CRUD =====
export function listParkingBoxs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBox.listParkingBox',
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

export function saveParkingBox(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBox.saveParkingBox',
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

export function updateParkingBox(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBox.updateParkingBox',
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

export function deleteParkingBox(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBox.deleteParkingBox',
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

// ===== 岗亭-停车场关联 =====
export function listParkingBoxAreas(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBoxArea.listParkingBoxArea',
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

export function saveParkingBoxArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBoxArea.saveParkingBoxArea',
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

export function updateParkingBoxArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBoxArea.updateParkingBoxArea',
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

export function deleteParkingBoxArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingBoxArea.deleteParkingBoxArea',
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

// ===== 厂家列表（道闸类型 hmType=2002） =====
export function listHms(params) {
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

// ===== 停车场 =====
export function listParkingAreas(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingArea.listParkingArea',
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

export function saveParkingArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingArea.saveParkingArea',
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

export function updateParkingArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingArea.updateParkingArea',
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

export function deleteParkingArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingArea.deleteParkingArea',
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

// ===== 停车区域 =====
export function listParkingRegions(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingRegion.listParkingRegion',
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

export function saveParkingRegion(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingRegion.saveParkingRegion',
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

export function updateParkingRegion(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingRegion.updateParkingRegion',
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

export function deleteParkingRegion(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingRegion.deleteParkingRegion',
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

// ===== 访客码 =====
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

// ===== 车辆进出记录 =====
export function listCarInoutDetails(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutDetail.listCarInoutDetail',
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

export function listAdminCarInoutDetails(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutDetail.listAdminCarInoutDetail',
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

// ===== 停车位 =====
export function listParkingSpaces(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpace.listParkingSpace',
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

export function saveParkingSpace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpace.saveParkingSpace',
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

export function updateParkingSpace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpace.updateParkingSpace',
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

export function deleteParkingSpace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpace.deleteParkingSpace',
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

// ===== 手动出场相关 =====
export function listCarInParkingArea(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.listCarInParkingArea',
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

export function customCarInOut(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.customCarInOut',
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

export function listParkingCouponCars(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listParkingCouponCar',
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

export function getTempCarFeeOrder(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/tempCarFee.getTempCarFeeOrder',
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

// ===== 业主车辆 =====
export function queryOwnerCars(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ownerCar.queryOwnerCars',
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

export function saveOwnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ownerCar.saveOwnerCar',
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

export function updateOwnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ownerCar.updateOwnerCar',
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

export function deleteOwnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ownerCar.deleteOwnerCar',
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

export function deleteCarParkingSpace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ownerCar.deleteCarParkingSpace',
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

export function carAddParkingSpace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingSpace.carAddParkingSpace',
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

// ===== 黑白名单 =====
export function listCarBlackWhites(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carBlackWhite.listCarBlackWhites',
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

export function saveCarBlackWhite(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carBlackWhite.saveCarBlackWhite',
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

export function updateCarBlackWhite(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carBlackWhite.updateCarBlackWhite',
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

export function deleteCarBlackWhite(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carBlackWhite.deleteCarBlackWhite',
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

// ===== 导出 =====
export function exportData(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/export.exportData',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '导出失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 业主/成员车辆查询 =====
export function queryOwnerCarsByOwner(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/owner.queryOwnerCars',
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

export function queryOwners(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/owner.queryOwners',
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

// ===== 导入 =====
export function assetImport(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/assetImport',
      method: 'post',
      data,
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '导入失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 停车月卡 =====
export function listCarMonthCards(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.listCarMonthCard',
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

export function saveCarMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.saveCarMonthCard',
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

export function updateCarMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.updateCarMonthCard',
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

export function deleteCarMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.deleteCarMonthCard',
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

// ===== 月卡订单 =====
export function listCarMonthOrders(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.listCarMonthOrder',
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

export function buyCarMonthOrder(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.buyCarMonthOrder',
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

export function refundCarMonthOrder(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.refundCarMonthOrder',
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

export function getBuyCarMonthCardQrCodeUrl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carMonth.getBuyCarMonthCardQrCodeUrl',
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

// ===== 临时车收费标准 =====
export function listTempCarFeeConfigs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/fee.listTempCarFeeConfigs',
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

export function saveTempCarFeeConfig(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/fee.saveTempCarFeeConfig',
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

export function updateTempCarFeeConfig(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/fee.updateTempCarFeeConfig',
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

export function deleteTempCarFeeConfig(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/fee.deleteTempCarFeeConfig',
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

export function queryTempCarFeeRules(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/tempCarFee.queryTempCarFeeRules',
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

// ===== 开/关闸 =====
export function openBarrierDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.openBarrierDoor',
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

export function closeBarrierDoor(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.closeBarrierDoor',
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

// ===== 云FLV视频 =====
export function getCloudFlvVideo(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.getCloudFlvVideo',
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

// ===== 剩余车位数 =====
export function getFreeParkingSpace(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.getFreeParkingSpace',
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

// ===== 修改车牌号 =====
export function updateCarInoutNum(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.updateCarInoutNum',
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

// ===== 异常出场 =====
export function exceptionCarOut(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.exceptionCarOut',
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

// ===== 无牌车进场二维码 =====
export function getUnlicensedCarMachineQrCodeUrl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.unlicensedCarMachineQrCodeUrl',
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

// ===== 设备二维码 =====
export function getCarMachineQrCodeUrl(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/barrier.getCarMachineQrCodeUrl',
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

// ===== 收费明细 =====
export function listCarInoutPayments(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutPayment.listCarInoutPayment',
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

// ===== 收费统计 =====
export function listCarInoutPaymentSummary(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutPayment.listCarInoutPaymentSummary',
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

// ===== 开闸日志 =====
export function listManualOpenDoorLogs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.listManualOpenDoorLog',
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

// ===== 临时内部车 =====
export function listTempInnerCars(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/car.listTempInnerCars',
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

// ===== 临时车审核 =====
export function listCarInoutTempAuth(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInout.listCarInoutTempAuth',
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

export function tempCarAuth(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/tempCarFee.tempCarAuth',
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

export function saveTempInnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/car.saveTempInnerCar',
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

export function updateTempInnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/car.updateTempInnerCar',
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

export function deleteTempInnerCar(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/car.deleteTempInnerCar',
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

// ===== 车辆出入历史记录 =====
export function listCarInoutHisDetails(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutHis.listCarInoutHis',
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

export function deleteCarInoutHis(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/carInoutHis.deleteCarInoutHis',
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
