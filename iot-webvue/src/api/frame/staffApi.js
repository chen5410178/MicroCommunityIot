import request from '@/utils/request'

export function listStaffs(params) {
  return request({ url: '/query.staff.infos', method: 'get', params })
}

export function addStaff(data) {
  return request({ url: '/user.staff.add', method: 'post', data })
}

export function updateStaff(data) {
  return request({ url: '/user.staff.modify', method: 'post', data })
}

export function deleteStaff(data) {
  return request({ url: '/user.staff.delete', method: 'post', data })
}

export function listStaffOrgs(params) {
  return request({ url: '/user.listStaffOrgs', method: 'get', params })
}

export function listStaffRoles(params) {
  return request({ url: '/user.listStaffRoles', method: 'get', params })
}

export function listStaffPrivileges(params) {
  return request({ url: '/privilege.queryStaffPrivileges', method: 'get', params })
}
