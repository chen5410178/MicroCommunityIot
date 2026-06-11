import request from '@/utils/request'

export function listMonitorModels(params) { return request({ url: '/monitorModel.listMonitorModel', method: 'get', params }) }
export function saveMonitorModel(data) { return request({ url: '/monitorModel.saveMonitorModel', method: 'post', data }) }
export function updateMonitorModel(data) { return request({ url: '/monitorModel.updateMonitorModel', method: 'post', data }) }
export function deleteMonitorModel(data) { return request({ url: '/monitorModel.deleteMonitorModel', method: 'post', data }) }

export function listMonitorMachineModels(params) { return request({ url: '/monitorMachineModel.listMonitorMachineModel', method: 'get', params }) }
export function saveMonitorMachineModel(data) { return request({ url: '/monitorMachineModel.saveMonitorMachineModel', method: 'post', data }) }
export function deleteMonitorMachineModel(data) { return request({ url: '/monitorMachineModel.deleteMonitorMachineModel', method: 'post', data }) }

export function listMonitorAreas(params) { return request({ url: '/monitorArea.listMonitorArea', method: 'get', params }) }
export function saveMonitorArea(data) { return request({ url: '/monitorArea.saveMonitorArea', method: 'post', data }) }
export function updateMonitorArea(data) { return request({ url: '/monitorArea.updateMonitorArea', method: 'post', data }) }
export function deleteMonitorArea(data) { return request({ url: '/monitorArea.deleteMonitorArea', method: 'post', data }) }

export function listMonitorMachines(params) { return request({ url: '/monitorMachine.listMonitorMachine', method: 'get', params }) }
export function saveMonitorMachine(data) { return request({ url: '/monitorMachine.saveMonitorMachine', method: 'post', data }) }
export function updateMonitorMachine(data) { return request({ url: '/monitorMachine.updateMonitorMachine', method: 'post', data }) }
export function deleteMonitorMachine(data) { return request({ url: '/monitorMachine.deleteMonitorMachine', method: 'post', data }) }
export function getPlayVideoUrl(params) { return request({ url: '/monitorMachine.getPlayVideoUrl', method: 'get', params }) }
export function saveVideoPhoto(data) { return request({ url: '/monitorMachine.saveVideoPhoto', method: 'post', data }) }

export function listMonitorManufactors(params) { return request({ url: '/monitorManufactor.listMonitorManufactor', method: 'get', params }) }
export function saveMonitorManufactor(data) { return request({ url: '/monitorManufactor.saveMonitorManufactor', method: 'post', data }) }
export function updateMonitorManufactor(data) { return request({ url: '/monitorManufactor.updateMonitorManufactor', method: 'post', data }) }
export function deleteMonitorManufactor(data) { return request({ url: '/monitorManufactor.deleteMonitorManufactor', method: 'post', data }) }

export function listMonitorPools(params) { return request({ url: '/monitorPool.listMonitorPool', method: 'get', params }) }

export function listMonitorRecords(params) { return request({ url: '/monitorMachine.listMonitorRecord', method: 'get', params }) }
export function getRecordPlayVideoUrl(params) { return request({ url: '/monitorMachine.getRecordPlayVideoUrl', method: 'get', params }) }

export function listMonitorMachinePersons(params) { return request({ url: '/monitor.listMonitorMachinePerson', method: 'get', params }) }
export function saveMonitorMachinePerson(data) { return request({ url: '/monitor.saveMonitorMachinePerson', method: 'post', data }) }
export function deleteMonitorMachinePerson(data) { return request({ url: '/monitor.deleteMonitorMachinePerson', method: 'post', data }) }

export function listMonitorCollects(params) { return request({ url: '/monitor.listMonitorCollect', method: 'get', params }) }
export function saveMonitorCollect(data) { return request({ url: '/monitor.saveMonitorCollect', method: 'post', data }) }
export function deleteMonitorCollect(data) { return request({ url: '/monitor.deleteMonitorCollect', method: 'post', data }) }
