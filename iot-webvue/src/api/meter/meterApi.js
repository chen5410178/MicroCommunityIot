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

// ===== 水电类型 =====
export function listMeterTypes(params) {
  return request({ url: '/meterType.listMeterType', method: 'get', params })
}
export function saveMeterType(data) {
  return request({ url: '/meterType.saveMeterType', method: 'post', data })
}
export function updateMeterType(data) {
  return request({ url: '/meterType.updateMeterType', method: 'post', data })
}
export function deleteMeterType(data) {
  return request({ url: '/meterType.deleteMeterType', method: 'post', data })
}

// ===== 水电表管理（物业端） =====
export function listMeterMachines(params) {
  return request({ url: '/meterMachine.listMeterMachine', method: 'get', params })
}
export function saveMeterMachine(data) {
  return request({ url: '/meterMachine.saveMeterMachine', method: 'post', data })
}
export function updateMeterMachine(data) {
  return request({ url: '/meterMachine.updateMeterMachine', method: 'post', data })
}
export function deleteMeterMachine(data) {
  return request({ url: '/meterMachine.deleteMeterMachine', method: 'post', data })
}
export function cleanMeterMachine(data) {
  return request({ url: '/meterMachine.cleanMeterMachine', method: 'post', data })
}
export function meterMachineControlSwitch(data) {
  return request({ url: '/meterMachine.meterMachineControlSwitch', method: 'post', data })
}

// ===== 抄表设置 =====
export function settingMeterMachineRead(data) {
  return request({ url: '/meterMachine.settingMeterMachineRead', method: 'post', data })
}
export function customMeterMachineRead(data) {
  return request({ url: '/meterMachine.customMeterMachineRead', method: 'post', data })
}

// ===== 开合闸记录 =====
export function listMeterMachineLog(params) {
  return request({ url: '/meterMachineLog.listMeterMachineLog', method: 'get', params })
}

// ===== 充值 =====
export function chargeMeterMoney(data) {
  return request({ url: '/meterMachineCharge.chargeMeterMoney', method: 'post', data })
}
export function listMeterMachineCharges(params) {
  return request({ url: '/meterMachineCharge.listMeterMachineCharge', method: 'get', params })
}

// ===== 水电二维码 =====
export function listMeterQrcodes(params) {
  return request({ url: '/meterQrcode.listMeterQrcode', method: 'get', params })
}
export function saveMeterQrcode(data) {
  return request({ url: '/meterQrcode.saveMeterQrcode', method: 'post', data })
}
export function updateMeterQrcode(data) {
  return request({ url: '/meterQrcode.updateMeterQrcode', method: 'post', data })
}
export function deleteMeterQrcode(data) {
  return request({ url: '/meterQrcode.deleteMeterQrcode', method: 'post', data })
}

// ===== 仪表组 =====
export function listInstrumentTypes(params) { return request({ url: '/instrumentType.listInstrumentType', method: 'get', params }) }
export function saveInstrumentType(data) { return request({ url: '/instrumentType.saveInstrumentType', method: 'post', data }) }
export function updateInstrumentType(data) { return request({ url: '/instrumentType.updateInstrumentType', method: 'post', data }) }
export function deleteInstrumentType(data) { return request({ url: '/instrumentType.deleteInstrumentType', method: 'post', data }) }

// ===== 水电统计 =====
export function queryMeterCountStatistics(params) {
  return request({ url: '/meter.queryMeterCountStatistics', method: 'get', params })
}
export function queryMeterMoneyStatistics(params) {
  return request({ url: '/meter.queryMeterMoneyStatistics', method: 'get', params })
}

// ===== 仪表设备 =====
export function listInstruments(params) {
  return request({ url: '/instrument.listInstrument', method: 'get', params })
}
export function saveInstrument(data) {
  return request({ url: '/instrument.saveInstrument', method: 'post', data })
}
export function updateInstrument(data) {
  return request({ url: '/instrument.updateInstrument', method: 'post', data })
}
export function deleteInstrument(data) {
  return request({ url: '/instrument.deleteInstrument', method: 'post', data })
}
export function listInstrumentLogs(params) {
  return request({ url: '/instrumentLog.listInstrumentLog', method: 'get', params })
}
