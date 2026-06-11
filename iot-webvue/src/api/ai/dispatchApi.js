import request from '@/utils/request'

export function getDispatchLog(params) { return request({ url: '/ai.getDispatchLog', method: 'get', params }) }
