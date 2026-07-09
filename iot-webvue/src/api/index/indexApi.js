import request from '@/utils/request'

export function queryPropertyAssetsIndex(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/propertyIndex.queryPropertyAssetsIndex', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEvent(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPool.listEventPool', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryMachineIndex(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/propertyIndex.queryPropertyMachinesIndex', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMonitorAlarm(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/monitorPool.listMonitorPool', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryAdminCount(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/admin.queryAdminCount', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryCommunityEvent(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/admin.queryCommunityEvent', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryCommunityMonitorPool(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/admin.queryCommunityMonitorPool', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryDevCount(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/admin.queryDevCount', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryAppServiceData(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/admin.queryAppServiceData', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}
