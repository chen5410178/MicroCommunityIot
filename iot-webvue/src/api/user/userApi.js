import request from '@/utils/request'

export function getUserInfo(params) {
  return request({
    url: '/user.getUserInfo',
    method: 'get',
    params
  })
}

export function changeStaffPwd(data) {
  return request({
    url: '/user.changeStaffPwd',
    method: 'post',
    data
  })
}
