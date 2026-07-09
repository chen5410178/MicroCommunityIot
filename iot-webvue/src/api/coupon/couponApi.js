import request from '@/utils/request'

// ===== 停车券 CRUD =====
export function listParkingCoupons(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listParkingCoupon',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveParkingCoupon(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.saveParkingCoupon',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateParkingCoupon(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.updateParkingCoupon',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteParkingCoupon(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.deleteParkingCoupon',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 车辆停车券 =====
export function listParkingCouponCars(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listParkingCouponCar',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 停车券订单 =====
export function listParkingCouponOrders(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listParkingCouponOrder',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 停车券核销 =====
export function listParkingCouponCarOrders(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCouponCar.listParkingCouponCarOrder',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 购买停车券 =====
export function buyParkingCoupon(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.buyParkingCoupon',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 商家停车券 =====
export function listShopParkingCoupons(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listShopParkingCoupon',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 充电桩停车券关联 =====
export function saveParkingCouponCharge(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.saveParkingCouponCharge',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteParkingCouponCharge(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.deleteParkingCouponCharge',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAdminParkingCouponCharge(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingCoupon.listAdminParkingCouponCharge',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 停车场 =====
export function listParkingAreas(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/parkingArea.listParkingArea',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// ===== 商家 =====
export function listCommunityShops(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/store.listCommunityStoreShop',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveCommunityShop(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/store.propertySaveStoreAndShop',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteCommunityShop(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/store.propertyDeleteStoreAndShop',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
