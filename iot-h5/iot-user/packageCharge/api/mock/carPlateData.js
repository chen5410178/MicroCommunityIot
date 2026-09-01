/**
 * 车牌省份 / 键盘按键常量（车辆服务 · 车牌搜索/编辑共用）
 * 本地写死数据，无后端接口
 * // TODO 后端接口就绪后替换
 */

// 车牌省份汉字（车牌第一位）
export const KB_PROVINCES = '京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新港澳台'.split('')

// 数字键盘
export const KB_NUMS = '1234567890'.split('')

// 字母键盘两行
export const KB_LET1 = 'ABCDEFGHIJKL'.split('')
export const KB_LET2 = 'MNOPQRSTUVWX'.split('')

// 特殊尾字
export const KB_TAILS = ['警', '学', '挂', '领', '使', '港', '澳']

// 历史车牌本地存储 key
export const PLATE_HISTORY_KEY = 'car_plate_history'

/**
 * 读取历史车牌
 */
export function getPlateHistory() {
	try {
		return JSON.parse(uni.getStorageSync(PLATE_HISTORY_KEY)) || []
	} catch (e) {
		return []
	}
}

/**
 * 保存历史车牌（最多 10 条）
 * @param {String} plate 车牌（含·或不含均可）
 */
export function savePlateHistory(plate) {
	let h = getPlateHistory().filter(p => p !== plate)
	h.unshift(plate)
	uni.setStorageSync(PLATE_HISTORY_KEY, JSON.stringify(h.slice(0, 10)))
}

/**
 * 删除单条历史车牌
 */
export function delPlateHistory(plate) {
	const h = getPlateHistory().filter(p => p !== plate)
	uni.setStorageSync(PLATE_HISTORY_KEY, JSON.stringify(h))
}

/**
 * 车牌格式校验：支持蓝牌 7 位、新能源绿牌 8 位、特殊尾字（警/学/挂等）
 */
export function isPlateValid(bare) {
	return /^[\u4e00-\u9fa5][A-Z]([DF][A-Z0-9]{5}|[A-Z0-9]{5}|[A-Z0-9]{5}[\u4e00-\u9fa5])$/.test(bare)
}

/**
 * 车牌类型判定：第 3 位 D/F 为新能源
 */
export function isNewEnergy(bare) {
	return /^[\u4e00-\u9fa5][A-Z][DF]/.test(bare)
}
