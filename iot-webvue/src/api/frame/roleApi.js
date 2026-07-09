import request from '@/utils/request'

export function listPrivilegeGroup(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/query.store.privilegeGroup',
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

export function savePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/privilegeGroup.savePrivilegeGroup',
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

export function updatePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/privilegeGroup.updatePrivilegeGroup',
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

export function deletePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/privilegeGroup.deletePrivilegeGroup',
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

export function listPrivilegeTree(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/query.privilegeGroup.noAddPrivilege',
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

export function addPrivilegeToGroup(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/add.privilege.PrivilegeGroup',
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

export function deletePrivilegeFromGroup(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/delete.privilege.PrivilegeGroup',
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

export function listRoleCommunity(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/roleCommunity.listRoleCommunity',
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

export function listOrgNoCommunitys(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/org.listOrgNoCommunitys',
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

export function saveRoleCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/roleCommunity.saveRoleCommunity',
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

export function deleteRoleCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/roleCommunity.deleteRoleCommunity',
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

export function listRoleStaff(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/role.listRoleStaff',
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

export function listStaffsNoRole(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/role.listStaffsNoRole',
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

export function saveRoleStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/role.saveRoleStaff',
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

export function deleteRoleStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/role.deleteRoleStaff',
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
