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
