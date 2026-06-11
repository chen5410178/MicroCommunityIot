import request from '@/utils/request'

// ===== 停车券 CRUD =====
export function listParkingCoupons(params) { return request({ url: '/parkingCoupon.listParkingCoupon', method: 'get', params }) }
export function saveParkingCoupon(data) { return request({ url: '/parkingCoupon.saveParkingCoupon', method: 'post', data }) }
export function updateParkingCoupon(data) { return request({ url: '/parkingCoupon.updateParkingCoupon', method: 'post', data }) }
export function deleteParkingCoupon(data) { return request({ url: '/parkingCoupon.deleteParkingCoupon', method: 'post', data }) }

// ===== 车辆停车券 =====
export function listParkingCouponCars(params) { return request({ url: '/parkingCoupon.listParkingCouponCar', method: 'get', params }) }

// ===== 停车券订单 =====
export function listParkingCouponOrders(params) { return request({ url: '/parkingCoupon.listParkingCouponOrder', method: 'get', params }) }

// ===== 停车券核销 =====
export function listParkingCouponCarOrders(params) { return request({ url: '/parkingCouponCar.listParkingCouponCarOrder', method: 'get', params }) }

// ===== 购买停车券 =====
export function buyParkingCoupon(data) { return request({ url: '/parkingCoupon.buyParkingCoupon', method: 'post', data }) }

// ===== 商家停车券 =====
export function listShopParkingCoupons(params) { return request({ url: '/parkingCoupon.listShopParkingCoupon', method: 'get', params }) }

// ===== 充电桩停车券关联 =====
export function saveParkingCouponCharge(data) { return request({ url: '/parkingCoupon.saveParkingCouponCharge', method: 'post', data }) }
export function deleteParkingCouponCharge(data) { return request({ url: '/parkingCoupon.deleteParkingCouponCharge', method: 'post', data }) }
export function listAdminParkingCouponCharge(params) { return request({ url: '/parkingCoupon.listAdminParkingCouponCharge', method: 'get', params }) }

// ===== 停车场 =====
export function listParkingAreas(params) { return request({ url: '/parkingArea.listParkingArea', method: 'get', params }) }

// ===== 商家 =====
export function listCommunityShops(params) { return request({ url: '/store.listCommunityStoreShop', method: 'get', params }) }
export function saveCommunityShop(data) { return request({ url: '/store.propertySaveStoreAndShop', method: 'post', data }) }
export function deleteCommunityShop(data) { return request({ url: '/store.propertyDeleteStoreAndShop', method: 'post', data }) }
