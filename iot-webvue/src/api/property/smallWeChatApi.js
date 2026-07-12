import request from '@/utils/request'

export function listSmallWeChats(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.listSmallWeChats',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询小程序列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveSmallWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.saveSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加小程序失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateSmallWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.updateSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改小程序失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteSmallWeChat(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/smallWeChat.deleteSmallWeChat',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除小程序失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
