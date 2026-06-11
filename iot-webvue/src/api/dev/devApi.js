import request from '@/utils/request'

// ===== 1. 应用管理 =====
export function listApps(params) { return request({ url: '/app.listApps', method: 'get', params }) }
export function saveApp(data) { return request({ url: '/app.saveApp', method: 'post', data }) }
export function updateApp(data) { return request({ url: '/app.updateApp', method: 'post', data }) }
export function deleteApp(data) { return request({ url: '/app.deleteApp', method: 'post', data }) }

// ===== 2. 属性规格管理 =====
export function listAttrSpecs(params) { return request({ url: '/attrSpec.listAttrSpec', method: 'get', params }) }
export function saveAttrSpec(data) { return request({ url: '/attrSpec.saveAttrSpec', method: 'post', data }) }
export function updateAttrSpec(data) { return request({ url: '/attrSpec.updateAttrSpec', method: 'post', data }) }
export function deleteAttrSpec(data) { return request({ url: '/attrSpec.deleteAttrSpec', method: 'post', data }) }

// ===== 3. 属性值管理 =====
export function listAttrValues(params) { return request({ url: '/attrValue.listAttrValue', method: 'get', params }) }
export function saveAttrValue(data) { return request({ url: '/attrValue.saveAttrValue', method: 'post', data }) }
export function updateAttrValue(data) { return request({ url: '/attrValue.updateAttrValue', method: 'post', data }) }
export function deleteAttrValue(data) { return request({ url: '/attrValue.deleteAttrValue', method: 'post', data }) }

// ===== 4. 基础权限管理 =====
export function listBasePrivileges(params) { return request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }) }
export function saveBasePrivilege(data) { return request({ url: '/basePrivilege.saveBasePrivilege', method: 'post', data }) }
export function updateBasePrivilege(data) { return request({ url: '/basePrivilege.updateBasePrivilege', method: 'post', data }) }
export function deleteBasePrivilege(data) { return request({ url: '/basePrivilege.deleteBasePrivilege', method: 'post', data }) }

// ===== 5. 业务数据总线管理 =====
export function queryBusinessDatabus(params) { return request({ url: '/businessDatabus/queryBusinessDatabus', method: 'get', params }) }
export function saveBusinessDatabus(data) { return request({ url: '/businessDatabus/saveBusinessDatabus', method: 'post', data }) }
export function updateBusinessDatabus(data) { return request({ url: '/businessDatabus/updateBusinessDatabus', method: 'post', data }) }
export function deleteBusinessDatabus(data) { return request({ url: '/businessDatabus/deleteBusinessDatabus', method: 'post', data }) }

// ===== 6. 业务表历史管理 =====
export function listBusinessTableHis(params) { return request({ url: '/businessTableHis.listBusinessTableHis', method: 'get', params }) }
export function saveBusinessTableHis(data) { return request({ url: '/businessTableHis.saveBusinessTableHis', method: 'post', data }) }
export function updateBusinessTableHis(data) { return request({ url: '/businessTableHis.updateBusinessTableHis', method: 'post', data }) }
export function deleteBusinessTableHis(data) { return request({ url: '/businessTableHis.deleteBusinessTableHis', method: 'post', data }) }

// ===== 7. 组件条件管理 =====
export function listComponentConditions(params) { return request({ url: '/reportCustomComponentCondition.listReportCustomComponentCondition', method: 'get', params }) }
export function saveComponentCondition(data) { return request({ url: '/reportCustomComponentCondition.saveReportCustomComponentCondition', method: 'post', data }) }
export function updateComponentCondition(data) { return request({ url: '/reportCustomComponentCondition.updateReportCustomComponentCondition', method: 'post', data }) }
export function deleteComponentCondition(data) { return request({ url: '/reportCustomComponentCondition.deleteReportCustomComponentCondition', method: 'post', data }) }

// ===== 8. 字典管理 =====
export function listDicts(params) { return request({ url: '/dict.listDict', method: 'get', params }) }
export function saveDict(data) { return request({ url: '/dict.saveDict', method: 'post', data }) }
export function updateDict(data) { return request({ url: '/dict.updateDict', method: 'post', data }) }
export function deleteDict(data) { return request({ url: '/dict.deleteDict', method: 'post', data }) }

// ===== 8-1. 字典类型管理 =====
export function listDictSpecs(params) { return request({ url: '/dictSpec.listDictSpec', method: 'get', params }) }
export function saveDictSpec(data) { return request({ url: '/dictSpec.saveDictSpec', method: 'post', data }) }
export function updateDictSpec(data) { return request({ url: '/dictSpec.updateDictSpec', method: 'post', data }) }
export function deleteDictSpec(data) { return request({ url: '/dictSpec.deleteDictSpec', method: 'post', data }) }

// ===== 9. 任务管理 =====
export function listTasks(params) { return request({ url: '/task.listTasks', method: 'get', params }) }
export function saveTask(data) { return request({ url: '/task.saveTask', method: 'post', data }) }
export function updateTask(data) { return request({ url: '/task.updateTask', method: 'post', data }) }
export function deleteTask(data) { return request({ url: '/task.deleteTask', method: 'post', data }) }
export function startTask(data) { return request({ url: '/task.startTask', method: 'post', data }) }
export function stopTask(data) { return request({ url: '/task.stopTask', method: 'post', data }) }
export function listTaskTemplate(params) { return request({ url: '/task.listTaskTemplate', method: 'get', params }) }
export function listTaskTemplateSpec(params) { return request({ url: '/task.listTaskTemplateSpec', method: 'get', params }) }

// ===== 10. 映射域名管理 =====
export function listMappingDomains(params) { return request({ url: '/mapping.listMappingDomain', method: 'get', params }) }
export function saveMappingDomain(data) { return request({ url: '/mapping.saveMappingDomain', method: 'post', data }) }
export function updateMappingDomain(data) { return request({ url: '/mapping.updateMappingDomain', method: 'post', data }) }
export function deleteMappingDomain(data) { return request({ url: '/mapping.deleteMappingDomain', method: 'post', data }) }

// ===== 11. 映射管理 =====
export function listMappings(params) { return request({ url: '/mapping.listMappings', method: 'get', params }) }
export function saveMapping(data) { return request({ url: '/mapping.saveMapping', method: 'post', data }) }
export function updateMapping(data) { return request({ url: '/mapping.updateMapping', method: 'post', data }) }
export function deleteMapping(data) { return request({ url: '/mapping.deleteMapping', method: 'post', data }) }

// ===== 12. 菜单目录管理 =====
export function listMenuCatalogs(params) { return request({ url: '/menuCatalog.listMenuCatalog', method: 'get', params }) }
export function saveMenuCatalog(data) { return request({ url: '/menuCatalog.saveMenuCatalog', method: 'post', data }) }
export function updateMenuCatalog(data) { return request({ url: '/menuCatalog.updateMenuCatalog', method: 'post', data }) }
export function deleteMenuCatalog(data) { return request({ url: '/menuCatalog.deleteMenuCatalog', method: 'post', data }) }

// ===== 13. 菜单组目录管理 =====
export function listMenuGroupCatalogs(params) { return request({ url: '/menuGroupCatalog.listMenuGroupCatalog', method: 'get', params }) }
export function saveMenuGroupCatalog(data) { return request({ url: '/menuGroupCatalog.saveMenuGroupCatalog', method: 'post', data }) }
export function updateMenuGroupCatalog(data) { return request({ url: '/menuGroupCatalog.updateMenuGroupCatalog', method: 'post', data }) }
export function deleteMenuGroupCatalog(data) { return request({ url: '/menuGroupCatalog.deleteMenuGroupCatalog', method: 'post', data }) }

// ===== 14. 菜单组管理 =====
export function listMenuGroups(params) { return request({ url: '/menuGroup.listMenuGroups', method: 'get', params }) }
export function saveMenuGroup(data) { return request({ url: '/menuGroup.saveMenuGroup', method: 'post', data }) }
export function updateMenuGroup(data) { return request({ url: '/menuGroup.updateMenuGroup', method: 'post', data }) }
export function deleteMenuGroup(data) { return request({ url: '/menuGroup.deleteMenuGroup', method: 'post', data }) }

// ===== 15. 菜单管理 =====
export function listMenus(params) { return request({ url: '/menu.listMenus', method: 'get', params }) }
export function saveMenu(data) { return request({ url: '/menu.saveMenu', method: 'post', data }) }
export function updateMenu(data) { return request({ url: '/menu.updateMenu', method: 'post', data }) }
export function deleteMenu(data) { return request({ url: '/menu.deleteMenu', method: 'post', data }) }

// ===== 16. 报表组件底部管理 =====
export function listReportCustomComponentFooters(params) { return request({ url: '/reportCustomComponentFooter.listReportCustomComponentFooter', method: 'get', params }) }
export function saveReportCustomComponentFooter(data) { return request({ url: '/reportCustomComponentFooter.saveReportCustomComponentFooter', method: 'post', data }) }
export function updateReportCustomComponentFooter(data) { return request({ url: '/reportCustomComponentFooter.updateReportCustomComponentFooter', method: 'post', data }) }
export function deleteReportCustomComponentFooter(data) { return request({ url: '/reportCustomComponentFooter.deleteReportCustomComponentFooter', method: 'post', data }) }

// ===== 17. 报表组件管理 =====
export function listReportCustomComponents(params) { return request({ url: '/reportCustomComponent.listReportCustomComponent', method: 'get', params }) }
export function saveReportCustomComponent(data) { return request({ url: '/reportCustomComponent.saveReportCustomComponent', method: 'post', data }) }
export function updateReportCustomComponent(data) { return request({ url: '/reportCustomComponent.updateReportCustomComponent', method: 'post', data }) }
export function deleteReportCustomComponent(data) { return request({ url: '/reportCustomComponent.deleteReportCustomComponent', method: 'post', data }) }

// ===== 18. 报表组件关联管理 =====
export function listReportCustomComponentRels(params) { return request({ url: '/reportCustomComponentRel.listReportCustomComponentRel', method: 'get', params }) }
export function saveReportCustomComponentRel(data) { return request({ url: '/reportCustomComponentRel.saveReportCustomComponentRel', method: 'post', data }) }
export function updateReportCustomComponentRel(data) { return request({ url: '/reportCustomComponentRel.updateReportCustomComponentRel', method: 'post', data }) }
export function deleteReportCustomComponentRel(data) { return request({ url: '/reportCustomComponentRel.deleteReportCustomComponentRel', method: 'post', data }) }

// ===== 19. 报表组管理 =====
export function listReportCustomGroups(params) { return request({ url: '/reportCustomGroup.listReportCustomGroup', method: 'get', params }) }
export function saveReportCustomGroup(data) { return request({ url: '/reportCustomGroup.saveReportCustomGroup', method: 'post', data }) }
export function updateReportCustomGroup(data) { return request({ url: '/reportCustomGroup.updateReportCustomGroup', method: 'post', data }) }
export function deleteReportCustomGroup(data) { return request({ url: '/reportCustomGroup.deleteReportCustomGroup', method: 'post', data }) }

// ===== 20. 报表管理 =====
export function listReportCustoms(params) { return request({ url: '/reportCustom.listReportCustom', method: 'get', params }) }
export function saveReportCustom(data) { return request({ url: '/reportCustom.saveReportCustom', method: 'post', data }) }
export function updateReportCustom(data) { return request({ url: '/reportCustom.updateReportCustom', method: 'post', data }) }
export function deleteReportCustom(data) { return request({ url: '/reportCustom.deleteReportCustom', method: 'post', data }) }

// ===== 21. 服务实现管理 =====
export function listServiceImpls(params) { return request({ url: '/serviceImpl.listServiceImpls', method: 'get', params }) }
export function saveServiceImpl(data) { return request({ url: '/serviceImpl.saveServiceImpl', method: 'post', data }) }
export function updateServiceImpl(data) { return request({ url: '/serviceImpl.updateServiceImpl', method: 'post', data }) }
export function deleteServiceImpl(data) { return request({ url: '/serviceImpl.deleteServiceImpl', method: 'post', data }) }

// ===== 22. 服务管理 =====
export function listServices(params) { return request({ url: '/service.listServices', method: 'get', params }) }
export function saveService(data) { return request({ url: '/service.saveService', method: 'post', data }) }
export function updateService(data) { return request({ url: '/service.updateService', method: 'post', data }) }
export function deleteService(data) { return request({ url: '/service.deleteService', method: 'post', data }) }

// ===== 23. 服务提供管理 =====
export function listServiceProvides(params) { return request({ url: '/serviceProvide.listServiceProvides', method: 'get', params }) }
export function saveServiceProvide(data) { return request({ url: '/serviceProvide.saveServiceProvide', method: 'post', data }) }
export function updateServiceProvide(data) { return request({ url: '/serviceProvide.updateServiceProvide', method: 'post', data }) }
export function deleteServiceProvide(data) { return request({ url: '/serviceProvide.deleteServiceProvide', method: 'post', data }) }

// ===== 24. 服务注册管理 =====
export function listServiceRegisters(params) { return request({ url: '/serviceRegister.listServiceRegisters', method: 'get', params }) }
export function updateServiceRegister(data) { return request({ url: '/serviceRegister.updateServiceRegister', method: 'post', data }) }
export function deleteServiceRegister(data) { return request({ url: '/serviceRegister.deleteServiceRegister', method: 'post', data }) }

// ===== 25. 系统文档管理 =====
export function querySysDocuments(params) { return request({ url: '/sysDocument/querySysDocument', method: 'get', params }) }
export function saveSysDocument(data) { return request({ url: '/sysDocument/saveSysDocument', method: 'post', data }) }
export function updateSysDocument(data) { return request({ url: '/sysDocument/updateSysDocument', method: 'post', data }) }
export function deleteSysDocument(data) { return request({ url: '/sysDocument/deleteSysDocument', method: 'post', data }) }

// ===== 26. 缓存管理 =====
export function listCache(params) { return request({ url: '/cache.listCache', method: 'get', params }) }
export function refreshCache(data) { return request({ url: '/cache.refreshCache', method: 'post', data }) }

// ===== 27. 列表演示 =====
export function queryDemoConfig(params) { return request({ url: '/demo.queryDemoConfig', method: 'get', params }) }

// ===== 28. 事务日志管理 =====
export function queryTransactionLogs(params) { return request({ url: '/transactionLog/queryTransactionLog', method: 'get', params }) }
export function queryTransactionLogMessage(params) { return request({ url: '/transactionLog/queryTransactionLogMessage', method: 'get', params }) }

// ===== 29. 初始化小区管理 =====
export function listCommunitys(params) { return request({ url: '/community.listCommunitys', method: 'get', params }) }
export function deleteBuildingUnit(data) { return request({ url: '/initializeBuildingUnit.deleteBuildingUnit', method: 'post', data }) }

// ===== 30. 配置菜单 =====
export function configMenu(data) { return request({ url: '/menu.configMenu', method: 'post', data }) }
export function listMenuGroupsAll(params) { return request({ url: '/menuGroup.listMenuGroups', method: 'get', params }) }
export function listMenusAll(params) { return request({ url: '/menu.listMenus', method: 'get', params }) }
export function listBasePrivilegesAll(params) { return request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }) }

// ===== 31. 开发者服务提供 =====
export function listAppsAll(params) { return request({ url: '/app.listApps', method: 'get', params }) }

// ===== 32. 服务绑定 =====
export function saveRoute(data) { return request({ url: '/route.saveRoute', method: 'post', data }) }

// ===== 33. 服务字典 =====
export function listCServiceDict(params) { return request({ url: '/dict.listDict', method: 'get', params: { ...params, tableName: 'c_service', columnName: 'url' } }) }

// ===== 34. 权限管理 =====
export function listPrivilegeGroups(params) { return request({ url: '/privilegeGroup.listPrivilegeGroups', method: 'get', params }) }
export function savePrivilegeGroup(data) { return request({ url: '/privilegeGroup.savePrivilegeGroup', method: 'post', data }) }
export function updatePrivilegeGroup(data) { return request({ url: '/privilegeGroup.updatePrivilegeGroup', method: 'post', data }) }
export function deletePrivilegeGroup(data) { return request({ url: '/privilegeGroup.deletePrivilegeGroup', method: 'post', data }) }
export function listPrivileges(params) { return request({ url: '/basePrivilege.listBasePrivileges', method: 'get', params }) }
export function savePrivilegeGroupPrivilege(data) { return request({ url: '/privilegeGroup.savePrivilegeGroupPrivilege', method: 'post', data }) }
