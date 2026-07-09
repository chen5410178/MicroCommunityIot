import request from '@/utils/request'

export function listEventPools(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPool.listEventPool', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventPool(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPool.saveEventPool', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteEventPool(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPool.deleteEventPool', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventPool(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPool.updateEventPool', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventPoolStaffs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPoolStaff.listEventPoolStaff', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventPoolStaff(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPoolStaff.saveEventPoolStaff', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventPoolStaff(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventPoolStaff.updateEventPoolStaff', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventRules(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventRule.listEventRule', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventRule.saveEventRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventRule.updateEventRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteEventRule(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventRule.deleteEventRule', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventObjects(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventObject.listEventObject', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventObject(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventObject.saveEventObject', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventObject(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventObject.updateEventObject', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteEventObject(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventObject.deleteEventObject', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventStaffs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventStaff.listEventStaff', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventStaff(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventStaff.saveEventStaff', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventStaff(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventStaff.updateEventStaff', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteEventStaff(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventStaff.deleteEventStaff', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventTemplates(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventTemplate.listEventTemplate', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveEventTemplate(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventTemplate.saveEventTemplate', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateEventTemplate(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventTemplate.updateEventTemplate', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteEventTemplate(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventTemplate.deleteEventTemplate', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listEventTemplateParamKeys(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/eventTemplateParamKey.listEventTemplateParamKey', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
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
