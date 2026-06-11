import request from '@/utils/request'

export function listSystemInfo(params) {
  return request({
    url: '/system.listSystemInfo',
    method: 'get',
    params
  })
}

export function updateSystemInfo(data) {
  return request({
    url: '/system.updateSystemInfo',
    method: 'post',
    data
  })
}

export function listRegisterProtocol(params) {
  return request({
    url: '/system.listRegisterProtocol',
    method: 'get',
    params
  })
}

export function updateRegisterProtocol(data) {
  return request({
    url: '/system.updateRegisterProtocol',
    method: 'post',
    data
  })
}
