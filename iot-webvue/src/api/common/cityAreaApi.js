import request from '@/utils/request'

export function queryAreaTree(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/area.queryAreaTree',
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

export function listAreas(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/area.listAreas',
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

export function saveCityArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/area.saveCityArea',
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

export function updateCityArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/area.updateCityArea',
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

export function deleteCityArea(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/area.deleteCityArea',
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
