import request from '@/utils/request'

export function listChargeMachineFactory(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listChargeMachineFactory', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.saveChargeMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.updateChargeMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.deleteChargeMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMachineFactorySpec(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachineFactorySpec.listChargeMachineFactorySpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachineFactorySpec.saveChargeMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachineFactorySpec.deleteChargeMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWorkLicenseFactorys(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicenseFactory.listWorkLicenseFactory', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveWorkLicenseFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicenseFactory.saveWorkLicenseFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateWorkLicenseFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicenseFactory.updateWorkLicenseFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteWorkLicenseFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicenseFactory.deleteWorkLicenseFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeMachine(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listAdminChargeMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeMachinePort(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listAdminChargeMachinePort', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeMachineOrder(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listAdminChargeMachineOrder', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeRuleFee(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.listAdminChargeRuleFee', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeRulePrice(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.listAdminChargeRulePrice', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminParkingCouponCharge(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/parkingCoupon.listAdminParkingCouponCharge', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryAdminOwnerAccount(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/account.queryAdminOwnerAccount', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAdminChargeMachineOrderAcct(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listAdminChargeMachineOrderAcct', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function stopCharge(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.stopCharge', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function throwStopCharge(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.throwStopCharge', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function orderRefundMoney(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.orderRefundMoney', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMachineOrder(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listChargeMachineOrder', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMachineOrderAcct(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listChargeMachineOrderAcct', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMachine(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listChargeMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryOwnerAccount(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/account.queryOwnerAccount', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeRule(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.listChargeRule', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.saveChargeRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.updateChargeRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.deleteChargeRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeRuleFee(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.listChargeRuleFee', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeRuleFee(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.saveChargeRuleFee', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeRuleFee(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.updateChargeRuleFee', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeRuleFee(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.deleteChargeRuleFee', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeRulePrice(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.listChargeRulePrice', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeRulePrice(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.saveChargeRulePrice', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeRulePrice(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.updateChargeRulePrice', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeRulePrice(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeRule.deleteChargeRulePrice', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargePeakAlleyPrice(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargePeakAlleyPrice.listChargePeakAlleyPrice', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargePeakAlleyPrice(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargePeakAlleyPrice.saveChargePeakAlleyPrice', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMonthOrders(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.listChargeMonthOrder', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMonthOrder(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.saveChargeMonthOrder', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMonthCards(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.listChargeMonthCard', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.saveChargeMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.updateChargeMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.deleteChargeMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function restartChargeMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.restartChargeMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMachinePort(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.listChargeMachinePort', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMachinePort(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.saveChargeMachinePort', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeMachinePort(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeMachine.deleteChargeMachinePort', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listChargeMonthCard(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.listChargeMonthCard', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveChargeMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.saveChargeMonthCard', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateChargeMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.updateChargeMonthCard', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteChargeMonthCard(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/chargeCard.deleteChargeMonthCard', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryOrderCountStatistics(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/charge.queryOrderCountStatistics', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryOrderMoneyStatistics(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/charge.queryOrderMoneyStatistics', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryMonthCountStatistics(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/charge.queryMonthCountStatistics', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryMonthMoneyStatistics(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/charge.queryMonthMoneyStatistics', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listCouponPropertyUser(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/coupon.listCouponPropertyUser', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWorkLicenseMachine(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.listWorkLicenseMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWorkLicensePos(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.listWorkLicensePos', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWorkLicenseMachines(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.listWorkLicenseMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveWorkLicenseMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.saveWorkLicenseMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateWorkLicenseMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.updateWorkLicenseMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteWorkLicenseMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.deleteWorkLicenseMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function playPps(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.playPps', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveWhiteList(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.saveWhiteList', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWhiteList(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/whiteList.listWhiteList', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listWorkLicenseLogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/workLicense.listWorkLicenseLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}
