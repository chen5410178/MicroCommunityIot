<template>
	<view class="od-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 在充订单 -->
		<template v-if="mode === 'running'">
			<!-- 本地充电流程在充订单（可停止充电） -->
			<template v-if="runningOrder">
				<view class="od-running-hero">
					<view class="od-head">
						<view class="od-title-row">
							<view class="c-pulse-dot" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
							<text class="od-title">充电中 · {{ runningOrder.pile }}</text>
							<text class="od-slot-tag" v-if="runningOrder.slot">{{ runningOrder.type === 'car' ? '枪 ' : '插座 ' }}{{ runningOrder.slot }}</text>
						</view>
						<text class="od-mode">{{ payModeText }}</text>
					</view>
					<view class="od-stats">
						<view class="od-stat">
							<text class="od-stat-label">已充时长</text>
							<text class="od-stat-val">{{ timeText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">当前功率</text>
							<text class="od-stat-val">{{ powerText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">已用电量</text>
							<text class="od-stat-val">{{ kwhText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">已充费用</text>
							<text class="od-stat-val">¥{{ fee.toFixed(2) }}</text>
						</view>
					</view>
					<view class="od-note">{{ note }}</view>
				</view>

				<view class="c-card od-mx od-mt">
					<text class="c-title">充电订单</text>
					<view class="od-info">
						<view class="od-info-row"><text class="c-text-sub">订单类型</text><text class="od-info-val">{{ runningOrder.type === 'car' ? '电动车 · 7kW 交流慢充' : '电瓶车 · 220W' }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">支付方式</text><text class="od-info-val">{{ runningOrder.type === 'car' ? '余额 · 预算 ¥' + (runningOrder.amt || 0).toFixed(2) : (runningOrder.way === 'balance' ? '余额' : '月卡') }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">当前费率</text><text class="od-info-val">{{ rateText }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">保护机制</text><text class="od-info-val">{{ protectText }}</text></view>
					</view>
				</view>

				<view class="od-mx od-mt" style="padding-bottom:80rpx;">
					<view class="c-btn-danger" @click="askStop">停止充电</view>
					<text class="od-stop-tip">停止后按实际充电量结算 · 剩余金额自动退还余额</text>
				</view>
			</template>

			<!-- 接口确认的在充订单（只读，不可停止） -->
			<template v-else-if="runningApi">
				<view class="od-running-hero">
					<view class="od-head">
						<view class="od-title-row">
							<view class="c-pulse-dot" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
							<text class="od-title">充电中 · {{ runningApi.pile }}</text>
							<text class="od-slot-tag" v-if="runningApi.slot">{{ runningApi.type === 'car' ? '枪 ' : '插座 ' }}{{ runningApi.slot }}</text>
						</view>
						<text class="od-mode">余额支付</text>
					</view>
					<view class="od-stats">
						<view class="od-stat">
							<text class="od-stat-label">已充时长</text>
							<text class="od-stat-val">{{ timeText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">当前功率</text>
							<text class="od-stat-val">{{ powerText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">已用电量</text>
							<text class="od-stat-val">{{ kwhText }}</text>
						</view>
						<view class="od-stat">
							<text class="od-stat-label">已充费用</text>
							<text class="od-stat-val">¥{{ fee.toFixed(2) }}</text>
						</view>
					</view>
					<view class="od-note">{{ note }}</view>
				</view>

				<view class="c-card od-mx od-mt">
					<text class="c-title">充电订单</text>
					<view class="od-info">
						<view class="od-info-row"><text class="c-text-sub">订单编号</text><text class="od-info-val">{{ runningApi.orderId }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">订单类型</text><text class="od-info-val">{{ runningApi.type === 'car' ? '电动车 · 7kW 交流慢充' : '电瓶车 · 220W' }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">支付方式</text><text class="od-info-val">余额</text></view>
						<view class="od-info-row"><text class="c-text-sub">充电开始时间</text><text class="od-info-val">{{ runningApi.startTime || '—' }}</text></view>
						<view class="od-info-row"><text class="c-text-sub">订单状态</text><text class="od-info-val">充电中</text></view>
						<view class="od-info-row"><text class="c-text-sub">已充费用</text><text class="od-info-val" :style="{ color: '#059669', fontWeight: 700 }">¥{{ runningApi.fee.toFixed(2) }}</text></view>
					</view>
				</view>

				<view class="od-mx od-mt" style="padding-bottom:80rpx;">
					<text class="od-readonly-tip">该订单为接口确认的充电中订单 · 如需停止请前往「充电中」页面操作</text>
				</view>
			</template>

			<view class="c-card co-empty od-mx od-mt" v-else>
				<image src="/static/h5/images/charge/ic-chart.png" class="co-empty-ic" mode="aspectFit"></image>
				<text class="c-text-sub" style="margin-top:16rpx;">未找到充电中的订单</text>
			</view>
		</template>

		<!-- 历史订单 -->
		<template v-else-if="order">
			<view class="c-card od-mx od-mt">
				<view class="od-done-head">
					<view class="od-done-ic">
						<image v-if="order.type === 'car'" src="/static/h5/images/charge/ic-car.png" class="od-done-ic-img" mode="aspectFit"></image>
						<image v-else src="/static/h5/images/charge/ic-bolt.png" class="od-done-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="od-done-name-row">
							<text class="c-title">{{ order.pile }}</text>
							<text class="c-badge c-badge-gray">{{ order.st }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ order.date }}</text>
					</view>
					<text class="od-done-fee" :class="order.fee > 0 ? 'pay' : 'free'">{{ order.fee > 0 ? '-¥' + order.fee.toFixed(2) : '免费' }}</text>
				</view>
			</view>
			<view class="c-card od-mx od-mt">
				<text class="c-title">订单信息</text>
				<view class="od-info">
					<view class="od-info-row"><text class="c-text-sub">订单编号</text><text class="od-info-val">{{ order.orderId }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">充电开始时间</text><text class="od-info-val">{{ order.date }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">充电结束时间</text><text class="od-info-val">{{ orderEndText }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">充电时长</text><text class="od-info-val">{{ order.mins }} 分钟</text></view>
					<view class="od-info-row" v-if="order.kwh"><text class="c-text-sub">用电量</text><text class="od-info-val">{{ order.kwh.toFixed(2) }} 度</text></view>
					<view class="od-info-row"><text class="c-text-sub">支付方式</text><text class="od-info-val">{{ order.way }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">订单状态</text><text class="od-info-val">{{ order.st }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">预扣款金额</text><text class="od-info-val">¥{{ (order.held || 0).toFixed(2) }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">充电结束退款金额</text><text class="od-info-val" :style="{ color: (order.held || 0) - order.fee > 0 ? '#059669' : '#475569' }">{{ (order.held || 0) - order.fee > 0 ? '¥' + ((order.held || 0) - order.fee).toFixed(2) : '¥0.00' }}</text></view>
					<view class="od-info-row"><text class="c-text-sub">实付金额</text><text class="od-info-val" :style="{ color: order.fee > 0 ? '#0f172a' : '#059669', fontWeight: 700 }">{{ order.fee > 0 ? '¥' + order.fee.toFixed(2) : '¥0.00' }}</text></view>
				</view>
			</view>
		</template>

		<!-- 历史订单加载失败/未找到 -->
		<view class="c-card co-empty od-mx od-mt" v-else-if="mode !== 'running' && orderLoaded">
			<image src="/static/h5/images/charge/ic-chart.png" class="co-empty-ic" mode="aspectFit"></image>
			<text class="c-text-sub" style="margin-top:16rpx;">未找到该订单</text>
		</view>

		<!-- 停止充电确认 -->
		<view class="c-mask" v-if="stopShow" @click="stopShow = false"></view>
		<view class="c-dialog" v-if="stopShow">
			<view class="od-dialog-head">
				<view class="od-dialog-ic">
					<image src="/static/h5/images/common/ic-alert.png" class="od-dialog-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-title" style="display:block;">停止充电</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">确定要停止本次充电吗？</text>
				</view>
			</view>
			<view class="od-dialog-warn">停止后按实际充电量结算 · 剩余金额自动退还余额。中途停止可能影响本次充电效果，请确认是否停止。</view>
			<view class="od-dialog-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="stopShow = false">再充一会</view>
				<view class="c-btn-danger" style="flex:1;" @click="confirmStop">确认停止</view>
			</view>
		</view>

		<!-- 结算弹窗 -->
		<view class="c-mask" v-if="settleShow" @click="cancelSettle"></view>
		<view class="c-dialog" v-if="settleShow">
			<view class="od-dialog-head">
				<view class="od-dialog-ic success">
					<image src="/static/h5/images/common/ic-check.png" class="od-dialog-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-title" style="display:block;">充电完成</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">本次充电已结束</text>
				</view>
			</view>
			<view class="od-settle-box">
				<view class="od-info-row"><text class="c-text-sub">充电桩</text><text class="od-info-val">{{ settle.pile }}</text></view>
				<view class="od-info-row"><text class="c-text-sub">充电时长</text><text class="od-info-val">{{ settle.mins }} 分钟</text></view>
				<view class="od-info-row"><text class="c-text-sub">支付方式</text><text class="od-info-val">{{ settle.way }}</text></view>
				<view class="od-info-row" style="border-top:1rpx solid #e2e8f0;padding-top:16rpx;">
					<text class="c-text-sub">实付金额</text>
					<text class="od-settle-fee">¥{{ settle.fee.toFixed(2) }}</text>
				</view>
			</view>
			<view class="od-dialog-btns" style="margin-top:24rpx;">
				<view class="c-btn-ghost" style="flex:1;" @click="cancelSettle">继续充电</view>
				<view class="c-btn-primary" style="flex:1;" @click="finishCharge">完成</view>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		adaptOrderList
	} from '@/api/charge/orderAdapter.js'
	import {
		queryPhoneChargeOrder
	} from '@/packageCharge/api/chargeApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		chgTypes,
		tariffAt,
		defaultTariffs
	} from '@/api/mock/chargeMockData.js'

	export default {
		data() {
			return {
				chgTypes,
				mode: 'done',
				orderId: '',
				order: null,
				orderLoaded: false,
				runningApi: null,
				timeText: '00:00',
				powerText: '0W',
				kwhText: '—',
				fee: 0,
				note: '正在连接车辆 · 请稍候…',
				stopShow: false,
				settleShow: false,
				settle: null,
				tariffs: JSON.parse(JSON.stringify(defaultTariffs)),
				timer: null,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			runningOrder() {
				return chargeStore.runningOrder
			},
			payModeText() {
				const r = this.runningOrder
				if (!r) return ''
				return r.way === 'balance' ? '余额支付' : '月卡抵扣'
			},
			protectText() {
				const r = this.runningOrder
				if (!r) return ''
				return r.type === 'car' ? '金额耗尽自动断电' : (r.dur > 0 ? r.dur + ' 小时后自动断电' : '充满自停 / 定时断电')
			},
			rateText() {
				const r = this.runningOrder
				if (!r) return ''
				const cfg = this.chgTypes[r.type]
				if (r.type === 'car') {
					const t = tariffAt(new Date().getHours(), this.tariffs)
					return '¥' + t.rate.toFixed(2) + '/度（' + t.name + ' · 电 ¥' + t.elec.toFixed(2) + ' + 服 ¥' + t.serv.toFixed(2) +
						'）'
				}
				return '电 ¥' + cfg.elecPrice.toFixed(2) + '/度 + 服 ¥' + cfg.servPrice.toFixed(2) + '/时'
			},
			orderEndText() {
				const o = this.order
				if (!o) return ''
				return this._orderEnd(o.date, o.mins)
			}
		},
		onLoad(options) {
			this.mode = options.mode || 'done'
			this.orderId = options.orderId || ''
			if (this.mode === 'running') {
				if (chargeStore.runningOrder) {
					this._refreshRunning()
				} else if (this.orderId) {
					this._loadRunningByApi()
				}
			} else {
				this._loadOrder()
			}
		},
		onShow() {
			if (this.mode === 'running' && chargeStore.runningOrder) {
				this._startTimer()
				this._refreshRunning()
			}
		},
		onHide() {
			if (this.timer) clearInterval(this.timer)
		},
		onUnload() {
			if (this.timer) clearInterval(this.timer)
		},
		methods: {
			_startTimer() {
				if (this.timer) clearInterval(this.timer)
				this.timer = setInterval(() => {
					chargeStore.simulateTick()
					this._refreshRunning()
				}, 1000)
			},
			// 历史订单：按 orderId 查真实接口
			_loadOrder() {
				if (!hasLogin()) {
					this.orderLoaded = true
					return
				}
				if (!this.orderId) {
					this.orderLoaded = true
					return
				}
				queryPhoneChargeOrder({
					orderId: this.orderId,
					page: 1,
					row: 10
				}).then(res => {
					const list = adaptOrderList(res.data)
					this.order = list.length ? list[0] : null
					this.orderLoaded = true
				}).catch(() => {
					this.order = null
					this.orderLoaded = true
				})
			},
			// 在充订单：本地无 runningOrder 时按 orderId 查接口只读展示
			_loadRunningByApi() {
				if (!hasLogin()) return
				queryPhoneChargeOrder({
					orderId: this.orderId,
					page: 1,
					row: 10,
					state: '1001'
				}).then(res => {
					const list = adaptOrderList(res.data)
					if (!list.length) return
					this.runningApi = list[0]
					this.powerText = this.runningApi.type === 'car' ? '7.0kW' : '220W'
					this.kwhText = this.runningApi.kwh > 0 ? this.runningApi.kwh.toFixed(2) + ' 度' : '—'
					this.fee = this.runningApi.fee
					this.note = this.runningApi.type === 'car' ? '按电量计费 · 金额耗尽自动断电' : '充满自停 / 定时断电'
					this._refreshRunningApi()
					if (this.timer) clearInterval(this.timer)
					this.timer = setInterval(() => {
						this._refreshRunningApi()
					}, 1000)
				}).catch(() => {})
			},
			// 接口在充时长实时刷新（仅刷新时长，无本地假数据）
			_refreshRunningApi() {
				const r = this.runningApi
				if (!r || !r.startTime) return
				const t = new Date(String(r.startTime).replace(/-/g, '/')).getTime()
				const sec = isNaN(t) ? 0 : Math.max(0, Math.floor((Date.now() - t) / 1000))
				this.timeText = ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
			},
			_refreshRunning() {
				const r = chargeStore.runningOrder
				if (!r) return
				const sec = Math.max(0, Math.floor((Date.now() - chargeStore.chgStart) / 1000))
				this.timeText = ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
				this.fee = chargeStore.chgFee
				this.kwhText = chargeStore.chgKwh.toFixed(2) + ' 度'
				const cfg = this.chgTypes[r.type]
				this.powerText = r.type === 'car' ? ([6.92, 7.0, 7.04, 6.98, 7.01][sec % 5]).toFixed(1) + 'kW' : [215,
					220, 224, 219, 222
				][sec % 5] + 'W'
				this.note = r.type === 'car' ? '电压 ' + cfg.volt + 'V · 电流 ' + cfg.amp + 'A · 按电量计费 · 金额耗尽自动断电' : '电压 ' +
					cfg.volt + 'V · 电流 ' + cfg.amp + 'A · 充满自停 / 定时断电'
			},
			_orderEnd(date, mins) {
				let d = new Date()
				let m = /今天 (\d+):(\d+)/.exec(date)
				if (!m) m = /昨天 (\d+):(\d+)/.exec(date)
				if (m) {
					if (date.startsWith('昨天')) d.setDate(d.getDate() - 1)
					d.setHours(+m[1], +m[2], 0, 0)
				} else {
					const m2 = /(\d+)-(\d+) (\d+):(\d+)/.exec(date)
					if (m2) d = new Date(2026, +m2[1] - 1, +m2[2], +m2[3], +m2[4], 0, 0)
				}
				d.setMinutes(d.getMinutes() + mins)
				const hh = ('0' + d.getHours()).slice(-2)
				const mm = ('0' + d.getMinutes()).slice(-2)
				const now = new Date()
				if (d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth() && d.getDate() === now.getDate()) {
					return '今天 ' + hh + ':' + mm
				}
				const yest = new Date(now)
				yest.setDate(now.getDate() - 1)
				if (d.getFullYear() === yest.getFullYear() && d.getMonth() === yest.getMonth() && d.getDate() === yest.getDate()) {
					return '昨天 ' + hh + ':' + mm
				}
				return ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2) + ' ' + hh + ':' + mm
			},
			askStop() {
				this.stopShow = true
			},
			confirmStop() {
				this.stopShow = false
				const settle = chargeStore.settleCharge()
				if (!settle) return
				this.settle = settle
				this.settleShow = true
			},
			cancelSettle() {
				this.settleShow = false
				this._stopAndBack()
			},
			finishCharge() {
				this.settleShow = false
				this._toast('充电完成 · 费用已结算')
				this._stopAndBack()
			},
			_stopAndBack() {
				if (this.timer) clearInterval(this.timer)
				uni.navigateTo({
					url: '/packageCharge/pages/charge/chargeOrders'
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
</script>

<style>
	.od-page {
		padding-bottom: 60rpx;
	}

	.od-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.od-mt {
		margin-top: 24rpx;
	}

	.od-running-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 32rpx;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .25);
	}

	.od-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.od-title-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.od-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.od-slot-tag {
		color: #fff;
		font-size: 20rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.od-mode {
		color: #fff;
		font-size: 20rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 6rpx 20rpx;
	}

	.od-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 12rpx;
		margin-top: 28rpx;
	}

	.od-stat {
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 18rpx 0;
		text-align: center;
	}

	.od-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 16rpx;
	}

	.od-stat-val {
		display: block;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	.od-note {
		margin-top: 20rpx;
		border-radius: 20rpx;
		background: rgba(255, 255, 255, .1);
		padding: 16rpx 20rpx;
		color: rgba(255, 255, 255, .85);
		font-size: 20rpx;
		line-height: 1.6;
	}

	.od-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.od-info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.od-info-val {
		font-size: 22rpx;
		font-weight: 600;
		color: #334155;
		text-align: right;
	}

	.od-stop-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 20rpx;
		margin-top: 16rpx;
	}

	.od-done-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
	}

	.od-done-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.od-done-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.od-done-name-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.od-done-fee {
		font-size: 28rpx;
		font-weight: 700;
	}

	.od-done-fee.pay {
		color: #0f172a;
	}

	.od-done-fee.free {
		color: #059669;
	}

	.od-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.od-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(239, 68, 68, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.od-dialog-ic-img {
		width: 32rpx;
		height: 32rpx;
	}

	.od-dialog-ic.success {
		background: rgba(16, 185, 129, .1);
	}

	.od-dialog-warn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 22rpx;
		line-height: 1.6;
	}

	.od-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.od-settle-box {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.od-settle-fee {
		font-size: 30rpx;
		font-weight: 700;
		color: #059669;
	}

	.od-readonly-tip {
		display: block;
		text-align: center;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		padding: 24rpx;
		color: #059669;
		font-size: 22rpx;
		line-height: 1.6;
	}
</style>
