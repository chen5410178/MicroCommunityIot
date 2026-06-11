import request from '@/utils/request'

export function listSmallWeChats(params) {
  return request({ url: '/smallWeChat.listSmallWeChats', method: 'get', params })
}

export function saveSmallWeChat(data) {
  return request({ url: '/smallWeChat.saveSmallWeChat', method: 'post', data })
}

export function updateSmallWeChat(data) {
  return request({ url: '/smallWeChat.updateSmallWeChat', method: 'post', data })
}

export function deleteSmallWeChat(data) {
  return request({ url: '/smallWeChat.deleteSmallWeChat', method: 'post', data })
}
