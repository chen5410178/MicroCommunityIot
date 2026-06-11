import request from '@/utils/request'

export function listDtModals(params) { return request({ url: '/dtModal.listDtModal', method: 'get', params }) }
export function saveDtModal(data) { return request({ url: '/dtModal.saveDtModal', method: 'post', data }) }
export function updateDtModal(data) { return request({ url: '/dtModal.updateDtModal', method: 'post', data }) }
export function deleteDtModal(data) { return request({ url: '/dtModal.deleteDtModal', method: 'post', data }) }
