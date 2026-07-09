import request from '@/utils/request'

// ===== 门禁厂家 =====
export function listHardwareManufacturers(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.listHardwareManufacturer',
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

export function saveHardwareManufacturer(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.saveHardwareManufacturer',
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

export function updateHardwareManufacturer(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.updateHardwareManufacturer',
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

export function deleteHardwareManufacturer(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.deleteHardwareManufacturer',
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

// ===== 门禁厂家规格 =====
export function listHardwareManufacturerAttrs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.listHardwareManufacturerAttr',
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

export function saveHardwareManufacturerAttr(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.saveHardwareManufacturerAttr',
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

export function deleteHardwareManufacturerAttr(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/hm.deleteHardwareManufacturerAttr',
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
