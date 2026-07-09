import request from '@/utils/request'

export function listPersonFaces(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.listPersonFace',
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

export function authPersonFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.authPersonFace',
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

export function deletePersonFace(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/personFace.deletePersonFace',
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
