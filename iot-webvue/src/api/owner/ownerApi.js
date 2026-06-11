import request from '@/utils/request'

export function queryCommunityAccount(params) {
  return request({ url: '/account.queryCommunityAccount', method: 'get', params })
}

export function queryOwnerAccountDetail(params) {
  return request({ url: '/account.queryOwnerAccountDetail', method: 'get', params })
}

export function ownerPrestoreAccount(data) {
  return request({ url: '/account.ownerPrestoreAccount', method: 'post', data })
}

export function deleteAccount(data) {
  return request({ url: '/account.deleteAccount', method: 'post', data })
}

// ===== 房屋认证管理 =====
export function listAppUsers(params) {
  return request({ url: '/appUser.listAppUser', method: 'get', params })
}

export function updateAppUser(data) {
  return request({ url: '/appUser.updateAppUser', method: 'post', data })
}

export function deleteAppUser(data) {
  return request({ url: '/appUser.deleteAppUser', method: 'post', data })
}
