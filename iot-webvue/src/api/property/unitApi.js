import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function queryUnits(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/unit.queryUnits',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0 || Array.isArray(res)) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询单元失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveUnit(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/unit.saveUnit',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加单元失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateUnit(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/unit.updateUnit',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改单元失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteUnitItem(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/unit.deleteUnit',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除单元失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
