import request from '@/utils/request'

export function listDtScripts(params) { return request({ url: '/dtScript.listDtScript', method: 'get', params }) }
export function saveDtScript(data) { return request({ url: '/dtScript.saveDtScript', method: 'post', data }) }
export function updateDtScript(data) { return request({ url: '/dtScript.updateDtScript', method: 'post', data }) }
export function deleteDtScript(data) { return request({ url: '/dtScript.deleteDtScript', method: 'post', data }) }
