import request from '@/utils/request'

export function listApps(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/app.listApps', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveApp(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/app.saveApp', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateApp(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/app.updateApp', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteApp(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/app.deleteApp', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAttrSpecs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrSpec.listAttrSpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveAttrSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrSpec.saveAttrSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateAttrSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrSpec.updateAttrSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteAttrSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrSpec.deleteAttrSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAttrValues(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrValue.listAttrValue', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveAttrValue(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrValue.saveAttrValue', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateAttrValue(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrValue.updateAttrValue', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteAttrValue(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/attrValue.deleteAttrValue', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listBasePrivileges(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveBasePrivilege(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.saveBasePrivilege', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateBasePrivilege(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.updateBasePrivilege', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteBasePrivilege(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.deleteBasePrivilege', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryBusinessDatabus(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessDatabus/queryBusinessDatabus', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveBusinessDatabus(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessDatabus/saveBusinessDatabus', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateBusinessDatabus(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessDatabus/updateBusinessDatabus', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteBusinessDatabus(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessDatabus/deleteBusinessDatabus', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listBusinessTableHis(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessTableHis.listBusinessTableHis', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveBusinessTableHis(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessTableHis.saveBusinessTableHis', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateBusinessTableHis(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessTableHis.updateBusinessTableHis', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteBusinessTableHis(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/businessTableHis.deleteBusinessTableHis', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listComponentConditions(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentCondition.listReportCustomComponentCondition', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveComponentCondition(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentCondition.saveReportCustomComponentCondition', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateComponentCondition(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentCondition.updateReportCustomComponentCondition', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteComponentCondition(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentCondition.deleteReportCustomComponentCondition', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listDicts(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/dict.listDict', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveDict(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dict.saveDict', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateDict(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dict.updateDict', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteDict(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dict.deleteDict', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listDictSpecs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/dictSpec.listDictSpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveDictSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dictSpec.saveDictSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateDictSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dictSpec.updateDictSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteDictSpec(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/dictSpec.deleteDictSpec', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTasks(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.listTasks', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveTask(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.saveTask', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateTask(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.updateTask', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteTask(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.deleteTask', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function startTask(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.startTask', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function stopTask(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.stopTask', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTaskTemplate(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.listTaskTemplate', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listTaskTemplateSpec(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/task.listTaskTemplateSpec', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMappingDomains(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.listMappingDomain', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMappingDomain(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.saveMappingDomain', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMappingDomain(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.updateMappingDomain', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMappingDomain(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.deleteMappingDomain', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMappings(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.listMappings', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMapping(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.saveMapping', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMapping(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.updateMapping', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMapping(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/mapping.deleteMapping', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenuCatalogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuCatalog.listMenuCatalog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMenuCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuCatalog.saveMenuCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMenuCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuCatalog.updateMenuCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMenuCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuCatalog.deleteMenuCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenuGroupCatalogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroupCatalog.listMenuGroupCatalog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMenuGroupCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroupCatalog.saveMenuGroupCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMenuGroupCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroupCatalog.updateMenuGroupCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMenuGroupCatalog(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroupCatalog.deleteMenuGroupCatalog', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenuGroups(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroup.listMenuGroups', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMenuGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroup.saveMenuGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMenuGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroup.updateMenuGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMenuGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroup.deleteMenuGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenus(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.listMenus', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveMenu(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.saveMenu', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateMenu(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.updateMenu', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteMenu(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.deleteMenu', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listReportCustomComponentFooters(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentFooter.listReportCustomComponentFooter', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveReportCustomComponentFooter(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentFooter.saveReportCustomComponentFooter', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateReportCustomComponentFooter(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentFooter.updateReportCustomComponentFooter', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteReportCustomComponentFooter(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentFooter.deleteReportCustomComponentFooter', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listReportCustomComponents(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponent.listReportCustomComponent', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveReportCustomComponent(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponent.saveReportCustomComponent', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateReportCustomComponent(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponent.updateReportCustomComponent', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteReportCustomComponent(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponent.deleteReportCustomComponent', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listReportCustomComponentRels(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentRel.listReportCustomComponentRel', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveReportCustomComponentRel(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentRel.saveReportCustomComponentRel', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateReportCustomComponentRel(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentRel.updateReportCustomComponentRel', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteReportCustomComponentRel(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomComponentRel.deleteReportCustomComponentRel', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listReportCustomGroups(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomGroup.listReportCustomGroup', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveReportCustomGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomGroup.saveReportCustomGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateReportCustomGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomGroup.updateReportCustomGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteReportCustomGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustomGroup.deleteReportCustomGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listReportCustoms(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustom.listReportCustom', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveReportCustom(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustom.saveReportCustom', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateReportCustom(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustom.updateReportCustom', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteReportCustom(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/reportCustom.deleteReportCustom', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listServiceImpls(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceImpl.listServiceImpls', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveServiceImpl(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceImpl.saveServiceImpl', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateServiceImpl(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceImpl.updateServiceImpl', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteServiceImpl(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceImpl.deleteServiceImpl', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listServices(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/service.listServices', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveService(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/service.saveService', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateService(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/service.updateService', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteService(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/service.deleteService', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listServiceProvides(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceProvide.listServiceProvides', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveServiceProvide(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceProvide.saveServiceProvide', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateServiceProvide(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceProvide.updateServiceProvide', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteServiceProvide(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceProvide.deleteServiceProvide', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listServiceRegisters(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceRegister.listServiceRegisters', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateServiceRegister(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceRegister.updateServiceRegister', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteServiceRegister(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/serviceRegister.deleteServiceRegister', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function querySysDocuments(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/sysDocument/querySysDocument', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveSysDocument(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/sysDocument/saveSysDocument', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updateSysDocument(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/sysDocument/updateSysDocument', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteSysDocument(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/sysDocument/deleteSysDocument', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listCache(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/cache.listCache', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function flushCache(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/cache.flushCache', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryDemoConfig(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/demo.queryDemoConfig', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryTransactionLogs(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/transactionLog/queryTransactionLog', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function queryTransactionLogMessage(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/transactionLog/queryTransactionLogMessage', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listCommunitys(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/community.listCommunitys', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deleteBuildingUnit(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/initializeBuildingUnit.deleteBuildingUnit', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function configMenu(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.configMenu', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenuGroupsAll(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menuGroup.listMenuGroups', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listMenusAll(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/menu.listMenus', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listBasePrivilegesAll(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listAppsAll(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/app.listApps', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function saveRoute(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/route.saveRoute', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function bindingService(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/service.bindingService', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listCServiceDict(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/dict.listDict', method: 'get', params: { ...params, tableName: 'c_service', columnName: 'url' } }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listPrivilegeGroups(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/privilegeGroup.listPrivilegeGroups', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function savePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/privilegeGroup.savePrivilegeGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function updatePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/privilegeGroup.updatePrivilegeGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}

export function deletePrivilegeGroup(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/privilegeGroup.deletePrivilegeGroup', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '删除失败')) }
    }).catch(error => { reject(error) })
  })
}

export function listPrivileges(params) {
  return new Promise((resolve, reject) => {
    request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '查询列表失败')) }
    }).catch(error => { reject(error) })
  })
}

export function savePrivilegeGroupPrivilege(data) {
  return new Promise((resolve, reject) => {
    request({ url: '/privilegeGroup.savePrivilegeGroupPrivilege', method: 'post', data }).then(res => {
      if (res.code == 0) { resolve(res) } else { reject(new Error(res.msg || '操作失败')) }
    }).catch(error => { reject(error) })
  })
}
