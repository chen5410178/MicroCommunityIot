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
