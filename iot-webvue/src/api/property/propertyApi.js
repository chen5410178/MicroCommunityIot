import request from '@/utils/request'

// ===== 房屋管理 =====
export function queryRooms(params) { return request({ url: '/room.queryRooms', method: 'get', params }) }
export function saveRoom(data) { return request({ url: '/room.saveRoom', method: 'post', data }) }
export function updateRoom(data) { return request({ url: '/room.updateRoom', method: 'post', data }) }
export function deleteRoom(data) { return request({ url: '/room.deleteRoom', method: 'post', data }) }
export function queryRoomsTree(params) { return request({ url: '/room.queryRoomsTree', method: 'get', params }) }

// ===== 楼栋管理 =====
export function queryFloors(params) { return request({ url: '/floor.queryFloors', method: 'get', params }) }
export function queryFloorAndUnits(params) { return request({ url: '/floor.queryFloorAndUnits', method: 'get', params }) }
export function saveFloor(data) { return request({ url: '/floor.saveFloor', method: 'post', data }) }
export function editFloor(data) { return request({ url: '/floor.editFloor', method: 'post', data }) }
export function deleteFloorItem(data) { return request({ url: '/floor.deleteFloor', method: 'post', data }) }

// ===== 单元管理 =====
export function queryUnits(params) { return request({ url: '/unit.queryUnits', method: 'get', params }) }
export function saveUnit(data) { return request({ url: '/unit.saveUnit', method: 'post', data }) }
export function updateUnit(data) { return request({ url: '/unit.updateUnit', method: 'post', data }) }
export function deleteUnitItem(data) { return request({ url: '/unit.deleteUnit', method: 'post', data }) }

// ===== 业主管理 =====
export function queryOwners(params) { return request({ url: '/owner.queryOwners', method: 'get', params }) }
export function saveOwnerRoomRel(data) { return request({ url: '/owner.saveOwnerRoomRel', method: 'post', data }) }
export function deleteOwnerRoomRel(data) { return request({ url: '/owner.deleteOwnerRoomRel', method: 'post', data }) }

// ===== 房产导入 =====
export function importOwnerRoom(data) { return request({ url: '/room.importOwnerRoom', method: 'post', data }) }

// ===== 字典/属性规格 =====
export function listDicts(params) { return request({ url: '/dict.listDict', method: 'get', params }) }
export function listAttrSpecs(params) { return request({ url: '/attrSpec.listAttrSpec', method: 'get', params }) }
export function listAttrValues(params) { return request({ url: '/attrValue.listAttrValue', method: 'get', params }) }

// ===== 业主车辆 =====
export function listOwnerCars(params) { return request({ url: '/car.queryCar', method: 'get', params }) }
export function listOwnerMembers(params) { return request({ url: '/owner.listOwnerMember', method: 'get', params }) }

// ===== 门禁管理 =====
export function listAccessControlFloor(params) { return request({ url: '/accessControlFloor.listAccessControlFloor', method: 'get', params }) }
export function listAccessControlLog(params) { return request({ url: '/accessControlLog.listAccessControlLog', method: 'get', params }) }
export function listAccessControlInout(params) { return request({ url: '/accessControlInout.listAccessControlInout', method: 'get', params }) }

// ===== 水电表管理 =====
export function listMeterMachine(params) { return request({ url: '/meterMachine.listMeterMachine', method: 'get', params }) }
export function listMeterMachineReadOrRechargeDetail(params) { return request({ url: '/meterMachine.listMeterMachineReadOrRechargeDetail', method: 'get', params }) }

// ===== 门锁管理 =====
export function listLockMachine(params) { return request({ url: '/lockMachine.listLockMachine', method: 'get', params }) }
export function listLockPerson(params) { return request({ url: '/lockPerson.listLockPerson', method: 'get', params }) }
export function listLockLog(params) { return request({ url: '/lockLog.listLockLog', method: 'get', params }) }
export function listLockInout(params) { return request({ url: '/lockInout.listLockInout', method: 'get', params }) }
