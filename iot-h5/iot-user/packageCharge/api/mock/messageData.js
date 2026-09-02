/**
 * 消息中心 mock 数据（本地 mock + uni.storage 保存已读/删除状态）
 * // TODO 后端接口就绪后替换
 */
import conf from '@/conf/config.js'
import {
	mockMessages
} from '@/api/mock/chargeMockData.js'

// 消息中心初始数据（与 chargeMockData.mockMessages 同源，便于后续替换）
export const initMessages = mockMessages.slice()

// 消息类型图标元信息（icon 为 PNG 图标路径，与原型 SVG 对应）
export const msgMeta = {
	charge: {
		bg: 'rgba(16,185,129,.12)',
		icon: conf.commonBaseUrl + '/h5/images/charge/ic-bolt.png'
	},
	wallet: {
		bg: 'rgba(59,130,246,.12)',
		icon: conf.commonBaseUrl + '/h5/images/charge/ic-wallet.png'
	},
	card: {
		bg: 'rgba(249,115,22,.12)',
		icon: conf.commonBaseUrl + '/h5/images/charge/ic-card.png'
	},
	sys: {
		bg: 'rgba(139,92,246,.12)',
		icon: conf.commonBaseUrl + '/h5/images/common/ic-bell-violet.png'
	}
}

// 消息已读/删除状态 storage key
export const MSG_STATE_KEY = 'charge_msg_state'

/**
 * 读取消息状态（已读/删除 id 集合）
 */
export function loadMsgState() {
	try {
		return uni.getStorageSync(MSG_STATE_KEY) || { read: [], deleted: [] }
	} catch (e) {
		return { read: [], deleted: [] }
	}
}

/**
 * 保存消息状态
 */
export function saveMsgState(state) {
	uni.setStorageSync(MSG_STATE_KEY, state)
}
