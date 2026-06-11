import request from '@/utils/request'

export function listEventPools(params) {
  return request({ url: '/eventPool.listEventPool', method: 'get', params })
}
export function saveEventPool(data) {
  return request({ url: '/eventPool.saveEventPool', method: 'post', data })
}
export function deleteEventPool(data) {
  return request({ url: '/eventPool.deleteEventPool', method: 'post', data })
}
export function updateEventPool(data) {
  return request({ url: '/eventPool.updateEventPool', method: 'post', data })
}

export function listEventPoolStaffs(params) {
  return request({ url: '/eventPoolStaff.listEventPoolStaff', method: 'get', params })
}
export function saveEventPoolStaff(data) {
  return request({ url: '/eventPoolStaff.saveEventPoolStaff', method: 'post', data })
}
export function updateEventPoolStaff(data) {
  return request({ url: '/eventPoolStaff.updateEventPoolStaff', method: 'post', data })
}

export function listEventRules(params) {
  return request({ url: '/eventRule.listEventRule', method: 'get', params })
}
export function saveEventRule(data) {
  return request({ url: '/eventRule.saveEventRule', method: 'post', data })
}
export function updateEventRule(data) {
  return request({ url: '/eventRule.updateEventRule', method: 'post', data })
}
export function deleteEventRule(data) {
  return request({ url: '/eventRule.deleteEventRule', method: 'post', data })
}

export function listEventObjects(params) {
  return request({ url: '/eventObject.listEventObject', method: 'get', params })
}
export function saveEventObject(data) {
  return request({ url: '/eventObject.saveEventObject', method: 'post', data })
}
export function updateEventObject(data) {
  return request({ url: '/eventObject.updateEventObject', method: 'post', data })
}
export function deleteEventObject(data) {
  return request({ url: '/eventObject.deleteEventObject', method: 'post', data })
}

export function listEventStaffs(params) {
  return request({ url: '/eventStaff.listEventStaff', method: 'get', params })
}
export function saveEventStaff(data) {
  return request({ url: '/eventStaff.saveEventStaff', method: 'post', data })
}
export function updateEventStaff(data) {
  return request({ url: '/eventStaff.updateEventStaff', method: 'post', data })
}
export function deleteEventStaff(data) {
  return request({ url: '/eventStaff.deleteEventStaff', method: 'post', data })
}

export function listEventTemplates(params) {
  return request({ url: '/eventTemplate.listEventTemplate', method: 'get', params })
}
export function saveEventTemplate(data) {
  return request({ url: '/eventTemplate.saveEventTemplate', method: 'post', data })
}
export function updateEventTemplate(data) {
  return request({ url: '/eventTemplate.updateEventTemplate', method: 'post', data })
}
export function deleteEventTemplate(data) {
  return request({ url: '/eventTemplate.deleteEventTemplate', method: 'post', data })
}
export function listEventTemplateParamKeys(params) {
  return request({ url: '/eventTemplateParamKey.listEventTemplateParamKey', method: 'get', params })
}

export function listCommunityStaffs(params) {
  return request({ url: '/user.listCommunityStaffsByCommunityId', method: 'get', params })
}
