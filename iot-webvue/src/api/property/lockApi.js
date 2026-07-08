import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listLockMachine(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/lockMachine.listLockMachine',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询门锁失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listLockPerson(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/lockPerson.listLockPerson',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询锁用户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listLockLog(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/lockLog.listLockLog',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询锁日志失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listLockInout(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/lockInout.listLockInout',
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
