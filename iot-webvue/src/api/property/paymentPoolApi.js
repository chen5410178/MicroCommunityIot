import request from '@/utils/request'

export function listPaymentPool(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.listPaymentPool',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询支付池失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function savePaymentPool(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.savePaymentPool',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加支付池失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updatePaymentPool(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.updatePaymentPool',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改支付池失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deletePaymentPool(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.deletePaymentPool',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除支付池失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listPaymentAdapt(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.listPaymentAdapt',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询支付适配失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listPaymentKey(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.listPaymentKey',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询支付密钥失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listFeeConfig(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/payment.listFeeConfig',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询费用配置失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
