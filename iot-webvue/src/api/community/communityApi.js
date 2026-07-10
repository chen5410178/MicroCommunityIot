import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listMyEnteredCommunitys(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.listMyEnteredCommunitys',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询小区列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMyStore(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/store.listMyStore',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询商户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryStaffPrivileges(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/privilege.queryStaffPrivileges',
      method: 'get',
      params
    }).then(res => {
        resolve(res)
    }).catch(error => {
      reject(error)
    })
  })
}

export function listCommunitys(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.listCommunitys',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询小区失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.saveCommunity',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加小区失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.updateCommunity',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改小区失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.deleteCommunity',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除小区失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
