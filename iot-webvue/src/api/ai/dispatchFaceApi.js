import request from '@/utils/request'

export function listDispatchFaces(params) { return request({ url: '/ai.getPersonFace', method: 'get', params }) }
export function saveDispatchFace(data) { return request({ url: '/ai.saveDispatchFace', method: 'post', data }) }
export function deleteDispatchFace(data) { return request({ url: '/ai.deleteDispatchFace', method: 'post', data }) }
