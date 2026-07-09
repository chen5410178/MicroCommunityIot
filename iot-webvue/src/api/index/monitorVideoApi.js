import request from '@/utils/request'

export function getAiAlter(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/ai.getAiAlter',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
