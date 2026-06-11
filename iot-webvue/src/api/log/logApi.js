import request from '@/utils/request'

export function listMqttLogs(params) {
  return request({ url: '/log.listMqttLog', method: 'get', params })
}

export function listAssetImportLogs(params) {
  return request({ url: '/log.queryAssetImportLog', method: 'get', params })
}

export function listAssetImportLogDetails(params) {
  return request({ url: '/log.queryAssetImportLogDetail', method: 'get', params })
}

export function listAssetImportLogTypes(params) {
  return request({ url: '/log.queryAssetImportLogType', method: 'get', params })
}
