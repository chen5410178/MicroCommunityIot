import request from '@/utils/request'

export function listProperty(params) { return request({ url: '/property.listProperty', method: 'get', params }) }
export function saveProperty(data) { return request({ url: '/property.saveProperty', method: 'post', data }) }
export function updateProperty(data) { return request({ url: '/property.updateProperty', method: 'post', data }) }
export function deleteProperty(data) { return request({ url: '/property.deleteProperty', method: 'post', data }) }
export function updatePropertyState(data) { return request({ url: '/property.updatePropertyState', method: 'post', data }) }
export function adminLoginProperty(data) { return request({ url: '/login.adminLoginProperty', method: 'post', data }) }
export function listStoreStaff(params) { return request({ url: '/storeStaff.listStoreStaff', method: 'get', params }) }
export function resetStaffPwd(data) { return request({ url: '/user.resetStaffPwd', method: 'post', data }) }
export function listMenuGroup(params) { return request({ url: '/menuGroup.listMenuGroup', method: 'get', params }) }
export function listFreeCommunity(params) { return request({ url: '/community.listFreeCommunity', method: 'get', params }) }
