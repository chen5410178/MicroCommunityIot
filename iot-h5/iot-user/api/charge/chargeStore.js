/**
 * 充电业务共享状态（页面间共享：在充订单/余额/月卡/演示数据）
 * 使用模块级单例 + uni.storage 持久化，保证首页/订单页/充电中页联动
 * // TODO 后端接口就绪后替换为接口数据
 */
import {
	mockCommunities,
	mockPlans,
	mockVehicles,
	mockRentOrders,
	mockCardOrders,
	mockRecords,
	mockWalletLogs,
	mockMessages
} from '../mock/chargeMockData.js'

const RUNNING_KEY = 'charge_running_order'
const BALANCE_KEY = 'charge_balance'
const CARD_KEY = 'charge_my_card'
const VEHICLES_KEY = 'charge_vehicles'
const RENT_ORDERS_KEY = 'charge_rent_orders'
const CARD_ORDERS_KEY = 'charge_card_orders'
const RECORDS_KEY = 'charge_records'
const WALLET_LOGS_KEY = 'charge_wallet_logs'
const MESSAGES_KEY = 'charge_messages'
const REC_RANGE_KEY = 'charge_rec_range'
const REC_TYPE_KEY = 'charge_rec_type'
const THEME_KEY = 'charge_theme_dark'
const THEME_SCHED_KEY = 'charge_theme_sched'

function load(key, def) {
	try {
		const v = uni.getStorageSync(key)
		return v === '' || v === undefined || v === null ? def : v
	} catch (e) {
		return def
	}
}

function save(key, val) {
	try {
		uni.setStorageSync(key, val)
	} catch (e) {}
}

export default {
	// 小区/桩（演示数据，切换小区后持久化当前索引）
	communities: mockCommunities,
	commIdx: load('charge_comm_idx', 0),
	chgType: load('charge_chg_type', 'bike'),

	// 在充订单
	runningOrder: load(RUNNING_KEY, null),
	chgStart: load('charge_chg_start', 0),
	chgFee: 0,
	chgKwh: 0,
	chgHeld: 0,
	payWay: 'balance',
	curPile: null,
	curSlot: null,
	curGun: null,
	occupiedSlot: null,
	occupiedGun: null,
	chargeTimer: null,

	// 余额
	balance: load(BALANCE_KEY, 26.5),

	// 月卡
	myCard: load(CARD_KEY, null),
	planFilter: null,

	// 车辆
	vehicles: load(VEHICLES_KEY, mockVehicles),
	rentOrders: load(RENT_ORDERS_KEY, mockRentOrders),
	curCarIdx: 0,
	curTempCar: null,

	// 订单/流水/消息
	cardOrders: load(CARD_ORDERS_KEY, mockCardOrders),
	records: load(RECORDS_KEY, mockRecords),
	walletLogs: load(WALLET_LOGS_KEY, mockWalletLogs),
	messages: load(MESSAGES_KEY, mockMessages),

	// 订单筛选
	recRange: load(REC_RANGE_KEY, 'all'),
	recType: load(REC_TYPE_KEY, 'all'),

	// 主题
	themeDark: load(THEME_KEY, false),
	themeSched: load(THEME_SCHED_KEY, { on: false, start: '22:00', end: '07:00' }),

	setRunningOrder(order) {
		this.runningOrder = order
		save(RUNNING_KEY, order)
	},
	setBalance(v) {
		this.balance = Math.round(v * 100) / 100
		save(BALANCE_KEY, this.balance)
	},
	setMyCard(card) {
		this.myCard = card
		save(CARD_KEY, card)
	},
	setCommIdx(i) {
		this.commIdx = i
		uni.setStorageSync('charge_comm_idx', i)
	},
	setChgType(t) {
		this.chgType = t
		uni.setStorageSync('charge_chg_type', t)
	},
	saveRecords() {
		save(RECORDS_KEY, this.records)
	},
	saveVehicles() {
		save(VEHICLES_KEY, this.vehicles)
	},
	saveRentOrders() {
		save(RENT_ORDERS_KEY, this.rentOrders)
	},
	saveCardOrders() {
		save(CARD_ORDERS_KEY, this.cardOrders)
	},
	saveWalletLogs() {
		save(WALLET_LOGS_KEY, this.walletLogs)
	},
	saveMessages() {
		save(MESSAGES_KEY, this.messages)
	},
	saveTheme() {
		save(THEME_KEY, this.themeDark)
		save(THEME_SCHED_KEY, this.themeSched)
	},

	// 当前小区
	curCommunity() {
		return this.communities[this.commIdx] || this.communities[0]
	},

	// 当前类型桩列表
	curPiles() {
		const c = this.curCommunity()
		return this.chgType === 'car' ? (c.carPiles || []) : c.piles
	},

	// 按类型取桩列表
	pilesByType(type) {
		const c = this.curCommunity()
		return type === 'car' ? (c.carPiles || []) : c.piles
	},

	// 根据充电中订单找到对应桩
	findPileByRunning() {
		const r = this.runningOrder
		if (!r) return null
		const list = this.pilesByType(r.type)
		return list.find(p => p.name === r.pile) || null
	},

	// 充电实时数据（演示加速：每 6 秒 = 1 分钟）
	simulateTick() {
		if (!this.runningOrder) return
		const r = this.runningOrder
		const sec = Math.max(0, Math.floor((Date.now() - this.chgStart) / 1000))
		r.sec = sec
		if (sec <= 0) return
		const mins = sec / 6
		const cfg = r.type === 'car' ? {
			kwhPerH: 7,
			elecPrice: 0,
			servPrice: 0
		} : {
			kwhPerH: 0.22,
			elecPrice: 0.60,
			servPrice: 0.10
		}
		if (r.type === 'car') {
			this.chgKwh = Math.round(mins / 60 * 7 * 100) / 100
			this.chgFee = Math.round(this.chgKwh * (r.tariffRate || 1.5) * 100) / 100
		} else {
			this.chgKwh = Math.round(mins / 60 * cfg.kwhPerH * 100) / 100
			this.chgFee = Math.round((this.chgKwh * cfg.elecPrice + mins / 60 * cfg.servPrice) * 100) / 100
		}
		r.mins = Math.max(1, Math.ceil(mins))
		r.kwh = this.chgKwh
		r.fee = this.chgFee
	},

	// 结算充电（停止/自动断电）：返回结算数据
	settleCharge() {
		const r = this.runningOrder
		if (!r) return null
		const sec = Math.max(1, Math.floor((Date.now() - this.chgStart) / 1000))
		const mins = Math.max(1, Math.ceil(sec / 6))
		const cfg = r.type === 'car' ? {
			kwhPerH: 7,
			elecPrice: 0,
			servPrice: 0
		} : {
			kwhPerH: 0.22,
			elecPrice: 0.60,
			servPrice: 0.10
		}
		let fee = 0
		let refund = 0
		if (r.way === 'balance') {
			if (r.type === 'car') {
				const rate = r.tariffRate || 1.5
				fee = Math.min(Math.round(this.chgKwh * rate * 100) / 100, r.amt || this.chgHeld)
				refund = Math.max(0, this.chgHeld - fee)
			} else {
				const kwh = Math.round(mins / 60 * cfg.kwhPerH * 100) / 100
				fee = Math.round((kwh * cfg.elecPrice + mins / 60 * cfg.servPrice) * 100) / 100
				fee = Math.min(fee, this.chgHeld)
				refund = Math.max(0, this.chgHeld - fee)
			}
			this.setBalance(this.balance + this.chgHeld - fee)
		} else if (this.myCard) {
			if (this.myCard.type === 'times') {
				this.myCard.left--
				this.setMyCard(Object.assign({}, this.myCard))
			}
			fee = 0
		}
		const settle = {
			pile: r.pile,
			slot: r.slot,
			mins: mins,
			way: r.way === 'balance' ? '余额' : (this.myCard && this.myCard.type === 'month' ? '畅充月卡（免费）' : '次卡抵扣（剩余 ' + (this.myCard ? this.myCard.left : 0) + ' 次）'),
			fee: fee,
			refund: refund,
			kwh: this.chgKwh,
			type: r.type,
			held: this.chgHeld,
			deposit: this.chgHeld
		}
		// 释放插座/枪
		this.releaseSlotGun()
		// 生成订单记录
		const d = new Date()
		const hh = ('0' + d.getHours()).slice(-2)
		const mm = ('0' + d.getMinutes()).slice(-2)
		this.records.unshift({
			pile: r.pile,
			date: '今天 ' + hh + ':' + mm,
			mins: mins,
			fee: fee,
			way: r.way === 'balance' ? '余额' : (this.myCard && this.myCard.type === 'month' ? '畅充月卡' : '次卡抵扣'),
			st: '已完成',
			held: this.chgHeld || 0,
			type: r.type,
			kwh: this.chgKwh
		})
		this.saveRecords()
		// 钱包流水（余额支出）
		if (fee > 0) {
			this.walletLogs.unshift({
				name: '扫码充电 · ' + r.pile,
				date: '今天 ' + hh + ':' + mm,
				amt: fee,
				in: false,
				days: 0,
				recIdx: 0
			})
			this.saveWalletLogs()
		}
		this.runningOrder = null
		this.setRunningOrder(null)
		return settle
	},

	// 释放充电占用的插座/枪
	releaseSlotGun() {
		const r = this.runningOrder
		if (!r) return
		const list = this.pilesByType(r.type)
		const pile = list.find(p => p.name === r.pile)
		if (!pile) return
		if (r.type === 'bike' && this.occupiedSlot !== null && pile.slots && pile.slots[this.occupiedSlot]) {
			pile.slots[this.occupiedSlot].busy = false
			pile.used = Math.max(0, (pile.used || 1) - 1)
			if (!pile.slots.some(s => s.busy)) pile.free = true
			this.occupiedSlot = null
		}
		if (r.type === 'car' && this.occupiedGun !== null && pile.guns && pile.guns[this.occupiedGun]) {
			pile.guns[this.occupiedGun].busy = false
			pile.used = Math.max(0, (pile.used || 1) - 1)
			if (!pile.guns.some(g => g.busy)) pile.free = true
			this.occupiedGun = null
		}
	},

	// 充电占用插座/枪
	occupySlotGun(running) {
		const list = this.pilesByType(running.type)
		const pile = list.find(p => p.name === running.pile)
		if (!pile) return
		if (running.type === 'bike' && running.slot) {
			const idx = (pile.slots || []).findIndex(s => s.n === running.slot)
			if (idx >= 0 && !pile.slots[idx].busy) {
				pile.slots[idx].busy = true
				pile.used = (pile.used || 0) + 1
				if (pile.slots.every(s => s.busy)) pile.free = false
				this.occupiedSlot = idx
			}
		}
		if (running.type === 'car' && running.gun) {
			const idx = (pile.guns || []).findIndex(g => g.n === running.gun)
			if (idx >= 0 && !pile.guns[idx].busy) {
				pile.guns[idx].busy = true
				pile.used = (pile.used || 0) + 1
				if (pile.guns.every(g => g.busy)) pile.free = false
				this.occupiedGun = idx
			}
		}
	},

	// 推送消息（顶部横幅联动消息中心）
	addMsg(type, title, text) {
		const m = {
			id: Date.now(),
			type: type || 'charge',
			title: title,
			text: text,
			time: '刚刚',
			read: false
		}
		this.messages.unshift(m)
		this.saveMessages()
		return m.id
	}
}
