import request from '@/utils/request'

export function listMqttLogs(params) {
  return request({ url: '/log.listMqttLog', method: 'get', params })
}
