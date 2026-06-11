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
