import request from '@/utils/request'

// ===== 电梯厂家 =====
export function listLiftMachineFactorys(params) {
  return request({ url: '/liftMachineFactory.listLiftMachineFactory', method: 'get', params })
}
export function saveLiftMachineFactory(data) {
  return request({ url: '/liftMachineFactory.saveLiftMachineFactory', method: 'post', data })
}
export function updateLiftMachineFactory(data) {
  return request({ url: '/liftMachineFactory.updateLiftMachineFactory', method: 'post', data })
}
export function deleteLiftMachineFactory(data) {
  return request({ url: '/liftMachineFactory.deleteLiftMachineFactory', method: 'post', data })
}

// ===== 电梯厂家规格 =====
export function listLiftMachineFactorySpecs(params) {
  return request({ url: '/liftMachineFactorySpec.listLiftMachineFactorySpec', method: 'get', params })
}
export function saveLiftMachineFactorySpec(data) {
  return request({ url: '/liftMachineFactorySpec.saveLiftMachineFactorySpec', method: 'post', data })
}
export function deleteLiftMachineFactorySpec(data) {
  return request({ url: '/liftMachineFactorySpec.deleteLiftMachineFactorySpec', method: 'post', data })
}
