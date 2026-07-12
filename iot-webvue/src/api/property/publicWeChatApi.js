import request from '@/utils/request'

export function listPublicWeChats(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.listSmallWeChats',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询公众号列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function savePublicWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.saveSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加公众号失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updatePublicWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.updateSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改公众号失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deletePublicWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.deleteSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除公众号失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
