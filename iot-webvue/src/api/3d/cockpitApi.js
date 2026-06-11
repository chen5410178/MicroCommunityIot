import request from '@/utils/request'

export function listDtScenes(params) { return request({ url: '/dtScene.listDtScene', method: 'get', params }) }
export function listDtSceneObjScripts(params) { return request({ url: '/dtScript.listDtSceneObjScript', method: 'get', params }) }
export function listDtModals(params) { return request({ url: '/dtModal.listDtModal', method: 'get', params }) }
export function queryFloors(params) { return request({ url: '/floor.queryFloors', method: 'get', params }) }
export function queryRooms(params) { return request({ url: '/room.queryRooms', method: 'get', params }) }
export function queryOwners(params) { return request({ url: '/owner.queryOwners', method: 'get', params }) }
export function queryOwnerCars(params) { return request({ url: '/ownerCar.queryOwnerCars', method: 'get', params }) }
