import request from '@/utils/request'

export function listMachineLocations(params) {
  return request({ url: '/location.listMachineLocation', method: 'get', params })
}
export function saveMachineLocation(data) {
  return request({ url: '/location.saveMachineLocation', method: 'post', data })
}
export function updateMachineLocation(data) {
  return request({ url: '/location.updateMachineLocation', method: 'post', data })
}
export function deleteMachineLocation(data) {
  return request({ url: '/location.deleteMachineLocation', method: 'post', data })
}
export function queryFloors(params) {
  return request({ url: '/floor.queryFloors', method: 'get', params })
}
export function queryUnits(params) {
  return request({ url: '/unit.queryUnits', method: 'get', params })
}
