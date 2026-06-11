import request from '@/utils/request'

export function listLampMachineFactory(params) { return request({ url: '/lampMachineFactory.listLampMachineFactory', method: 'get', params }) }
export function saveLampMachineFactory(data) { return request({ url: '/lampMachineFactory.saveLampMachineFactory', method: 'post', data }) }
export function updateLampMachineFactory(data) { return request({ url: '/lampMachineFactory.updateLampMachineFactory', method: 'post', data }) }
export function deleteLampMachineFactory(data) { return request({ url: '/lampMachineFactory.deleteLampMachineFactory', method: 'post', data }) }

export function listLampMachineFactorySpec(params) { return request({ url: '/lampMachineFactorySpec.listLampMachineFactorySpec', method: 'get', params }) }
export function saveLampMachineFactorySpec(data) { return request({ url: '/lampMachineFactorySpec.saveLampMachineFactorySpec', method: 'post', data }) }
export function deleteLampMachineFactorySpec(data) { return request({ url: '/lampMachineFactorySpec.deleteLampMachineFactorySpec', method: 'post', data }) }
