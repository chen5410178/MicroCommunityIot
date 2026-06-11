import request from '@/utils/request'

export function listPublicWeChats(params) {
  return request({ url: '/smallWeChat.listSmallWeChats', method: 'get', params })
}

export function savePublicWeChat(data) {
  return request({ url: '/smallWeChat.saveSmallWeChat', method: 'post', data })
}

export function updatePublicWeChat(data) {
  return request({ url: '/smallWeChat.updateSmallWeChat', method: 'post', data })
}

export function deletePublicWeChat(data) {
  return request({ url: '/smallWeChat.deleteSmallWeChat', method: 'post', data })
}
