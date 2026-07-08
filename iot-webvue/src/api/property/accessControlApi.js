import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listAccessControlFloor(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/accessControlFloor.listAccessControlFloor',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询门禁失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAccessControlLog(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/accessControlLog.listAccessControlLog',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询门禁日志失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAccessControlInout(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/accessControlInout.listAccessControlInout',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询出入记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
