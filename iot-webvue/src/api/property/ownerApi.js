import request from '@/utils/request'

export function queryOwners(params) {
  return request({ url: '/owner.queryOwners', method: 'get', params })
}

export function saveOwner(data) {
  return request({ url: '/owner.saveOwner', method: 'post', data })
}

export function updateOwner(data) {
  return request({ url: '/owner.editOwner', method: 'post', data })
}

export function deleteOwner(data) {
  return request({ url: '/owner.deleteOwner', method: 'post', data })
}

export function queryRoomsByOwner(params) {
  return request({ url: '/room.queryRoomsByOwner', method: 'get', params })
}
