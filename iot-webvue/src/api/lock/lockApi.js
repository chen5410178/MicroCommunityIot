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
