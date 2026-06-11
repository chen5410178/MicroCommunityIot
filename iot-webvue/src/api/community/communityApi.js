import request from '@/utils/request'

export function listMyEnteredCommunitys(params) {
  return request({
    url: '/community.listMyEnteredCommunitys',
    method: 'get',
    params
  })
}

export function listMyStore(params) {
  return request({
    url: '/store.listMyStore',
    method: 'get',
    params
  })
}

export function queryStaffPrivileges(params) {
  return request({
    url: '/privilege.queryStaffPrivileges',
    method: 'get',
    params
  })
}

export function listCommunitys(params) {
  return request({ url: '/community.listCommunitys', method: 'get', params })
}
export function saveCommunity(data) {
  return request({ url: '/community.saveCommunity', method: 'post', data })
}
export function updateCommunity(data) {
  return request({ url: '/community.updateCommunity', method: 'post', data })
}
export function deleteCommunity(data) {
  return request({ url: '/community.deleteCommunity', method: 'post', data })
}
