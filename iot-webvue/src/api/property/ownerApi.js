import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function queryOwners(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/owner.queryOwners',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询业主失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveOwnerRoomRel(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.saveOwnerRoomRel',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '绑定房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteOwnerRoomRel(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.deleteOwnerRoomRel',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '解除绑定失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listOwnerMembers(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/owner.listOwnerMember',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询家庭成员失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
