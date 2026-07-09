import request from '@/utils/request'

export function listLampMachineFactory(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactory.listLampMachineFactory', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLampMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactory.saveLampMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLampMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactory.updateLampMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLampMachineFactory(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactory.deleteLampMachineFactory', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLampMachineFactorySpec(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactorySpec.listLampMachineFactorySpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLampMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactorySpec.saveLampMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLampMachineFactorySpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineFactorySpec.deleteLampMachineFactorySpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelMachine(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachine.listTelMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelMachineUser(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachineUser.listTelMachineUser', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveTelMachineUser(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachineUser.saveTelMachineUser', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteTelMachineUser(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachineUser.deleteTelMachineUser', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listStaffs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/query.staff.infos', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelMachines(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachine.listTelMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveTelMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachine.saveTelMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateTelMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachine.updateTelMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteTelMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telMachine.deleteTelMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLampMachine(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachine.listLampMachine', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLampMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachine.saveLampMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateLampMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachine.updateLampMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteLampMachine(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachine.deleteLampMachine', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function switchLampState(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachine.switchLampState', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLampMachineOpenClose(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineOpenClose.listLampMachineOpenClose', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveLampMachineOpenClose(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineOpenClose.saveLampMachineOpenClose', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLampMachineOperation(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineOperation.listLampMachineOperation', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listLampMachineLog(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/lampMachineLog.listLampMachineLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelCallLog(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telCallLog.listTelCallLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelSmsLog(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telSmsLog.listTelSmsLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTelTts(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/telTts.listTelTts', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveTelTts(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telTts.saveTelTts', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateTelTts(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telTts.updateTelTts', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteTelTts(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/telTts.deleteTelTts', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listCommunityStaffs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/user.listCommunityStaffsByCommunityId', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}
