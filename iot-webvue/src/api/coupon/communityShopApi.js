import request from '@/utils/request'

export function listCommunityStoreShop(params) {
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

export function saveStoreAndShop(data) {
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

export function deleteStoreAndShop(data) {
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

export function listShopParkingCoupon(params) {
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

export function listParkingCoupon(params) {
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
