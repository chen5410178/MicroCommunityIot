import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function queryRooms(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/room.queryRooms',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveRoom(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/room.saveRoom',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateRoom(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/room.updateRoom',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteRoom(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/room.deleteRoom',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryRoomsTree(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/room.queryRoomsTree',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0 || Array.isArray(res)) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询房屋树失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function sellRoom(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/room.sellRoom',
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

export function exitRoom(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/room.exitRoom',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '解绑失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function importOwnerRoom(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/assetImport',
      method: 'post',
      data,
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '导入失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
