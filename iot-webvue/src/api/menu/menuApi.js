import request from '@/utils/request'

export function listCatalog(params) {
  return request({
    url: '/menu.listCatalog',
    method: 'get',
    params
  })
}

export function listCatalogMenus(params) {
  return request({
    url: '/menu.listCatalogMenus',
    method: 'get',
    params
  })
}
