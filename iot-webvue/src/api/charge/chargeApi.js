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
