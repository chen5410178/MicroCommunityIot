import request from '@/utils/request'

// ===== 门锁厂家 =====
export function listLockMachineFactorys(params) {
  return request({ url: '/lockMachineFactory.listLockMachineFactory', method: 'get', params })
}
export function saveLockMachineFactory(data) {
  return request({ url: '/lockMachineFactory.saveLockMachineFactory', method: 'post', data })
}
export function updateLockMachineFactory(data) {
  return request({ url: '/lockMachineFactory.updateLockMachineFactory', method: 'post', data })
}
export function deleteLockMachineFactory(data) {
  return request({ url: '/lockMachineFactory.deleteLockMachineFactory', method: 'post', data })
}

// ===== 门锁厂家规格 =====
export function listLockMachineFactorySpecs(params) {
  return request({ url: '/lockMachineFactorySpec.listLockMachineFactorySpec', method: 'get', params })
}
export function saveLockMachineFactorySpec(data) {
  return request({ url: '/lockMachineFactorySpec.saveLockMachineFactorySpec', method: 'post', data })
}
export function deleteLockMachineFactorySpec(data) {
  return request({ url: '/lockMachineFactorySpec.deleteLockMachineFactorySpec', method: 'post', data })
}

// ===== 门锁授权管理 =====
export function listLockPersons(params) {
  return request({ url: '/lockPerson.listLockPerson', method: 'get', params })
}
export function saveLockPerson(data) {
  return request({ url: '/lockPerson.saveLockPerson', method: 'post', data })
}
export function updateLockPerson(data) {
  return request({ url: '/lockPerson.updateLockPerson', method: 'post', data })
}
export function deleteLockPerson(data) {
  return request({ url: '/lockPerson.deleteLockPerson', method: 'post', data })
}
export function renewalLockPerson(data) {
  return request({ url: '/lockPerson.renewalLockPerson', method: 'post', data })
}
export function resetLockPwd(data) {
  return request({ url: '/lockPerson.resetLockPwd', method: 'post', data })
}

// ===== 门锁设备CRUD =====
export function listLockMachines(params) {
  return request({ url: '/lockMachine.listLockMachine', method: 'get', params })
}
export function saveLockMachine(data) {
  return request({ url: '/lockMachine.saveLockMachine', method: 'post', data })
}
export function updateLockMachine(data) {
  return request({ url: '/lockMachine.updateLockMachine', method: 'post', data })
}
export function deleteLockMachine(data) {
  return request({ url: '/lockMachine.deleteLockMachine', method: 'post', data })
}
export function unLockMachine(data) {
  return request({ url: '/lockMachine.unLockMachine', method: 'post', data })
}

// ===== 门锁下发记录 =====
export function listLockLogs(params) {
  return request({ url: '/lockLog.listLockLog', method: 'get', params })
}

// ===== 门锁进出记录 =====
export function listLockInouts(params) {
  return request({ url: '/lockInout.listLockInout', method: 'get', params })
}
