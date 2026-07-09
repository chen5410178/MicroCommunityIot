import request from '@/utils/request'

export function ssoTokenLogin(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/login.ssoTokenLogin',
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
