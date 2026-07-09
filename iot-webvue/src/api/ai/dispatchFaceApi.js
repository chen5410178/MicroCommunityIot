import request from '@/utils/request'

export function listDispatchFaces(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.getPersonFace',
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

export function saveDispatchFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.saveDispatchFace',
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

export function deleteDispatchFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.deleteDispatchFace',
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
