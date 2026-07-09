import request from '@/utils/request'

export function getSysInfo(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.getSysInfo',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '获取系统信息失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function generatorValidateCode(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.generatorValidateCode',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '获取验证码失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function generatorSliderCode(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.generatorSliderCode',
      method: 'get',
      params
    }).then(res => {
      resolve(res)
    }).catch(error => {
      reject(error)
    })
  })
}

export function pcUserLogin(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.pcUserLogin',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '登录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function userLogout(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.userLogout',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '登出失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
