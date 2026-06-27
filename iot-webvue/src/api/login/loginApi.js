import request from '@/utils/request'

export function getSysInfo(params) {
  return request({
    url: '/login.getSysInfo',
    method: 'get',
    params
  })
}

export function generatorValidateCode(params) {
  return request({
    url: '/login.generatorValidateCode',
    method: 'get',
    params
  })
}

export function generatorSliderCode(params) {
  return request({
    url: '/login.generatorSliderCode',
    method: 'get',
    params
  })
}

export function pcUserLogin(data) {
  return request({
    url: '/login.pcUserLogin',
    method: 'post',
    data
  })
}

export function userLogout(data) {
  return request({
    url: '/login.userLogout',
    method: 'post',
    data
  })
}
