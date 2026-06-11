import request from '@/utils/request'

export function listChargeMachineFactory(params) { return request({ url: '/chargeMachine.listChargeMachineFactory', method: 'get', params }) }
export function saveChargeMachineFactory(data) { return request({ url: '/chargeMachine.saveChargeMachineFactory', method: 'post', data }) }
export function updateChargeMachineFactory(data) { return request({ url: '/chargeMachine.updateChargeMachineFactory', method: 'post', data }) }
export function deleteChargeMachineFactory(data) { return request({ url: '/chargeMachine.deleteChargeMachineFactory', method: 'post', data }) }

export function listChargeMachineFactorySpec(params) { return request({ url: '/chargeMachineFactorySpec.listChargeMachineFactorySpec', method: 'get', params }) }
export function saveChargeMachineFactorySpec(data) { return request({ url: '/chargeMachineFactorySpec.saveChargeMachineFactorySpec', method: 'post', data }) }
export function deleteChargeMachineFactorySpec(data) { return request({ url: '/chargeMachineFactorySpec.deleteChargeMachineFactorySpec', method: 'post', data }) }

export function listWorkLicenseFactorys(params) { return request({ url: '/workLicenseFactory.listWorkLicenseFactory', method: 'get', params }) }
export function saveWorkLicenseFactory(data) { return request({ url: '/workLicenseFactory.saveWorkLicenseFactory', method: 'post', data }) }
export function updateWorkLicenseFactory(data) { return request({ url: '/workLicenseFactory.updateWorkLicenseFactory', method: 'post', data }) }
export function deleteWorkLicenseFactory(data) { return request({ url: '/workLicenseFactory.deleteWorkLicenseFactory', method: 'post', data }) }

// ===== Admin 充电桩管理 =====
export function listAdminChargeMachine(params) { return request({ url: '/chargeMachine.listAdminChargeMachine', method: 'get', params }) }
export function listAdminChargeMachinePort(params) { return request({ url: '/chargeMachine.listAdminChargeMachinePort', method: 'get', params }) }
export function listAdminChargeMachineOrder(params) { return request({ url: '/chargeMachine.listAdminChargeMachineOrder', method: 'get', params }) }
export function listAdminChargeRuleFee(params) { return request({ url: '/chargeRule.listAdminChargeRuleFee', method: 'get', params }) }
export function listAdminChargeRulePrice(params) { return request({ url: '/chargeRule.listAdminChargeRulePrice', method: 'get', params }) }
export function listAdminParkingCouponCharge(params) { return request({ url: '/parkingCoupon.listAdminParkingCouponCharge', method: 'get', params }) }
export function queryAdminOwnerAccount(params) { return request({ url: '/account.queryAdminOwnerAccount', method: 'get', params }) }
export function listAdminChargeMachineOrderAcct(params) { return request({ url: '/chargeMachine.listAdminChargeMachineOrderAcct', method: 'get', params }) }
export function stopCharge(data) { return request({ url: '/chargeMachine.stopCharge', method: 'post', data }) }
export function throwStopCharge(data) { return request({ url: '/chargeMachine.throwStopCharge', method: 'post', data }) }
export function orderRefundMoney(data) { return request({ url: '/chargeMachine.orderRefundMoney', method: 'post', data }) }

// ===== 物业端充电 =====
export function listChargeMachineOrder(params) { return request({ url: '/chargeMachine.listChargeMachineOrder', method: 'get', params }) }
export function listChargeMachineOrderAcct(params) { return request({ url: '/chargeMachine.listChargeMachineOrderAcct', method: 'get', params }) }
export function listChargeMachine(params) { return request({ url: '/chargeMachine.listChargeMachine', method: 'get', params }) }
export function queryOwnerAccount(params) { return request({ url: '/account.queryOwnerAccount', method: 'get', params }) }

// ===== 充电规则 =====
export function listChargeRule(params) { return request({ url: '/chargeRule.listChargeRule', method: 'get', params }) }
export function saveChargeRule(data) { return request({ url: '/chargeRule.saveChargeRule', method: 'post', data }) }
export function updateChargeRule(data) { return request({ url: '/chargeRule.updateChargeRule', method: 'post', data }) }
export function deleteChargeRule(data) { return request({ url: '/chargeRule.deleteChargeRule', method: 'post', data }) }

export function listChargeRuleFee(params) { return request({ url: '/chargeRule.listChargeRuleFee', method: 'get', params }) }
export function saveChargeRuleFee(data) { return request({ url: '/chargeRule.saveChargeRuleFee', method: 'post', data }) }
export function updateChargeRuleFee(data) { return request({ url: '/chargeRule.updateChargeRuleFee', method: 'post', data }) }
export function deleteChargeRuleFee(data) { return request({ url: '/chargeRule.deleteChargeRuleFee', method: 'post', data }) }

export function listChargeRulePrice(params) { return request({ url: '/chargeRule.listChargeRulePrice', method: 'get', params }) }
export function saveChargeRulePrice(data) { return request({ url: '/chargeRule.saveChargeRulePrice', method: 'post', data }) }
export function updateChargeRulePrice(data) { return request({ url: '/chargeRule.updateChargeRulePrice', method: 'post', data }) }
export function deleteChargeRulePrice(data) { return request({ url: '/chargeRule.deleteChargeRulePrice', method: 'post', data }) }

export function listChargePeakAlleyPrice(params) { return request({ url: '/chargePeakAlleyPrice.listChargePeakAlleyPrice', method: 'get', params }) }
export function saveChargePeakAlleyPrice(data) { return request({ url: '/chargePeakAlleyPrice.saveChargePeakAlleyPrice', method: 'post', data }) }

// ===== 充电月卡订单 =====
export function listChargeMonthOrders(params) { return request({ url: '/chargeCard.listChargeMonthOrder', method: 'get', params }) }
export function saveChargeMonthOrder(data) { return request({ url: '/chargeCard.saveChargeMonthOrder', method: 'post', data }) }
export function listChargeMonthCards(params) { return request({ url: '/chargeCard.listChargeMonthCard', method: 'get', params }) }

// ===== 充电桩管理 =====
export function saveChargeMachine(data) { return request({ url: '/chargeMachine.saveChargeMachine', method: 'post', data }) }
export function updateChargeMachine(data) { return request({ url: '/chargeMachine.updateChargeMachine', method: 'post', data }) }
export function deleteChargeMachine(data) { return request({ url: '/chargeMachine.deleteChargeMachine', method: 'post', data }) }
export function restartChargeMachine(data) { return request({ url: '/chargeMachine.restartChargeMachine', method: 'post', data }) }

// ===== 充电桩端口管理 =====
export function listChargeMachinePort(params) { return request({ url: '/chargeMachine.listChargeMachinePort', method: 'get', params }) }
export function saveChargeMachinePort(data) { return request({ url: '/chargeMachine.saveChargeMachinePort', method: 'post', data }) }
export function deleteChargeMachinePort(data) { return request({ url: '/chargeMachine.deleteChargeMachinePort', method: 'post', data }) }

// ===== 月卡设置 =====
export function listChargeMonthCard(params) { return request({ url: '/chargeCard.listChargeMonthCard', method: 'get', params }) }
export function saveChargeMonthCard(data) { return request({ url: '/chargeCard.saveChargeMonthCard', method: 'post', data }) }
export function updateChargeMonthCard(data) { return request({ url: '/chargeCard.updateChargeMonthCard', method: 'post', data }) }
export function deleteChargeMonthCard(data) { return request({ url: '/chargeCard.deleteChargeMonthCard', method: 'post', data }) }

// ===== 充电统计 =====
export function queryOrderCountStatistics(params) { return request({ url: '/charge.queryOrderCountStatistics', method: 'get', params }) }
export function queryOrderMoneyStatistics(params) { return request({ url: '/charge.queryOrderMoneyStatistics', method: 'get', params }) }
export function queryMonthCountStatistics(params) { return request({ url: '/charge.queryMonthCountStatistics', method: 'get', params }) }
export function queryMonthMoneyStatistics(params) { return request({ url: '/charge.queryMonthMoneyStatistics', method: 'get', params }) }
