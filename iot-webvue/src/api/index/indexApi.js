import request from '@/utils/request'

export function queryPropertyAssetsIndex(params) {
  return request({
    url: '/propertyIndex.queryPropertyAssetsIndex',
    method: 'get',
    params
  })
}

export function listEvent(params) {
  return request({
    url: '/eventPool.listEventPool',
    method: 'get',
    params
  })
}

export function queryMachineIndex(params) {
  return request({
    url: '/propertyIndex.queryPropertyMachinesIndex',
    method: 'get',
    params
  })
}

export function listMonitorAlarm(params) {
  return request({
    url: '/monitorPool.listMonitorPool',
    method: 'get',
    params
  })
}
