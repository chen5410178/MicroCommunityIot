import request from '@/utils/request'

// ===== 车位地磁 =====
export function listParkingSpaceMachines(params) {
  return request({ url: '/parkingSpaceMachine.listParkingSpaceMachine', method: 'get', params })
}
export function saveParkingSpaceMachine(data) {
  return request({ url: '/parkingSpaceMachine.saveParkingSpaceMachine', method: 'post', data })
}
export function updateParkingSpaceMachine(data) {
  return request({ url: '/parkingSpaceMachine.updateParkingSpaceMachine', method: 'post', data })
}
export function deleteParkingSpaceMachine(data) {
  return request({ url: '/parkingSpaceMachine.deleteParkingSpaceMachine', method: 'post', data })
}

// ===== 地磁厂家 =====
export function listSpaceMachineFactorys(params) {
  return request({ url: '/spaceMachineFactory.listSpaceMachineFactory', method: 'get', params })
}
export function saveSpaceMachineFactory(data) {
  return request({ url: '/spaceMachineFactory.saveSpaceMachineFactory', method: 'post', data })
}
export function updateSpaceMachineFactory(data) {
  return request({ url: '/spaceMachineFactory.updateSpaceMachineFactory', method: 'post', data })
}
export function deleteSpaceMachineFactory(data) {
  return request({ url: '/spaceMachineFactory.deleteSpaceMachineFactory', method: 'post', data })
}

// ===== 道闸摄像头（adminBarrier） =====
export function listAdminBarriers(params) {
  return request({ url: '/barrier.listAdminBarrier', method: 'get', params })
}

// ===== 道闸摄像头（barrier CRUD） =====
export function listBarriers(params) {
  return request({ url: '/barrier.listBarrier', method: 'get', params })
}
export function saveBarrier(data) {
  return request({ url: '/barrier.saveBarrier', method: 'post', data })
}
export function updateBarrier(data) {
  return request({ url: '/barrier.updateBarrier', method: 'post', data })
}
export function deleteBarrier(data) {
  return request({ url: '/barrier.deleteBarrier', method: 'post', data })
}

// ===== 岗亭 CRUD =====
export function listParkingBoxs(params) {
  return request({ url: '/parkingBox.listParkingBox', method: 'get', params })
}
export function saveParkingBox(data) {
  return request({ url: '/parkingBox.saveParkingBox', method: 'post', data })
}
export function updateParkingBox(data) {
  return request({ url: '/parkingBox.updateParkingBox', method: 'post', data })
}
export function deleteParkingBox(data) {
  return request({ url: '/parkingBox.deleteParkingBox', method: 'post', data })
}

// ===== 岗亭-停车场关联 =====
export function listParkingBoxAreas(params) {
  return request({ url: '/parkingBoxArea.listParkingBoxArea', method: 'get', params })
}
export function saveParkingBoxArea(data) {
  return request({ url: '/parkingBoxArea.saveParkingBoxArea', method: 'post', data })
}
export function updateParkingBoxArea(data) {
  return request({ url: '/parkingBoxArea.updateParkingBoxArea', method: 'post', data })
}
export function deleteParkingBoxArea(data) {
  return request({ url: '/parkingBoxArea.deleteParkingBoxArea', method: 'post', data })
}

// ===== 厂家列表（道闸类型 hmType=2002） =====
export function listHms(params) {
  return request({ url: '/hm.listHardwareManufacturer', method: 'get', params })
}

// ===== 停车场 =====
export function listParkingAreas(params) {
  return request({ url: '/parkingArea.listParkingArea', method: 'get', params })
}
export function saveParkingArea(data) {
  return request({ url: '/parkingArea.saveParkingArea', method: 'post', data })
}
export function updateParkingArea(data) {
  return request({ url: '/parkingArea.updateParkingArea', method: 'post', data })
}
export function deleteParkingArea(data) {
  return request({ url: '/parkingArea.deleteParkingArea', method: 'post', data })
}

// ===== 停车区域 =====
export function listParkingRegions(params) {
  return request({ url: '/parkingRegion.listParkingRegion', method: 'get', params })
}
export function saveParkingRegion(data) {
  return request({ url: '/parkingRegion.saveParkingRegion', method: 'post', data })
}
export function updateParkingRegion(data) {
  return request({ url: '/parkingRegion.updateParkingRegion', method: 'post', data })
}
export function deleteParkingRegion(data) {
  return request({ url: '/parkingRegion.deleteParkingRegion', method: 'post', data })
}

// ===== 访客码 =====
export function getVisitQrCodeUrl(params) {
  return request({ url: '/visit.getVisitQrCodeUrl', method: 'get', params })
}

// ===== 车辆进出记录 =====
export function listAdminCarInoutDetails(params) {
  return request({ url: '/carInoutDetail.listAdminCarInoutDetail', method: 'get', params })
}

// ===== 停车位 =====
export function listParkingSpaces(params) {
  return request({ url: '/parkingSpace.listParkingSpace', method: 'get', params })
}
export function saveParkingSpace(data) {
  return request({ url: '/parkingSpace.saveParkingSpace', method: 'post', data })
}
export function updateParkingSpace(data) {
  return request({ url: '/parkingSpace.updateParkingSpace', method: 'post', data })
}
export function deleteParkingSpace(data) {
  return request({ url: '/parkingSpace.deleteParkingSpace', method: 'post', data })
}

// ===== 手动出场相关 =====
export function listCarInParkingArea(params) {
  return request({ url: '/carInout.listCarInParkingArea', method: 'get', params })
}
export function customCarInOut(data) {
  return request({ url: '/barrier.customCarInOut', method: 'post', data })
}
export function listParkingCouponCars(params) {
  return request({ url: '/parkingCoupon.listParkingCouponCar', method: 'get', params })
}
export function getTempCarFeeOrder(params) {
  return request({ url: '/tempCarFee.getTempCarFeeOrder', method: 'get', params })
}

// ===== 业主车辆 =====
export function queryOwnerCars(params) {
  return request({ url: '/ownerCar.queryOwnerCars', method: 'get', params })
}
export function saveOwnerCar(data) {
  return request({ url: '/ownerCar.saveOwnerCar', method: 'post', data })
}
export function updateOwnerCar(data) {
  return request({ url: '/ownerCar.updateOwnerCar', method: 'post', data })
}
export function deleteOwnerCar(data) {
  return request({ url: '/ownerCar.deleteOwnerCar', method: 'post', data })
}
export function deleteCarParkingSpace(data) {
  return request({ url: '/ownerCar.deleteCarParkingSpace', method: 'post', data })
}
export function carAddParkingSpace(data) {
  return request({ url: '/parkingSpace.carAddParkingSpace', method: 'post', data })
}

// ===== 黑白名单 =====
export function listCarBlackWhites(params) {
  return request({ url: '/carBlackWhite.listCarBlackWhites', method: 'get', params })
}
export function saveCarBlackWhite(data) {
  return request({ url: '/carBlackWhite.saveCarBlackWhite', method: 'post', data })
}
export function updateCarBlackWhite(data) {
  return request({ url: '/carBlackWhite.updateCarBlackWhite', method: 'post', data })
}
export function deleteCarBlackWhite(data) {
  return request({ url: '/carBlackWhite.deleteCarBlackWhite', method: 'post', data })
}

// ===== 导出 =====
export function exportData(params) {
  return request({ url: '/export.exportData', method: 'get', params })
}

// ===== 业主/成员车辆查询 =====
export function queryOwnerCarsByOwner(params) {
  return request({ url: '/owner.queryOwnerCars', method: 'get', params })
}
export function queryOwners(params) {
  return request({ url: '/owner.queryOwners', method: 'get', params })
}

// ===== 导入 =====
export function assetImport(data) {
  return request({ url: '/assetImport', method: 'post', data, headers: { 'Content-Type': 'multipart/form-data' } })
}

// ===== 停车月卡 =====
export function listCarMonthCards(params) {
  return request({ url: '/carMonth.listCarMonthCard', method: 'get', params })
}
export function saveCarMonthCard(data) {
  return request({ url: '/carMonth.saveCarMonthCard', method: 'post', data })
}
export function updateCarMonthCard(data) {
  return request({ url: '/carMonth.updateCarMonthCard', method: 'post', data })
}
export function deleteCarMonthCard(data) {
  return request({ url: '/carMonth.deleteCarMonthCard', method: 'post', data })
}

// ===== 月卡订单 =====
export function listCarMonthOrders(params) {
  return request({ url: '/carMonth.listCarMonthOrder', method: 'get', params })
}
export function buyCarMonthOrder(data) {
  return request({ url: '/carMonth.buyCarMonthOrder', method: 'post', data })
}
export function refundCarMonthOrder(data) {
  return request({ url: '/carMonth.refundCarMonthOrder', method: 'post', data })
}
export function getBuyCarMonthCardQrCodeUrl(params) {
  return request({ url: '/carMonth.getBuyCarMonthCardQrCodeUrl', method: 'get', params })
}

// ===== 临时车收费标准 =====
export function listTempCarFeeConfigs(params) {
  return request({ url: '/fee.listTempCarFeeConfigs', method: 'get', params })
}
export function saveTempCarFeeConfig(data) {
  return request({ url: '/fee.saveTempCarFeeConfig', method: 'post', data })
}
export function updateTempCarFeeConfig(data) {
  return request({ url: '/fee.updateTempCarFeeConfig', method: 'post', data })
}
export function deleteTempCarFeeConfig(data) {
  return request({ url: '/fee.deleteTempCarFeeConfig', method: 'post', data })
}
export function queryTempCarFeeRules(params) {
  return request({ url: '/tempCarFee.queryTempCarFeeRules', method: 'get', params })
}

// ===== 开/关闸 =====
export function openBarrierDoor(data) {
  return request({ url: '/barrier.openBarrierDoor', method: 'post', data })
}
export function closeBarrierDoor(data) {
  return request({ url: '/barrier.closeBarrierDoor', method: 'post', data })
}
// ===== 云FLV视频 =====
export function getCloudFlvVideo(params) {
  return request({ url: '/barrier.getCloudFlvVideo', method: 'get', params })
}
// ===== 剩余车位数 =====
export function getFreeParkingSpace(params) {
  return request({ url: '/carInout.getFreeParkingSpace', method: 'get', params })
}
// ===== 修改车牌号 =====
export function updateCarInoutNum(data) {
  return request({ url: '/carInout.updateCarInoutNum', method: 'post', data })
}
// ===== 异常出场 =====
export function exceptionCarOut(data) {
  return request({ url: '/carInout.exceptionCarOut', method: 'post', data })
}
// ===== 设备二维码 =====
export function getCarMachineQrCodeUrl(params) {
  return request({ url: '/barrier.getCarMachineQrCodeUrl', method: 'get', params })
}
// ===== 收费明细 =====
export function listCarInoutPayments(params) {
  return request({ url: '/carInoutPayment.listCarInoutPayment', method: 'get', params })
}
// ===== 收费统计 =====
export function listCarInoutPaymentSummary(params) {
  return request({ url: '/carInoutPayment.listCarInoutPaymentSummary', method: 'get', params })
}
// ===== 开闸日志 =====
export function listManualOpenDoorLogs(params) {
  return request({ url: '/carInout.listManualOpenDoorLog', method: 'get', params })
}

// ===== 临时内部车 =====
export function listTempInnerCars(params) {
  return request({ url: '/car.listTempInnerCars', method: 'get', params })
}
// ===== 临时车审核 =====
export function listCarInoutTempAuth(params) {
  return request({ url: '/carInout.listCarInoutTempAuth', method: 'get', params })
}
export function tempCarAuth(data) {
  return request({ url: '/tempCarFee.tempCarAuth', method: 'post', data })
}
export function saveTempInnerCar(data) {
  return request({ url: '/car.saveTempInnerCar', method: 'post', data })
}
export function updateTempInnerCar(data) {
  return request({ url: '/car.updateTempInnerCar', method: 'post', data })
}
export function deleteTempInnerCar(data) {
  return request({ url: '/car.deleteTempInnerCar', method: 'post', data })
}
