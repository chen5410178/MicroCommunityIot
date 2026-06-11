import request from '@/utils/request'

export function getAiAlter(params) {
  return request({ url: '/ai.getAiAlter', method: 'get', params })
}
