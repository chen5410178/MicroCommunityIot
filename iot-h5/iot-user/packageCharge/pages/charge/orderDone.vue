<template>
	<view class="ad-page" :class="{ 'charge-theme-dark': themeDark }">
		<template v-if="order">
			<!-- 结算中提示 -->
			<view class="ad-banner" v-if="showSettling">结算处理中，请稍后下拉刷新</view>

			<!-- 头部 -->
			<view class="c-card ad-mx ad-mt">
				<view class="ad-head">
					<view class="ad-ic">
						<image v-if="isCar" :src="imgUrl + '/h5/images/charge/ic-car.png'" class="ad-ic-img" mode="aspectFit"></image>
						<image v-else :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="ad-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="ad-name-row">
							<text class="c-title">{{ order.pile || '充电桩' }}</text>
							<text class="c-badge" :class="badgeClass">{{ order.st }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ order.date }}</text>
					</view>
					<text class="ad-fee" :class="payFee > 0 ? 'pay' : 'free'">{{ headFeeText }}</text>
				</view>
				<view class="ad-hero-sub">
					<text class="c-text-sub">{{ isCar ? '汽车充电' : '电瓶车充电' }} · {{ order.way || '—' }}</text>
				</view>
			</view>

			<!-- 订单信息 -->
			<view class="c-card ad-mx ad-mt">
				<text class="c-title">订单信息</text>
				<view class="ad-info">
					<view class="ad-info-row"><text class="c-text-sub">订单编号</text><text class="ad-info-val">{{ order.orderId || '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">充电桩</text><text class="ad-info-val">{{ order.pile || '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">{{ isCar ? '充电枪' : '插座' }}</text><text class="ad-info-val">{{ order.slot || '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">开始时间</text><text class="ad-info-val">{{ order.startTime || '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">结束时间</text><text class="ad-info-val">{{ endTimeText }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">充电时长</text><text class="ad-info-val">{{ order.mins }} 分钟</text></view>
					<view class="ad-info-row"><text class="c-text-sub">用电量</text><text class="ad-info-val">{{ order.kwh ? order.kwh.toFixed(2) + ' 度' : '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">支付方式</text><text class="ad-info-val">{{ order.way || '—' }}</text></view>
					<view class="ad-info-row"><text class="c-text-sub">订单状态</text><text class="ad-info-val">{{ order.st || '—' }}</text></view>
				</view>
			</view>

			<!-- 费用明细 -->
			<view class="c-card ad-mx ad-mt">
				<text class="c-title">费用明细</text>
				<view class="ad-info">
					<view class="ad-info-row" v-if="isMonthCard">
						<text class="c-text-sub">扣款方式</text>
						<text class="ad-info-val" style="color:#d97706;font-weight:700;">月卡抵扣</text>
					</view>
					<template v-else>
						<view class="ad-info-row" v-if="hasHeld">
							<text class="c-text-sub">预扣款金额</text>
							<text class="ad-info-val">¥{{ heldFee.toFixed(2) }}</text>
						</view>
						<view class="ad-info-row" v-if="hasHeld">
							<text class="c-text-sub">退款金额</text>
							<text class="ad-info-val" :style="refundFee > 0 ? 'color:#059669;' : ''">{{ refundFee > 0 ? '¥' + refundFee.toFixed(2) : '¥0.00' }}</text>
						</view>
						<view class="ad-info-row" v-else>
							<text class="c-text-sub">结算方式</text>
							<text class="ad-info-val">按实际充电量结算</text>
						</view>
						<view class="ad-info-row" style="border-top:1rpx solid #e2e8f0;padding-top:16rpx;">
							<text class="c-text-sub">实付金额</text>
							<text class="ad-info-val" :style="{ color: payFee > 0 ? '#0f172a' : '#059669', fontWeight: 700, fontSize: '30rpx' }">¥{{ payFee.toFixed(2) }}</text>
						</view>
					</template>
				</view>
				<view class="ad-tip" v-if="isFail">本次充电失败，如已扣款将原路退回，请稍后关注退款到账。</view>
			</view>

			<!-- 申请发票 -->
			<view class="ad-mx ad-mt" v-if="canInvoice" style="padding-bottom:80rpx;">
				<view class="c-btn-primary" @click="goInvoice">申请发票</view>
			</view>
			<view v-else style="height:80rpx;"></view>
		</template>

		<view class="c-card co-empty ad-mx ad-mt" v-else-if="orderLoaded">
			<image :src="imgUrl + '/h5/images/charge/ic-chart.png'" class="co-empty-ic" mode="aspectFit"></image>
			<text class="c-text-sub" style="margin-top:16rpx;">未找到该订单</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import {
		adaptOrderList
	} from '@/api/charge/orderAdapter.js'
	import {
		queryPhoneChargeOrder
	} from '@/packageCharge/api/chargeApi.js'
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

	function fmtFull(t) {
		const ts = toTime(t)
		if (!ts) return ''
		const d = new Date(ts)
		return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' +
			pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds())
	}

	export default {
		data() {
			return {
				orderId: '',
				chargeType: '',
				stopped: false,
				order: null,
				orderLoaded: false,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			isCar() {
				return this.chargeType === '1002'
			},
			// 月卡订单无实付金额，按扣款方式展示
			isMonthCard() {
				return !!this.order && this.order.acctDetailId === '-1'
			},
			isFail() {
				return !!this.order && this.order.state === '3003'
			},
			// 结算中：已停止 / 在充且尚未回填真实结束时间
			showSettling() {
				const o = this.order
				if (!o) return false
				return o.state === '4004' || o.state === '1001' || this.stopped
			},
			payFee() {
				if (this.isMonthCard) return 0
				return (this.order && this.order.fee) || 0
			},
			// 头部金额：月卡订单展示抵扣方式，避免误读为已扣款
			headFeeText() {
				if (this.isMonthCard) return '月卡抵扣'
				return '¥' + this.payFee.toFixed(2)
			},
			heldFee() {
				return (this.order && this.order.held) || 0
			},
			// 后台未回传预扣款时隐藏预扣/退款明细，避免展示推算数值
			hasHeld() {
				return this.heldFee > 0
			},
			refundFee() {
				const o = this.order
				if (!o) return 0
				const refund = (o.refundFee !== undefined && o.refundFee !== null) ? o.refundFee : o.refund
				if (refund !== undefined && refund !== null && refund !== '') return parseFloat(refund) || 0
				return Math.max(0, this.heldFee - this.payFee)
			},
			// 结算中按预扣款展示，结算后回填实付
			endTimeText() {
				const o = this.order
				if (!o) return '—'
				// 结束中/在充：订单 end_time 为预计结束时间，不代表真实结算时间
				if (o.state === '4004' || o.state === '1001') return '结算中…'
				if (o.endTime) {
					const f = fmtFull(o.endTime)
					if (f) return f
				}
				const base = this._orderEndTs(o)
				if (!base) return '—'
				return fmtFull(base + (o.mins || 0) * 60 * 1000)
			},
			badgeClass() {
				const st = this.order ? this.order.state : ''
				if (st === '2002') return 'c-badge-green'
				if (st === '3003') return 'c-badge-red'
				if (st === '1001' || st === '4004') return 'c-badge-orange'
				return 'c-badge-gray'
			},
			canInvoice() {
				const o = this.order
				return !!(o && o.state === '2002' && o.fee > 0 && !this.isMonthCard)
			}
		},
		onLoad(options) {
			const opt = options || {}
			this.orderId = opt.orderId || ''
			this.chargeType = opt.chargeType || ''
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
		onPullDownRefresh() {
			this._loadOrder(true)
		},
		methods: {
			_loadOrder(byPull) {
				queryPhoneChargeOrder({
					orderId: this.orderId,
					page: 1,
					row: 10
				}).then(res => {
					const list = adaptOrderList(res.data)
					this.order = list.length ? list[0] : null
					if (this.order && !this.chargeType) {
						this.chargeType = this.order.type === 'car' ? '1002' : '1001'
					}
					this.orderLoaded = true
					if (byPull) {
						uni.stopPullDownRefresh()
						this._toast('已刷新')
					}
				}).catch(() => {
					this.order = null
					this.orderLoaded = true
					if (byPull) {
						uni.stopPullDownRefresh()
						this._toast('刷新失败，请稍后重试')
					}
				})
			},
			// 历史订单无 endTime 时按 日期 + 时长 推算（仅展示用）
			_orderEndTs(o) {
				let d = new Date()
				let m = /(\d+)-(\d+) (\d+):(\d+)/.exec(o.date || '')
				if (m) {
					d = new Date(d.getFullYear(), +m[1] - 1, +m[2], +m[3], +m[4], 0, 0)
				} else {
					m = /今天 (\d+):(\d+)/.exec(o.date || '')
					if (m) {
						d.setHours(+m[1], +m[2], 0, 0)
					} else {
						m = /昨天 (\d+):(\d+)/.exec(o.date || '')
						if (m) {
							d.setDate(d.getDate() - 1)
							d.setHours(+m[1], +m[2], 0, 0)
						}
					}
				}
				return d.getTime()
			},
			goInvoice() {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/invoice'
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
	.ad-page {
		padding-bottom: 60rpx;
	}

	.ad-page .c-title {
		font-size: 30rpx;
	}

	.ad-page .c-text-sub {
		font-size: 24rpx;
	}

	.ad-page .c-badge-red {
		background: rgba(239, 68, 68, .1);
		color: #dc2626;
		border: 1rpx solid rgba(239, 68, 68, .3);
	}

	.ad-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.ad-mt {
		margin-top: 24rpx;
	}

	.ad-banner {
		margin: 24rpx 28rpx 0;
		border-radius: 20rpx;
		background: rgba(245, 158, 11, .12);
		border: 1rpx solid rgba(245, 158, 11, .3);
		color: #d97706;
		font-size: 24rpx;
		font-weight: 600;
		padding: 18rpx 24rpx;
	}

	.ad-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx 28rpx 0;
	}

	.ad-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.ad-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.ad-name-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.ad-fee {
		font-size: 32rpx;
		font-weight: 700;
	}

	.ad-fee.pay {
		color: #0f172a;
	}

	.ad-fee.free {
		color: #059669;
	}

	.ad-hero-sub {
		padding: 16rpx 28rpx 26rpx;
	}

	.ad-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.ad-info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.ad-info-val {
		font-size: 26rpx;
		font-weight: 600;
		color: #334155;
		text-align: right;
		max-width: 60%;
	}

	.ad-tip {
		margin: 0 28rpx 28rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 24rpx;
		line-height: 1.6;
	}

	.co-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 80rpx 0;
	}

	.co-empty-ic {
		width: 64rpx;
		height: 64rpx;
	}
</style>
