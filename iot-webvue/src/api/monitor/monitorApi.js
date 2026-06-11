import request from '@/utils/request'

export function listMonitorModels(params) { return request({ url: '/monitorModel.listMonitorModel', method: 'get', params }) }
export function saveMonitorModel(data) { return request({ url: '/monitorModel.saveMonitorModel', method: 'post', data }) }
export function updateMonitorModel(data) { return request({ url: '/monitorModel.updateMonitorModel', method: 'post', data }) }
export function deleteMonitorModel(data) { return request({ url: '/monitorModel.deleteMonitorModel', method: 'post', data }) }

export function listMonitorMachineModels(params) { return request({ url: '/monitorMachineModel.listMonitorMachineModel', method: 'get', params }) }
export function saveMonitorMachineModel(data) { return request({ url: '/monitorMachineModel.saveMonitorMachineModel', method: 'post', data }) }
export function deleteMonitorMachineModel(data) { return request({ url: '/monitorMachineModel.deleteMonitorMachineModel', method: 'post', data }) }

export function listMonitorAreas(params) { return request({ url: '/monitorArea.listMonitorArea', method: 'get', params }) }

export function listMonitorMachines(params) { return request({ url: '/monitorMachine.listMonitorMachine', method: 'get', params }) }
