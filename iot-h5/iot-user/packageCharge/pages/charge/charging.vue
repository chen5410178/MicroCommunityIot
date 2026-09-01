<template>
	<view class="chg-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 充电状态卡 -->
		<view class="chg-hero">
			<view class="chg-hero-head">
				<view class="chg-title-row">
					<view class="c-pulse-dot chg-dot"></view>
					<text class="chg-title">充电中 · {{ pileName }}</text>
					<text class="chg-slot-tag" v-if="slotText">{{ slotText }}</text>
				</view>
				<text class="chg-mode">{{ payModeText }}</text>
			</view>

			<!-- 进度环 -->
			<view class="chg-ring-wrap">
				<svg class="chg-ring" viewBox="0 0 100 100">
					<circle cx="50" cy="50" r="44" fill="none" stroke="rgba(255,255,255,.16)" stroke-width="8" />
					<circle class="c-ring" cx="50" cy="50" r="44" fill="none" stroke="#fff" stroke-width="8"
						stroke-linecap="round" :stroke-dasharray="276.46" :stroke-dashoffset="ringOffset" />
				</svg>
				<view class="chg-ring-center">
					<text class="chg-ring-pct">{{ ringPct }}%</text>
					<text class="chg-ring-label">充电进度</text>
				</view>
				<view class="chg-bolt c-bolt-spin">⚡</view>
			</view>

			<view class="chg-stats">
				<view class="chg-stat">
					<text class="chg-stat-label">已充时长</text>
					<text class="chg-stat-val">{{ timeText }}</text>
				</view>
				<view class="chg-stat">
					<text class="chg-stat-label">当前功率</text>
					<text class="chg-stat-val">{{ powerText }}</text>
				</view>
				<view class="chg-stat">
					<text class="chg-stat-label">已用电量</text>
					<text class="chg-stat-val">{{ kwhText }}</text>
				</view>
				<view class="chg-stat">
					<text class="chg-stat-label">已充费用</text>
					<text class="chg-stat-val">¥{{ fee.toFixed(2) }}</text>
				</view>
			</view>
			<view class="chg-note">{{ note }}</view>
		</view>

		<!-- 充电状态步骤 -->
		<view class="c-card chg-mx chg-mt">
			<text class="c-title">充电状态</text>
			<view class="chg-steps">
				<view class="chg-step">
					<view class="chg-step-ic done">✓</view>
					<text class="chg-step-text">已连接车辆 · 插座已锁定</text>
				</view>
				<view class="chg-step">
					<view class="chg-step-ic done">✓</view>
					<text class="chg-step-text">正常充电 · 实时计费</text>
				</view>
				<view class="chg-step">
					<view class="chg-step-ic waiting">✓</view>
					<text class="chg-step-text" style="color:#94a3b8;">{{ protectText }}</text>
				</view>
			</view>
		</view>

		<!-- 停止充电 -->
		<view class="chg-mx chg-mt" style="padding-bottom:80rpx;">
			<view class="c-btn-danger" @click="askStop">停止充电</view>
			<text class="chg-stop-tip">停止后未使用的预扣金额将原路退回余额</text>
		</view>

		<!-- 停止充电确认弹窗 -->
		<view class="c-mask" v-if="stopShow" @click="stopShow = false"></view>
		<view class="c-dialog" v-if="stopShow">
			<view class="chg-dialog-head">
				<view class="chg-dialog-ic danger">!</view>
				<view>
					<text class="c-title" style="display:block;">停止充电</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">确定要停止本次充电吗？</text>
				</view>
			</view>
			<view class="chg-dialog-warn">停止后按实际充电量结算 · 剩余金额自动退还余额。中途停止可能影响本次充电效果，请确认是否停止。</view>
			<view class="chg-dialog-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="stopShow = false">再充一会</view>
				<view class="c-btn-danger" style="flex:1;" @click="confirmStop">确认停止</view>
			</view>
		</view>

		<!-- 结算弹窗 -->
		<view class="c-mask" v-if="settleShow" @click="cancelSettle"></view>
		<view class="c-dialog" v-if="settleShow">
			<view class="chg-dialog-head">
				<view class="chg-dialog-ic success">✓</view>
				<view>
					<text class="c-title" style="display:block;">充电完成</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">本次充电已结束</text>
				</view>
			</view>
			<view class="chg-settle-box">
				<view class="chg-settle-row"><text class="c-text-sub">充电桩</text><text class="chg-settle-val">{{ settle.pile }}</text></view>
				<view class="chg-settle-row"><text class="c-text-sub">充电时长</text><text class="chg-settle-val">{{ settle.mins }} 分钟</text></view>
				<view class="chg-settle-row"><text class="c-text-sub">支付方式</text><text class="chg-settle-val">{{ settle.way }}</text></view>
				<view class="chg-settle-row" style="border-top:1rpx solid #e2e8f0;padding-top:16rpx;">
					<text class="c-text-sub">实付金额</text>
					<text class="chg-settle-fee">¥{{ settle.fee.toFixed(2) }}</text>
				</view>
			</view>
			<text class="c-text-sub" style="display:block;margin-top:16rpx;line-height:1.6;">{{ settleNote }}</text>
			<view class="chg-dialog-btns" style="margin-top:24rpx;">
				<view class="c-btn-ghost" style="flex:1;" @click="cancelSettle">继续充电</view>
				<view class="c-btn-primary" style="flex:1;" @click="finishCharge">完成</view>
			</view>
		</view>

		<!-- 顶部消息横幅 -->
		<view class="c-top-msg" v-if="topMsgShow" @click="openTopMsg">
			<view class="chg-topmsg-body">
				<view class="chg-topmsg-ic">{{ topMsgIcon }}</view>
				<view style="flex:1;min-width:0;">
					<view class="chg-topmsg-title-row">
						<text class="chg-topmsg-title">{{ topMsgTitle }}</text>
						<view class="chg-topmsg-close" @click.stop="topMsgShow = false">✕</view>
					</view>
					<text class="chg-topmsg-text">{{ topMsgText }}</text>
				</view>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		chgTypes,
		tariffAt,
		defaultTariffs
	} from '@/api/mock/chargeMockData.js'

	export default {
		data() {
			return {
				chgTypes,
				pileName: '',
				slotText: '',
				payModeText: '',
				timeText: '00:00',
				powerText: '0W',
				kwhText: '—',
				fee: 0,
				ringPct: 0,
				ringOffset: 276.46,
				note: '正在连接车辆 · 请稍候…',
				protectText: '充满自停保护中',
				stopShow: false,
				settleShow: false,
				settle: null,
				settleNote: '',
				topMsgShow: false,
				topMsgTitle: '',
				topMsgText: '',
				topMsgIcon: '⚡',
				tariffs: JSON.parse(JSON.stringify(defaultTariffs)),
				toastMsg: '',
				timer: null,
				finished: false
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad(options) {
			const params = JSON.parse(decodeURIComponent(options.params || '{}'))
			this._initCharge(params)
		},
		onShow() {
			// 从后台回来继续刷新
			if (chargeStore.runningOrder && !this.timer && !this.finished) {
				this._startTimer()
				this._refresh()
			}
		},
		onUnload() {
			this._stopTimer()
			// 未结束时保留 runningOrder（在充状态供首页/订单页展示）
		},
		methods: {
			_initCharge(params) {
				const running = {
					pile: params.pile || '',
					slot: params.slot || '',
					gun: params.gun || '',
					type: params.chgType || 'bike',
					way: params.payWay || 'balance',
					dur: params.dur || 0,
					amt: params.amt || 0,
					deposit: params.deposit || 0,
					start: Date.now()
				}
				// 找到桩名
				const list = chargeStore.pilesByType(running.type)
				const pile = list[parseInt(params.pileIdx) || 0]
				if (pile) running.pile = pile.name
				chargeStore.chgType = running.type
				chargeStore.payWay = running.way
				chargeStore.chgHeld = running.way === 'balance' ? Math.min(running.deposit, chargeStore.balance) : 0
				if (running.way === 'balance') chargeStore.setBalance(chargeStore.balance - chargeStore.chgHeld)
				chargeStore.chgFee = 0
				chargeStore.chgKwh = 0
				chargeStore.chgStart = Date.now()
				chargeStore.occupySlotGun(running)
				chargeStore.setRunningOrder(running)

				this.pileName = running.pile
				this.slotText = running.type === 'bike' ? ('插座 ' + running.slot) : ('枪 ' + running.gun)
				this.payModeText = running.way === 'balance' ? '余额支付' : (chargeStore.myCard && chargeStore.myCard.type ===
					'month' ? '畅充月卡' : '次卡抵扣')
				this.protectText = running.type === 'car' ? '金额耗尽自动断电' : (running.dur > 0 ? running.dur + ' 小时后自动断电' : '充满自停保护中')
				const cfg = this.chgTypes[running.type]
				this.powerText = '0' + cfg.unit
				this.kwhText = running.type === 'car' ? '0.00 度' : '—'
				this._refresh()
				this._startTimer()
				// 顶部消息：充电开始
				this._showTopMsg('充电已开始', (running.type === 'car'
					? running.pile + ' ' + this.slotText + ' 已连接 · 预支付 ¥' + running.amt.toFixed(2) + ' · 按电量计费'
					: running.pile + ' ' + this.slotText + ' 已连接 · 选择 ' + (running.dur > 0 ? running.dur + ' 小时' : '充满自停') +
					' ，预扣 ¥' + chargeStore.chgHeld.toFixed(2) + ' · 到点或充满自动断电'), 'charge')
				setTimeout(() => {
					const cfg2 = this.chgTypes[running.type]
					if (running.type === 'car') {
						const t = tariffAt(new Date().getHours(), this.tariffs)
						this.note = '电压 ' + cfg2.volt + 'V · 电流 ' + cfg2.amp + 'A · 当前费率 ¥' + t.rate.toFixed(2) +
							'/度（' + t.name + ' · 电 ¥' + t.elec.toFixed(2) + ' + 服 ¥' + t.serv.toFixed(2) +
							'）· 预算 ¥' + running.amt.toFixed(2) + ' · 金额耗尽自动断电'
					} else {
						const remain = running.dur > 0 ? (running.dur * 60).toFixed(0) : '未知'
						this.note = '电压 ' + cfg2.volt + 'V · 电流 ' + cfg2.amp + 'A · 已选时长 ' + (running.dur > 0 ? running.dur +
							' 小时' : '充满自停') + ' · 预扣 ¥' + chargeStore.chgHeld.toFixed(2) + ' · 预计还需 ' + remain +
							' 分钟 · ' + (running.dur > 0 ? '到点自动断电' : '充满自动断电')
					}
					this.powerText = cfg2.powerText
				}, 800)
			},
			_startTimer() {
				this._stopTimer()
				this.timer = setInterval(() => {
					this._tick()
				}, 1000)
			},
			_stopTimer() {
				if (this.timer) {
					clearInterval(this.timer)
					this.timer = null
				}
			},
			_tick() {
				const r = chargeStore.runningOrder
				if (!r || this.finished) return
				chargeStore.simulateTick()
				this._refresh()
				const sec = Math.floor((Date.now() - chargeStore.chgStart) / 1000)
				const cfg = this.chgTypes[r.type]
				// 自动断电
				if (r.type === 'car' && chargeStore.chgFee >= (r.amt || 0)) {
					this._toast('已用完预算 ¥' + r.amt.toFixed(2) + ' · 自动断电')
					this._doSettle(true)
					return
				}
				if (r.way === 'balance' && chargeStore.chgHeld > 0 && chargeStore.chgFee >= chargeStore.chgHeld) {
					this._toast('已用完预扣 ¥' + chargeStore.chgHeld.toFixed(2) + ' · 自动断电')
					this._doSettle(true)
					return
				}
				if (r.dur > 0 && sec >= r.dur * 360) {
					this._toast('已达到设定时长 · 自动断电')
					this._doSettle(true)
					return
				}
			},
			_refresh() {
				const r = chargeStore.runningOrder
				if (!r) return
				const sec = Math.max(0, Math.floor((Date.now() - chargeStore.chgStart) / 1000))
				this.timeText = this._fmtTime(sec)
				this.fee = chargeStore.chgFee
				this.kwhText = chargeStore.chgKwh.toFixed(2) + ' 度'
				const cfg = this.chgTypes[r.type]
				this.powerText = r.type === 'car' ? ([6.92, 7.0, 7.04, 6.98, 7.01][sec % 5]).toFixed(1) + 'kW' : [215,
					220, 224, 219, 222
				][sec % 5] + 'W'
				// 进度环
				let pct = 0
				if (r.type === 'car') {
					pct = (r.amt || 0) > 0 ? Math.min(100, chargeStore.chgFee / r.amt * 100) : 0
				} else if (r.dur > 0) {
					pct = Math.min(100, sec / (r.dur * 360) * 100)
				} else {
					pct = Math.min(100, sec / 1440 * 100)
				}
				this.ringPct = Math.round(pct)
				this.ringOffset = (276.46 * (1 - pct / 100)).toFixed(2)
			},
			_fmtTime(sec) {
				return ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
			},
			askStop() {
				this.stopShow = true
			},
			confirmStop() {
				this.stopShow = false
				this._doSettle(false)
			},
			_doSettle(auto) {
				if (this.finished) return
				this.finished = true
				this._stopTimer()
				const settle = chargeStore.settleCharge()
				if (!settle) {
					this._stopTimer()
					uni.navigateBack({})
					return
				}
				this.settle = settle
				const r = chargeStore.runningOrder
				// 结算文案
				this.settleNote = '充电 ' + settle.mins + ' 分钟 · 用电 ' + settle.kwh.toFixed(2) + ' 度' +
					(settle.refund > 0 ? ' · 退回 ¥' + settle.refund.toFixed(2) : '')
				if (auto) {
					this.settleShow = true
					this._showTopMsg('充电完成 · 已结账', (settle.pile || '充电桩') + ' · 用时 ' + settle.mins + ' 分钟 · 费用 ¥' + settle
						.fee.toFixed(2) + (settle.refund > 0 ? ' · 退回 ¥' + settle.refund.toFixed(2) : ''), 'charge')
				} else {
					this.settleShow = true
				}
			},
			cancelSettle() {
				this.settleShow = false
				this._stopTimer()
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeOrders' })
			},
			finishCharge() {
				this.settleShow = false
				this._toast('充电完成 · 费用已结算')
				uni.switchTab({ url: '/pages/index/index' })
			},
			_showTopMsg(title, text, type) {
				this.topMsgTitle = title
				this.topMsgText = text
				this.topMsgIcon = type === 'charge' ? '⚡' : '📇'
				this.topMsgShow = true
				chargeStore.addMsg(type || 'charge', title, text)
				clearTimeout(this._topMsgTimer)
				this._topMsgTimer = setTimeout(() => {
					this.topMsgShow = false
				}, 5000)
			},
			openTopMsg() {
				this.topMsgShow = false
				uni.navigateTo({ url: '/packageCharge/pages/charge/message' })
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
	.chg-page {
		padding-bottom: 40rpx;
	}

	.chg-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 36rpx;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .25);
	}

	.chg-hero-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.chg-title-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.chg-dot {
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
		background: #fff;
	}

	.chg-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.chg-slot-tag {
		color: #fff;
		font-size: 20rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.chg-mode {
		color: #fff;
		font-size: 20rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 6rpx 20rpx;
	}

	.chg-ring-wrap {
		position: relative;
		width: 280rpx;
		height: 280rpx;
		margin: 40rpx auto;
	}

	.chg-ring {
		width: 100%;
		height: 100%;
		transform: rotate(-90deg);
	}

	.chg-ring-center {
		position: absolute;
		inset: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.chg-ring-pct {
		color: #fff;
		font-size: 40rpx;
		font-weight: 700;
		line-height: 1;
	}

	.chg-ring-label {
		color: rgba(255, 255, 255, .6);
		font-size: 16rpx;
		margin-top: 8rpx;
	}

	.chg-bolt {
		position: absolute;
		top: -8rpx;
		right: -8rpx;
		width: 56rpx;
		height: 56rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, .2);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, .2);
	}

	.chg-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 12rpx;
	}

	.chg-stat {
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 20rpx 0;
		text-align: center;
	}

	.chg-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.chg-stat-val {
		display: block;
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.chg-note {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(255, 255, 255, .1);
		padding: 18rpx 24rpx;
		color: rgba(255, 255, 255, .85);
		font-size: 20rpx;
		line-height: 1.6;
	}

	.chg-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.chg-mt {
		margin-top: 24rpx;
	}

	.chg-steps {
		padding: 0 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.chg-step {
		display: flex;
		align-items: center;
		gap: 18rpx;
	}

	.chg-step-ic {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 20rpx;
		font-weight: 700;
	}

	.chg-step-ic.done {
		background: rgba(16, 185, 129, .12);
		color: #059669;
	}

	.chg-step-ic.waiting {
		background: #f1f5f9;
		color: #94a3b8;
	}

	.chg-step-text {
		font-size: 22rpx;
		color: #475569;
	}

	.chg-stop-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 20rpx;
		margin-top: 16rpx;
	}

	.chg-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.chg-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
		font-weight: 700;
	}

	.chg-dialog-ic.danger {
		background: rgba(239, 68, 68, .1);
		color: #ef4444;
	}

	.chg-dialog-ic.success {
		background: rgba(16, 185, 129, .1);
		color: #059669;
	}

	.chg-dialog-warn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 22rpx;
		line-height: 1.6;
	}

	.chg-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.chg-settle-box {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.chg-settle-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.chg-settle-val {
		font-size: 24rpx;
		font-weight: 600;
		color: #1e293b;
	}

	.chg-settle-fee {
		font-size: 30rpx;
		font-weight: 700;
		color: #059669;
	}

	.chg-topmsg-body {
		display: flex;
		gap: 20rpx;
		padding: 24rpx;
	}

	.chg-topmsg-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
	}

	.chg-topmsg-title-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
	}

	.chg-topmsg-title {
		font-size: 26rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.chg-topmsg-close {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background: #f1f5f9;
		color: #94a3b8;
		font-size: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.chg-topmsg-text {
		display: block;
		font-size: 20rpx;
		color: #64748b;
		margin-top: 8rpx;
		line-height: 1.5;
	}
</style>
