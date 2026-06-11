import request from '@/utils/request'

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

// ===== 岗亭列表 =====
export function listParkingBoxs(params) {
  return request({ url: '/parkingBox.listParkingBox', method: 'get', params })
}

// ===== 厂家列表（道闸类型 hmType=2002） =====
export function listHms(params) {
  return request({ url: '/hm.listHardwareManufacturer', method: 'get', params })
}

// ===== 访客码 =====
export function getVisitQrCodeUrl(params) {
  return request({ url: '/visit.getVisitQrCodeUrl', method: 'get', params })
}

// ===== 车辆进出记录 =====
export function listAdminCarInoutDetails(params) {
  return request({ url: '/carInoutDetail.listAdminCarInoutDetail', method: 'get', params })
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
