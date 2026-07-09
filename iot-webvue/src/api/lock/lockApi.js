import request from '@/utils/request'

export function listLockMachineFactorys(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactory.listLockMachineFactory', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLockMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactory.saveLockMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLockMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactory.updateLockMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLockMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactory.deleteLockMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLockMachineFactorySpecs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactorySpec.listLockMachineFactorySpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLockMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactorySpec.saveLockMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLockMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachineFactorySpec.deleteLockMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLockPersons(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.listLockPerson', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLockPerson(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.saveLockPerson', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLockPerson(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.updateLockPerson', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLockPerson(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.deleteLockPerson', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function renewalLockPerson(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.renewalLockPerson', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function resetLockPwd(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockPerson.resetLockPwd', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLockMachines(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachine.listLockMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLockMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachine.saveLockMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLockMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachine.updateLockMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLockMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachine.deleteLockMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function unLockMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockMachine.unLockMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLockLogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockLog.listLockLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLockInouts(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lockInout.listLockInout', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}
