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

// ===== admin水电表 =====
export function listAdminMeterMachine(params) {
  return request({ url: '/meterMachine.listAdminMeterMachine', method: 'get', params })
}
export function listMeterMachineFactory(params) {
  return request({ url: '/meterMachine.listMeterMachineFactory', method: 'get', params })
}

// ===== admin充值明细 =====
export function listAdminMeterCharge(params) {
  return request({ url: '/meterMachineCharge.listAdminMeterCharge', method: 'get', params })
}

// ===== admin水电表详情 =====
export function listAdminMeterReadRechargeDetail(params) {
  return request({ url: '/meterMachine.listAdminMeterReadRechargeDetail', method: 'get', params })
}

// ===== admin房屋查询 =====
export function queryAdminRooms(params) {
  return request({ url: '/room.queryAdminRooms', method: 'get', params })
}
