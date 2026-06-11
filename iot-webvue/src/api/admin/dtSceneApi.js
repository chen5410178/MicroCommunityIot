import request from '@/utils/request'

export function listDtScenes(params) { return request({ url: '/dtScene.listDtScene', method: 'get', params }) }
export function saveDtScene(data) { return request({ url: '/dtScene.saveDtScene', method: 'post', data }) }
export function updateDtScene(data) { return request({ url: '/dtScene.updateDtScene', method: 'post', data }) }
export function deleteDtScene(data) { return request({ url: '/dtScene.deleteDtScene', method: 'post', data }) }
export function listDtSceneObjScripts(params) { return request({ url: '/dtScript.listDtSceneObjScript', method: 'get', params }) }
export function deleteDtSceneObjScript(data) { return request({ url: '/dtScript.deleteDtSceneObjScript', method: 'post', data }) }
export function listCommunitys(params) { return request({ url: '/community.listCommunitys', method: 'get', params }) }
