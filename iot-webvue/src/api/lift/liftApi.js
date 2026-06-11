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

// ===== 电梯设备管理 =====
export function listLiftMachines(params) {
  return request({ url: '/liftMachine.listLiftMachine', method: 'get', params })
}
export function saveLiftMachine(data) {
  return request({ url: '/liftMachine.saveLiftMachine', method: 'post', data })
}
export function updateLiftMachine(data) {
  return request({ url: '/liftMachine.updateLiftMachine', method: 'post', data })
}
export function deleteLiftMachine(data) {
  return request({ url: '/liftMachine.deleteLiftMachine', method: 'post', data })
}
export function switchLiftState(data) {
  return request({ url: '/liftMachine.switchLiftState', method: 'post', data })
}
export function reservationLiftMachine(data) {
  return request({ url: '/liftMachine.reservationLiftMachine', method: 'post', data })
}

// ===== 电梯门禁（详情Tab用） =====
export function listLiftMachineAcs(params) {
  return request({ url: '/liftMachineAc.listLiftMachineAc', method: 'get', params })
}
export function saveLiftMachineAc(data) {
  return request({ url: '/liftMachineAc.saveLiftMachineAc', method: 'post', data })
}
export function deleteLiftMachineAc(data) {
  return request({ url: '/liftMachineAc.deleteLiftMachineAc', method: 'post', data })
}

// ===== 预约记录 =====
export function listLiftMachineReservations(params) {
  return request({ url: '/liftMachineReservation.listLiftMachineReservation', method: 'get', params })
}

// ===== 电梯日志（详情Tab用） =====
export function listLiftMachineLogs(params) {
  return request({ url: '/liftMachineLog.listLiftMachineLog', method: 'get', params })
}

// ===== 乘梯记录（详情Tab用） =====
export function listLiftRideLogs(params) {
  return request({ url: '/liftMachineLog.listLiftRideLog', method: 'get', params })
}

// ===== 门禁设备（添加电梯门禁用下拉） =====
export function listAccessControls(params) {
  return request({ url: '/accessControl.listAccessControl', method: 'get', params })
}

// ===== 电梯相机 =====
export function listLiftCameras(params) {
  return request({ url: '/liftCamera.listLiftCamera', method: 'get', params })
}
export function saveLiftCamera(data) {
  return request({ url: '/liftCamera.saveLiftCamera', method: 'post', data })
}
export function updateLiftCamera(data) {
  return request({ url: '/liftCamera.updateLiftCamera', method: 'post', data })
}
export function deleteLiftCamera(data) {
  return request({ url: '/liftCamera.deleteLiftCamera', method: 'post', data })
}

// ===== 电梯告警 =====
export function listLiftWarns(params) {
  return request({ url: '/liftCamera.listLiftWarn', method: 'get', params })
}
