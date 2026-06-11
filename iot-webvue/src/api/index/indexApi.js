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

export function queryAdminCount(params) {
  return request({
    url: '/admin.queryAdminCount',
    method: 'get',
    params
  })
}

export function queryCommunityEvent(params) {
  return request({
    url: '/admin.queryCommunityEvent',
    method: 'get',
    params
  })
}

export function queryCommunityMonitorPool(params) {
  return request({
    url: '/admin.queryCommunityMonitorPool',
    method: 'get',
    params
  })
}

export function queryDevCount(params) {
  return request({
    url: '/admin.queryDevCount',
    method: 'get',
    params
  })
}

export function queryAppServiceData(params) {
  return request({
    url: '/admin.queryAppServiceData',
    method: 'get',
    params
  })
}
