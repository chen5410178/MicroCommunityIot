import request from '@/utils/request'

export function listPaymentPool(params) {
  return request({ url: '/payment.listPaymentPool', method: 'get', params })
}

export function savePaymentPool(data) {
  return request({ url: '/payment.savePaymentPool', method: 'post', data })
}

export function updatePaymentPool(data) {
  return request({ url: '/payment.updatePaymentPool', method: 'post', data })
}

export function deletePaymentPool(data) {
  return request({ url: '/payment.deletePaymentPool', method: 'post', data })
}

export function listPaymentAdapt(params) {
  return request({ url: '/payment.listPaymentAdapt', method: 'get', params })
}

export function listPaymentKey(params) {
  return request({ url: '/payment.listPaymentKey', method: 'get', params })
}

export function listFeeConfig(params) {
  return request({ url: '/payment.listFeeConfig', method: 'get', params })
}
