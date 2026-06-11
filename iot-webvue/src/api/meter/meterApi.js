import request from '@/utils/request'

export function listInstrumentFactory(params) { return request({ url: '/instrumentFactory.listInstrumentFactory', method: 'get', params }) }
export function saveInstrumentFactory(data) { return request({ url: '/instrumentFactory.saveInstrumentFactory', method: 'post', data }) }
export function updateInstrumentFactory(data) { return request({ url: '/instrumentFactory.updateInstrumentFactory', method: 'post', data }) }
export function deleteInstrumentFactory(data) { return request({ url: '/instrumentFactory.deleteInstrumentFactory', method: 'post', data }) }

export function listInstrumentFactorySpec(params) { return request({ url: '/instrumentFactorySpec.listInstrumentFactorySpec', method: 'get', params }) }
export function saveInstrumentFactorySpec(data) { return request({ url: '/instrumentFactorySpec.saveInstrumentFactorySpec', method: 'post', data }) }
export function deleteInstrumentFactorySpec(data) { return request({ url: '/instrumentFactorySpec.deleteInstrumentFactorySpec', method: 'post', data }) }

// ===== 水电厂家 =====
export function listMeterMachineFactorys(params) {
  return request({ url: '/meterMachineFactory.listMeterMachineFactory', method: 'get', params })
}
export function saveMeterMachineFactory(data) {
  return request({ url: '/meterMachineFactory.saveMeterMachineFactory', method: 'post', data })
}
export function updateMeterMachineFactory(data) {
  return request({ url: '/meterMachineFactory.updateMeterMachineFactory', method: 'post', data })
}
export function deleteMeterMachineFactory(data) {
  return request({ url: '/meterMachineFactory.deleteMeterMachineFactory', method: 'post', data })
}

// ===== 水电厂家规格 =====
export function listMeterMachineFactorySpecs(params) {
  return request({ url: '/meterMachineFactorySpec.listMeterMachineFactorySpec', method: 'get', params })
}
export function saveMeterMachineFactorySpec(data) {
  return request({ url: '/meterMachineFactorySpec.saveMeterMachineFactorySpec', method: 'post', data })
}
export function deleteMeterMachineFactorySpec(data) {
  return request({ url: '/meterMachineFactorySpec.deleteMeterMachineFactorySpec', method: 'post', data })
}
