import request from '@/utils/request'

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

export function listCallingPlatform(params) {
  return request({ url: '/callingPlatform.listCallingPlatform', method: 'get', params })
}
