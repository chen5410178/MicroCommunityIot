import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listMeterMachine(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/meterMachine.listMeterMachine',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询水电表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMeterMachineReadOrRechargeDetail(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/meterMachine.listMeterMachineReadOrRechargeDetail',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询充值记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
