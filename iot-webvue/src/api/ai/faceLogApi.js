import request from '@/utils/request'

export function getFaceLog(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.getFaceLog',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryMonitorCamera(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.queryMonitorCamera',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
