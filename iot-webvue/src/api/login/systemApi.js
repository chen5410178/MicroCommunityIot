import request from '@/utils/request'

export function listSystemInfo(params) {
  return request({
    url: '/system.listSystemInfo',
    method: 'get',
    params
  })
}
