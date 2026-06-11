import request from '@/utils/request'

// ===== 路灯厂家 =====
export function listLampMachineFactory(params) { return request({ url: '/lampMachineFactory.listLampMachineFactory', method: 'get', params }) }
export function saveLampMachineFactory(data) { return request({ url: '/lampMachineFactory.saveLampMachineFactory', method: 'post', data }) }
export function updateLampMachineFactory(data) { return request({ url: '/lampMachineFactory.updateLampMachineFactory', method: 'post', data }) }
export function deleteLampMachineFactory(data) { return request({ url: '/lampMachineFactory.deleteLampMachineFactory', method: 'post', data }) }

// ===== 路灯厂家规格 =====
export function listLampMachineFactorySpec(params) { return request({ url: '/lampMachineFactorySpec.listLampMachineFactorySpec', method: 'get', params }) }
export function saveLampMachineFactorySpec(data) { return request({ url: '/lampMachineFactorySpec.saveLampMachineFactorySpec', method: 'post', data }) }
export function deleteLampMachineFactorySpec(data) { return request({ url: '/lampMachineFactorySpec.deleteLampMachineFactorySpec', method: 'post', data }) }

// 话机设备
export function listTelMachine(params) { return request({ url: '/telMachine.listTelMachine', method: 'get', params }) }

// 话机客服
export function listTelMachineUser(params) { return request({ url: '/telMachineUser.listTelMachineUser', method: 'get', params }) }
export function saveTelMachineUser(data) { return request({ url: '/telMachineUser.saveTelMachineUser', method: 'post', data }) }
export function deleteTelMachineUser(data) { return request({ url: '/telMachineUser.deleteTelMachineUser', method: 'post', data }) }

// 员工查询
export function listStaffs(params) { return request({ url: '/query.staff.infos', method: 'get', params }) }

export function listTelMachines(params) { return request({ url: '/telMachine.listTelMachine', method: 'get', params }) }
export function saveTelMachine(data) { return request({ url: '/telMachine.saveTelMachine', method: 'post', data }) }
export function updateTelMachine(data) { return request({ url: '/telMachine.updateTelMachine', method: 'post', data }) }
export function deleteTelMachine(data) { return request({ url: '/telMachine.deleteTelMachine', method: 'post', data }) }

// ===== 路灯设备 CRUD =====
export function listLampMachine(params) { return request({ url: '/lampMachine.listLampMachine', method: 'get', params }) }
export function saveLampMachine(data) { return request({ url: '/lampMachine.saveLampMachine', method: 'post', data }) }
export function updateLampMachine(data) { return request({ url: '/lampMachine.updateLampMachine', method: 'post', data }) }
export function deleteLampMachine(data) { return request({ url: '/lampMachine.deleteLampMachine', method: 'post', data }) }
export function switchLampState(data) { return request({ url: '/lampMachine.switchLampState', method: 'post', data }) }

// ===== 开关灯时间 =====
export function listLampMachineOpenClose(params) { return request({ url: '/lampMachineOpenClose.listLampMachineOpenClose', method: 'get', params }) }
export function saveLampMachineOpenClose(data) { return request({ url: '/lampMachineOpenClose.saveLampMachineOpenClose', method: 'post', data }) }

// ===== 操作日志 =====
export function listLampMachineOperation(params) { return request({ url: '/lampMachineOperation.listLampMachineOperation', method: 'get', params }) }

// ===== 下发日志 =====
export function listLampMachineLog(params) { return request({ url: '/lampMachineLog.listLampMachineLog', method: 'get', params }) }

// ===== 呼叫记录 =====
export function listTelCallLog(params) { return request({ url: '/telCallLog.listTelCallLog', method: 'get', params }) }

// ===== 短信记录 =====
export function listTelSmsLog(params) { return request({ url: '/telSmsLog.listTelSmsLog', method: 'get', params }) }

// ===== 话机语音 =====
export function listTelTts(params) { return request({ url: '/telTts.listTelTts', method: 'get', params }) }
export function saveTelTts(data) { return request({ url: '/telTts.saveTelTts', method: 'post', data }) }
export function updateTelTts(data) { return request({ url: '/telTts.updateTelTts', method: 'post', data }) }
export function deleteTelTts(data) { return request({ url: '/telTts.deleteTelTts', method: 'post', data }) }

// ===== 操作人员 =====
export function listCommunityStaffs(params) { return request({ url: '/user.listCommunityStaffsByCommunityId', method: 'get', params }) }
