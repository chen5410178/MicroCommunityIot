import request from '@/utils/request'

export function listCallingDevice(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.listCallingDevice',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.saveCallingDevice',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.updateCallingDevice',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteCallingDevice(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingDevice.deleteCallingDevice',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listCallingPlatform(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/callingPlatform.listCallingPlatform',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
