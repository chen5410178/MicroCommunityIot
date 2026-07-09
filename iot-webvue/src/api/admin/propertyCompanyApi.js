import request from '@/utils/request'

export function listProperty(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/property.listProperty',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询物业列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveProperty(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/property.saveProperty',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加物业失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateProperty(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/property.updateProperty',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改物业失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteProperty(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/property.deleteProperty',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除物业失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updatePropertyState(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/property.updatePropertyState',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '更新物业状态失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function adminLoginProperty(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.adminLoginProperty',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '登录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listStoreStaff(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/storeStaff.listStoreStaff',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询员工列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function resetStaffPwd(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.resetStaffPwd',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '重置密码失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMenuGroup(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/menuGroup.listMenuGroup',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询菜单组失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listFreeCommunity(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.listFreeCommunity',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询空闲小区失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
