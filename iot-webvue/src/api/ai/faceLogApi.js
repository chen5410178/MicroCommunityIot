import request from '@/utils/request'

export function getFaceLog(params) { return request({ url: '/ai.getFaceLog', method: 'get', params }) }
export function queryMonitorCamera(params) { return request({ url: '/ai.queryMonitorCamera', method: 'get', params }) }
