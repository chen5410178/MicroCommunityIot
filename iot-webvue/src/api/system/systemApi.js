import request from '@/utils/request'

export function listDicts(params = {}) {
  return new Promise((resolve, reject) => {
    const finalParams = {
      page: 1,
      row: 100,
      ...params
    }
    request({
      url: '/dict.listDict',
      method: 'get',
      params: finalParams
    }).then(res => {
      if (res.code == 0) {
        resolve(res.data || [])
      } else {
        reject(new Error(res.msg || '查询字典失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveDict(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dict.saveDict',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '保存字典失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateDict(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dict.updateDict',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '更新字典失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteDict(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dict.deleteDict',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除字典失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
