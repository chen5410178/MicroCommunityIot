import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

// 开票记录分页（状态以商城实时状态回填）
export function listInvoiceOrders(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/invoice.listInvoiceOrders',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询开票记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 按手机号查询未开票充电订单
export function listUnInvoicedOrders(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/invoice.listUnInvoicedOrders',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询未开票订单失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 按手机号查询商城发票抬头
export function queryInvoiceHeadings(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/invoice.queryInvoiceHeadings',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询发票抬头失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 提交开票（先调商城开票，商城建单才在物联网侧落库）
export function saveInvoiceOrder(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/invoice.saveInvoiceOrder',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '提交开票失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 开票失败单重新提交
export function reSubmitInvoice(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/invoice.reSubmitInvoice',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '重新提交失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 删除未成功开票记录（软删除）
export function deleteInvoiceOrder(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/invoice.deleteInvoiceOrder',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除开票记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 红冲列表（直接读取商城数据）
export function listInvoiceReds(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/invoice.listInvoiceReds',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询红冲记录失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

// 发起红冲（商城执行）
export function saveInvoiceRed(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/invoice.saveInvoiceRed',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '发起红冲失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
