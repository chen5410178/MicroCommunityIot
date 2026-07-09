import request from '@/utils/request'

export function listLiftMachineFactorys(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactory.listLiftMachineFactory', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLiftMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactory.saveLiftMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLiftMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactory.updateLiftMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLiftMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactory.deleteLiftMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftMachineFactorySpecs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactorySpec.listLiftMachineFactorySpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLiftMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactorySpec.saveLiftMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLiftMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineFactorySpec.deleteLiftMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftMachines(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.listLiftMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLiftMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.saveLiftMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLiftMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.updateLiftMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLiftMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.deleteLiftMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function switchLiftState(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.switchLiftState', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function reservationLiftMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachine.reservationLiftMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftMachineAcs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineAc.listLiftMachineAc', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLiftMachineAc(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineAc.saveLiftMachineAc', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLiftMachineAc(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineAc.deleteLiftMachineAc', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftMachineReservations(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineReservation.listLiftMachineReservation', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftMachineLogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineLog.listLiftMachineLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftRideLogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftMachineLog.listLiftRideLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAccessControls(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/accessControl.listAccessControl', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftCameras(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftCamera.listLiftCamera', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLiftCamera(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftCamera.saveLiftCamera', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLiftCamera(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftCamera.updateLiftCamera', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLiftCamera(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftCamera.deleteLiftCamera', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLiftWarns(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/liftCamera.listLiftWarn', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}
