import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listOwnerCars(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/car.queryCar',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询车辆失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
