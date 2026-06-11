import request from '@/utils/request'

export function listPersonFaces(params) {
  return request({ url: '/personFace.listPersonFace', method: 'get', params })
}

export function authPersonFace(data) {
  return request({ url: '/personFace.authPersonFace', method: 'post', data })
}

export function deletePersonFace(data) {
  return request({ url: '/personFace.deletePersonFace', method: 'post', data })
}
