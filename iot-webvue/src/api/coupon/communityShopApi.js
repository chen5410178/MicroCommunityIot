import request from '@/utils/request'

export function listCommunityStoreShop(params) {
  return request({ url: '/store.listCommunityStoreShop', method: 'get', params })
}

export function saveStoreAndShop(data) {
  return request({ url: '/store.propertySaveStoreAndShop', method: 'post', data })
}

export function deleteStoreAndShop(data) {
  return request({ url: '/store.propertyDeleteStoreAndShop', method: 'post', data })
}

export function listShopParkingCoupon(params) {
  return request({ url: '/parkingCoupon.listShopParkingCoupon', method: 'get', params })
}

export function listParkingCoupon(params) {
  return request({ url: '/parkingCoupon.listParkingCoupon', method: 'get', params })
}

export function buyParkingCoupon(data) {
  return request({ url: '/parkingCoupon.buyParkingCoupon', method: 'post', data })
}
