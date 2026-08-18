import request from '@/utils/request'

// ===== 商铺位置 =====
export function listShopLocations(params) {
  return request({ url: '/shop.listShopLocations', method: 'get', params })
}
export function saveShopLocation(data) {
  return request({ url: '/shop.saveShopLocation', method: 'post', data })
}
export function updateShopLocation(data) {
  return request({ url: '/shop.updateShopLocation', method: 'post', data })
}
export function deleteShopLocation(data) {
  return request({ url: '/shop.deleteShopLocation', method: 'post', data })
}

// ===== 商铺 =====
export function listBuildingShops(params) {
  return request({ url: '/shop.listBuildingShops', method: 'get', params })
}
export function saveBuildingShop(data) {
  return request({ url: '/shop.saveBuildingShop', method: 'post', data })
}
export function updateBuildingShop(data) {
  return request({ url: '/shop.updateBuildingShop', method: 'post', data })
}
export function deleteBuildingShop(data) {
  return request({ url: '/shop.deleteBuildingShop', method: 'post', data })
}

// ===== 商铺业主关联（移交/退租） =====
export function listBuildingShopOwners(params) {
  return request({ url: '/shop.listBuildingShopOwners', method: 'get', params })
}
export function saveBuildingShopOwner(data) {
  return request({ url: '/shop.saveBuildingShopOwner', method: 'post', data })
}
export function updateBuildingShopOwner(data) {
  return request({ url: '/shop.updateBuildingShopOwner', method: 'post', data })
}
export function deleteBuildingShopOwner(data) {
  return request({ url: '/shop.deleteBuildingShopOwner', method: 'post', data })
}

// ===== Excel 导入 =====
export function assetImport(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/assetImport',
      method: 'post',
      data,
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '导入失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
