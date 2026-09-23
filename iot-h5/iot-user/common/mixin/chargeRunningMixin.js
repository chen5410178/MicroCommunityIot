/**
 * 充电中订单详情共用逻辑（四轮/两轮充电中页面）
 * 订单加载 / 已充时长秒级计时 / 数据轮询 / 停止充电 / 定时器统一清理 / 状态流转跳已完成页
 * 页面需声明：pageKind（car | bike），可选 pollInterval（默认 car 5000 / bike 30000）
 */
import {
	queryPhoneChargeOrder,
	queryCarChargeProcess
} from '@/packageCharge/api/chargeApi.js'
import {
	adaptOrderList
} from '@/api/charge/orderAdapter.js'
import {
	stopCharge,
	stopCarCharge
} from '@/api/machine/machineApi.js'
import {
	getCommunityId
} from '@/api/community/communityApi.js'
import {
	getUserInfo
} from '@/lib/java110/utils/StorageUtil.js'
import {
	hasLogin
} from '@/api/user/sessionApi.js'
import chargeStore from '@/api/charge/chargeStore.js'

function pad(n) {
	return ('0' + n).slice(-2)
}

// 接口时间 → 时间戳（兼容 时间戳 / yyyy-MM-dd HH:mm:ss）
function toTime(t) {
	if (!t) return 0
	if (typeof t === 'number') {
		return t < 1e12 ? t * 1000 : t
	}
	const s = String(t)
	if (/^\d+$/.test(s)) {
		const n = parseInt(s)
		return s.length <= 10 ? n * 1000 : n
	}
	const d = new Date(s.replace(/-/g, '/'))
	return isNaN(d.getTime()) ? 0 : d.getTime()
}

let MIXIN = {
	data() {
		return {
			orderId: '',
			chargeType: '',
			order: null,
			orderLoaded: false,
			timeText: '00:00',
			powerText: '0.00 kW',
			kwhText: '0.00 度',
			fee: 0,
			note: '正在连接设备 · 请稍候…',
			socText: '—',
			leftMinText: '—',
			stopShow: false,
			stopping: false,
			stopped: false,
			timer: null,
			pollTimer: null,
			pollTick: 0,
			alive: false,
			busy: false,
			chargeMins: 0,
			chargeBaseTs: 0,
			toastMsg: ''
		}
	},
	computed: {
		themeDark() {
			return chargeStore.themeDark
		},
		isCar() {
			return this.pageKind === 'car'
		},
		isMine() {
			return this._isMyOrder(this.order)
		},
		showStopBtn() {
			const o = this.order
			return !!(o && o.state === '1001' && this.isMine && !this.stopped)
		},
		canStop() {
			return this.showStopBtn && !this.stopping
		},
		// 两轮无可视实时金额：充电中不展示预扣款、不造数
		feeLocked() {
			return !this.isCar && !!this.order && this.order.state === '1001'
		},
		feeText() {
			return this.feeLocked ? '结算后更新' : '¥' + (this.fee || 0).toFixed(2)
		},
		payWayText() {
			const o = this.order
			if (!o) return ''
			return o.way === '月卡' ? '月卡抵扣' : '余额支付'
		},
		chargeInfoText() {
			return this.isCar ? '电动车 · 7kW 交流慢充' : '电瓶车 · 定时/自动充满'
		}
	},
	onLoad(options) {
		this._initPage(options)
	},
	onShow() {
		const o = this.order
		if (o && o.state === '1001' && !this.stopped) this._resume()
	},
	onHide() {
		this._clearTimers()
	},
	onUnload() {
		this._clearTimers()
	},
	onBackPress() {
		this._clearTimers()
		return false
	},
	onPullDownRefresh() {
		this._syncOrder(() => uni.stopPullDownRefresh())
	},
	methods: {
		// 路由参数 + 订单加载
		_initPage(options) {
			const opt = options || {}
			this.chargeType = opt.chargeType || (this.pageKind === 'car' ? '1002' : '1001')
			this.orderId = opt.orderId || ''
			this.stopped = opt.stopped === '1' || opt.stopped === 1
			if (!this.orderId) {
				this.orderLoaded = true
				return
			}
			if (!hasLogin()) {
				this.orderLoaded = true
				this._toast('请先登录')
				return
			}
			this._loadOrder()
		},
		// 首次加载订单：非充电中直接流转到已完成页
		_loadOrder() {
			queryPhoneChargeOrder({
				orderId: this.orderId,
				page: 1,
				row: 10
			}).then(res => {
				const list = adaptOrderList(res.data)
				this.order = list.length ? list[0] : null
				this.orderLoaded = true
				this._initProcess()
				if (!this.order || this.order.state !== '1001') {
					this._goDone()
					return
				}
				this._resume()
			}).catch(() => {
				this.order = null
				this.orderLoaded = true
			})
		},
		// 订单在充：重置指标 + 启动计时器与轮询
		_resume() {
			if (!this.order || this.order.state !== '1001') return
			const o = this.order
			o.communityId = o.communityId || getCommunityId()
			this.stopped = false
			this.stopping = false
			this._startTimer()
			this._startPolling()
		},
		// 进入在充视图：指标以设备上报为准，先置 0 等轮询回填
		_initProcess() {
			this.fee = 0
			this.socText = '—'
			this.leftMinText = '—'
			this.chargeMins = 0
			this.chargeBaseTs = 0
			if (this.isCar) {
				this.powerText = '0.00 kW'
				this.kwhText = '0.00 度'
				this.note = '按电量计费 · 金额耗尽自动断电'
			} else {
				const o = this.order
				this.powerText = '0.00 kW'
				this.kwhText = ((o && o.kwh) || 0).toFixed(2) + ' 度'
				this.note = '充满自停 / 定时断电 · 电瓶车不支持实时功率上报'
			}
			this._refreshOrderTime()
		},
		_startTimer() {
			this._stopTimer()
			this.timer = setInterval(() => this._refreshOrderTime(), 1000)
		},
		_stopTimer() {
			if (this.timer) clearInterval(this.timer)
			this.timer = null
		},
		// 数据轮询：四轮 5s 进度，两轮 30s 订单
		_startPolling() {
			this._stopPolling()
			this.alive = true
			this.pollTick = 0
			const interval = this.pollInterval || (this.isCar ? 5000 : 30000)
			this._pollOnce()
			this.pollTimer = setInterval(() => this._pollOnce(), interval)
		},
		_stopPolling() {
			this.alive = false
			this.busy = false
			if (this.pollTimer) clearInterval(this.pollTimer)
			this.pollTimer = null
		},
		// 定时器统一清理：秒级计时器 + 数据轮询
		_clearTimers() {
			this._stopTimer()
			this._stopPolling()
		},
		_pollOnce() {
			if (!this.alive || this.busy || !this.order || this.order.state !== '1001') return
			this.busy = true
			this.pollTick++
			if (this.isCar) {
				this._pollCarProcess()
				return
			}
			this._pollBikeOrder()
		},
		// 四轮：设备实时上报（功率/电量/费用/电压电流枪温/SOC/剩余）
		_pollCarProcess() {
			const o = this.order
			queryCarChargeProcess({
				orderId: o.orderId,
				communityId: o.communityId || getCommunityId()
			}).then(res => {
				this.busy = false
				if (!this.alive) return
				const d = (res && res.code == 0 && res.data) ? res.data : null
				if (!d) {
					this._setProcessZero()
					this._checkOrderState()
					return
				}
				this.fee = parseFloat(d.chargeMoney || 0) || 0
				this.kwhText = (parseFloat(d.charged || 0) || 0).toFixed(2) + ' 度'
				this.powerText = (parseFloat(d.energy || 0) || 0).toFixed(2) + ' kW'
				this.socText = (parseInt(d.process || 0) || 0) + '%'
				this.leftMinText = (parseInt(d.min || 0) || 0) + ' 分钟'
				this.note = '电压 ' + (d.voltage || 0) + 'V · 电流 ' + (d.electric || 0) + 'A · 枪温 ' +
					(d.temperature || 0) + '°C · 金额耗尽自动断电'
				if (parseInt(d.chargedMin || 0) > 0) {
					this.chargeMins = parseInt(d.chargedMin) || 0
					this.chargeBaseTs = Date.now()
				}
				this._checkOrderState()
			}).catch(() => {
				this.busy = false
				if (!this.alive) return
				this._setProcessZero()
				this._checkOrderState()
			})
		},
		// 两轮：只取订单真实字段（无实时上报来源，不造数）
		_pollBikeOrder() {
			queryPhoneChargeOrder({
				orderId: this.orderId,
				page: 1,
				row: 10
			}).then(res => {
				this.busy = false
				if (!this.alive) return
				const list = adaptOrderList(res.data)
				const o = list.length ? list[0] : null
				if (!o) return
				o.startTime = o.startTime || (this.order ? this.order.startTime : '')
				this.order = o
				if (o.state !== '1001') {
					this._stopPolling()
					this._goDone()
					return
				}
				this.kwhText = (parseFloat(o.kwh || 0) || 0).toFixed(2) + ' 度'
				this.fee = 0
				this._refreshOrderTime()
			}).catch(() => {
				this.busy = false
			})
		},
		// 无有效上报数据：功率/电量/费用统一置 0（不兜底订单字段）
		_setProcessZero() {
			this.powerText = '0.00 kW'
			this.kwhText = '0.00 度'
			this.fee = 0
			this.socText = '—'
			this.leftMinText = '—'
			this.note = '正在获取设备实时数据 · 金额耗尽自动断电'
		},
		// 四轮：每 3 次轮询（约 15s）同步一次订单状态，保证状态流转与停止后的结算感知
		_checkOrderState() {
			if (this.pollTick % 3 === 0) this._syncOrder()
		},
		// 同步订单状态：已结束则停轮询并流转已完成页
		_syncOrder(done) {
			if (!this.alive && !done) return
			if (!this.orderId) {
				if (typeof done === 'function') done()
				return
			}
			queryPhoneChargeOrder({
				orderId: this.orderId,
				page: 1,
				row: 10
			}).then(res => {
				const list = adaptOrderList(res.data)
				const o = list.length ? list[0] : null
				if (!o) return
				o.startTime = o.startTime || (this.order ? this.order.startTime : '')
				this.order = o
				if (o.state !== '1001') {
					this._stopPolling()
					this._goDone()
				}
			}).catch(() => {}).then(() => {
				if (typeof done === 'function') done()
			})
		},
		// 已充时长：优先设备累计充电时间平滑递增，兜底订单 startTime
		_refreshOrderTime() {
			const o = this.order
			if (!o) return
			let sec = 0
			if (this.chargeMins > 0 && this.chargeBaseTs > 0) {
				sec = this.chargeMins * 60 + Math.floor((Date.now() - this.chargeBaseTs) / 1000)
			} else if (o.startTime) {
				const t = toTime(o.startTime)
				sec = t ? Math.max(0, Math.floor((Date.now() - t) / 1000)) : 0
			}
			this.timeText = pad(Math.floor(sec / 60)) + ':' + pad(sec % 60)
		},
		// 订单归属校验：personId 优先，personTel 兜底
		_isMyOrder(o) {
			if (!o) return false
			const raw = getUserInfo()
			if (!raw) return false
			let me = raw
			if (typeof raw === 'string') {
				try {
					me = JSON.parse(raw)
				} catch (e) {
					return false
				}
			}
			if (!me) return false
			if (o.personId && me.userId && o.personId === me.userId) return true
			return !!(o.personTel && me.tel && o.personTel === me.tel)
		},
		askStop() {
			if (!this.canStop) return
			this.stopShow = true
		},
		cancelStop() {
			this.stopShow = false
		},
		// 停止充电：成功即停轮询并流转已完成页（结算中）
		confirmStop() {
			this.stopShow = false
			const o = this.order
			if (!o || o.state !== '1001' || !this.isMine || this.stopping) return
			this.stopping = true
			const params = {
				orderId: o.orderId,
				machineId: o.machineId,
				portId: o.portId,
				communityId: o.communityId || getCommunityId()
			}
			const req = this.isCar ? stopCarCharge : stopCharge
			req(params).then(res => {
				this.stopping = false
				if (!res || res.code != 0) {
					this._toast((res && res.msg) || '停止失败')
					return
				}
				this._goDone('&stopped=1')
			}).catch(() => {
				this.stopping = false
				this._toast('停止失败，请稍后重试')
			})
		},
		// 状态流转 / 停止成功：单页替换进入已完成页，不叠加页面栈
		_goDone(extra) {
			this._clearTimers()
			let u = '/packageCharge/pages/charge/orderDone?orderId=' + this.orderId +
				'&chargeType=' + this.chargeType
			if (extra) u += extra
			uni.redirectTo({
				url: u
			})
		},
		_toast(msg) {
			this.toastMsg = msg
			clearTimeout(this._toastTimer)
			this._toastTimer = setTimeout(() => {
				this.toastMsg = ''
			}, 2000)
		}
	}
}
export default MIXIN
