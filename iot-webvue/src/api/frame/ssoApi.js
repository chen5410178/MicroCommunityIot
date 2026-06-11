import request from '@/utils/request'

export function ssoTokenLogin(data) {
  return request({
    url: '/login.ssoTokenLogin',
    method: 'post',
    data
  })
}
