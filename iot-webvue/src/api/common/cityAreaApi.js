import request from '@/utils/request'

export function queryAreaTree(params) {
  return request({ url: '/area.queryAreaTree', method: 'get', params })
}

export function listAreas(params) {
  return request({ url: '/area.listAreas', method: 'get', params })
}

export function saveCityArea(data) {
  return request({ url: '/area.saveCityArea', method: 'post', data })
}

export function updateCityArea(data) {
  return request({ url: '/area.updateCityArea', method: 'post', data })
}

export function deleteCityArea(data) {
  return request({ url: '/area.deleteCityArea', method: 'post', data })
}
