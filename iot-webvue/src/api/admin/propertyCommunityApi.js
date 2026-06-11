import request from '@/utils/request'

export function listCommunitys(params) { return request({ url: '/community.listCommunitys', method: 'get', params }) }
export function joinCommunity(data) { return request({ url: '/member.join.community', method: 'post', data }) }
export function quitCommunity(data) { return request({ url: '/member.quit.community', method: 'post', data }) }
export function updateMenuGroupCommunity(data) { return request({ url: '/menuGroupCommunity.updateMenuGroupCommunity', method: 'post', data }) }
export function listMenuGroup(params) { return request({ url: '/menuGroup.listMenuGroup', method: 'get', params }) }
export function listFreeCommunity(params) { return request({ url: '/community.listFreeCommunity', method: 'get', params }) }
export function listMenuGroupCommunity(params) { return request({ url: '/menuGroupCommunity.listMenuGroupCommunity', method: 'get', params }) }
