import request from '@/utils/request'

// ===== 字典/属性规格 =====
export function listDicts(params) { return request({ url: '/dict.listDict', method: 'get', params }) }
export function listAttrSpecs(params) { return request({ url: '/attrSpec.listAttrSpec', method: 'get', params }) }
export function listAttrValues(params) { return request({ url: '/attrValue.listAttrValue', method: 'get', params }) }
