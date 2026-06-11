import request from '@/utils/request'

export function listPrivilegeGroup(params) {
  return request({ url: '/query.store.privilegeGroup', method: 'get', params })
}

export function savePrivilegeGroup(data) {
  return request({ url: '/privilegeGroup.savePrivilegeGroup', method: 'post', data })
}

export function updatePrivilegeGroup(data) {
  return request({ url: '/privilegeGroup.updatePrivilegeGroup', method: 'post', data })
}

export function deletePrivilegeGroup(data) {
  return request({ url: '/privilegeGroup.deletePrivilegeGroup', method: 'post', data })
}

export function listPrivilegeTree(params) {
  return request({ url: '/query.privilegeGroup.noAddPrivilege', method: 'get', params })
}

export function addPrivilegeToGroup(data) {
  return request({ url: '/add.privilege.PrivilegeGroup', method: 'post', data })
}

export function deletePrivilegeFromGroup(data) {
  return request({ url: '/delete.privilege.PrivilegeGroup', method: 'post', data })
}

export function listRoleCommunity(params) {
  return request({ url: '/roleCommunity.listRoleCommunity', method: 'get', params })
}

export function listOrgNoCommunitys(params) {
  return request({ url: '/org.listOrgNoCommunitys', method: 'get', params })
}

export function saveRoleCommunity(data) {
  return request({ url: '/roleCommunity.saveRoleCommunity', method: 'post', data })
}

export function deleteRoleCommunity(data) {
  return request({ url: '/roleCommunity.deleteRoleCommunity', method: 'post', data })
}

export function listRoleStaff(params) {
  return request({ url: '/role.listRoleStaff', method: 'get', params })
}

export function listStaffsNoRole(params) {
  return request({ url: '/role.listStaffsNoRole', method: 'get', params })
}

export function saveRoleStaff(data) {
  return request({ url: '/role.saveRoleStaff', method: 'post', data })
}

export function deleteRoleStaff(data) {
  return request({ url: '/role.deleteRoleStaff', method: 'post', data })
}
