import request from '@/utils/request'

// ===== 门禁厂家 =====
export function listHardwareManufacturers(params) {
  return request({ url: '/hm.listHardwareManufacturer', method: 'get', params })
}
export function saveHardwareManufacturer(data) {
  return request({ url: '/hm.saveHardwareManufacturer', method: 'post', data })
}
export function updateHardwareManufacturer(data) {
  return request({ url: '/hm.updateHardwareManufacturer', method: 'post', data })
}
export function deleteHardwareManufacturer(data) {
  return request({ url: '/hm.deleteHardwareManufacturer', method: 'post', data })
}

// ===== 门禁厂家规格 =====
export function listHardwareManufacturerAttrs(params) {
  return request({ url: '/hm.listHardwareManufacturerAttr', method: 'get', params })
}
export function saveHardwareManufacturerAttr(data) {
  return request({ url: '/hm.saveHardwareManufacturerAttr', method: 'post', data })
}
export function deleteHardwareManufacturerAttr(data) {
  return request({ url: '/hm.deleteHardwareManufacturerAttr', method: 'post', data })
}
