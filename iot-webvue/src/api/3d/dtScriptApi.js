import request from '@/utils/request'

export function listDtScripts(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.listDtScript',
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

export function saveDtScript(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.saveDtScript',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateDtScript(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.updateDtScript',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteDtScript(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.deleteDtScript',
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
