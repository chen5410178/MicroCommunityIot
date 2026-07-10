import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function listUserDownloadFile(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/userDownloadFile.listUserDownloadFile',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询下载记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteUserDownloadFile(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/userDownloadFile.deleteUserDownloadFile',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除下载记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAttrSpecs(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/attrSpec.listAttrSpec',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询属性规格失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAttrValues(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/attrValue.listAttrValue',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询属性值失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
