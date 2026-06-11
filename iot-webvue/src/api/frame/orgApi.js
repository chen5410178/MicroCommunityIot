import request from '@/utils/request'

export function listStaffs(params) {
  return request({ url: '/query.staff.infos', method: 'get', params })
}

export function listStaffsNoInOrg(params) {
  return request({ url: '/user.listStaffsNoInOrg', method: 'get', params })
}

export function saveStaffOrgRel(data) {
  return request({ url: '/user.saveStaffOrgRel', method: 'post', data })
}

export function deleteOrgRelStaff(data) {
  return request({ url: '/org.deleteOrgRelStaff', method: 'post', data })
}

export function saveOrg(data) {
  return request({ url: '/org.saveOrg', method: 'post', data })
}

export function updateOrg(data) {
  return request({ url: '/org.updateOrg', method: 'post', data })
}

export function deleteOrg(data) {
  return request({ url: '/org.deleteOrg', method: 'post', data })
}

export function queryStaffPrivileges(params) {
  return request({ url: '/privilege.queryStaffPrivileges', method: 'get', params })
}

export function listStaffOrgs(params) {
  return request({ url: '/user.listStaffOrgs', method: 'get', params })
}

export function listStaffRoles(params) {
  return request({ url: '/user.listStaffRoles', method: 'get', params })
}
