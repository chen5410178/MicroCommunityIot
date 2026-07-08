import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function queryFloors(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/floor.queryFloors',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询楼栋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryFloorAndUnits(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/floor.queryFloorAndUnits',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询楼栋单元失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveFloor(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/floor.saveFloor',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加楼栋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function editFloor(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/floor.editFloor',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改楼栋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteFloorItem(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/floor.deleteFloor',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除楼栋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
