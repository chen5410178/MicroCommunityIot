import request from '@/utils/request'

export function listStaffs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/query.staff.infos',
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

export function addStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.staff.add',
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

export function updateStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.staff.modify',
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

export function deleteStaff(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.staff.delete',
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

export function listStaffOrgs(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.listStaffOrgs',
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

export function listStaffRoles(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/user.listStaffRoles',
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

export function listStaffPrivileges(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/privilege.queryStaffPrivileges',
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
