<template>
	<view class="rd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 订单头部 -->
		<view class="rd-hero">
			<view class="rd-hero-head">
				<text class="rd-title">{{ order.plate }} · {{ order.plan }}</text>
				<text class="rd-state">{{ order.st }}</text>
			</view>
			<text class="rd-label">实付金额</text>
			<text class="rd-fee">¥{{ order.fee }}</text>
			<text class="rd-period">有效期 {{ order.from }} → {{ order.to }}</text>
		</view>

		<!-- 订单信息 -->
		<view class="c-card rd-mx rd-mt">
			<text class="c-title">订单信息</text>
			<view class="rd-info">
				<view class="rd-info-row"><text class="c-text-sub">订单编号</text><text class="rd-info-val">{{ order.orderId || '—' }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">车牌号码</text><text class="rd-info-val">{{ order.plate || '—' }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">月卡</text><text class="rd-info-val">{{ order.plan || '—' }}{{ order.months ? '（' + order.months + ' 个月）' : '' }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">支付方式</text><text class="rd-info-val">{{ order.payType || '—' }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">支付时间</text><text class="rd-info-val">{{ order.time || '—' }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">订单状态</text><text class="rd-info-val" style="color:#059669;">{{ order.st }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">实付金额</text><text class="rd-info-val" style="font-weight:700;">¥{{ order.fee }}</text></view>
			</view>
		</view>

		<view class="rd-tip rd-mx rd-mt">
			<text style="font-size:20rpx;line-height:1.6;color:#3b82f6;">月卡生效期间车辆进出小区按月卡计费，到期前可随时在车辆服务页购买月卡。</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		listUserCarMonthOrder
	} from '@/packageCharge/api/chargeApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'

	export default {
		data() {
			return {
				orderId: '',
				order: {},
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad(options) {
			this.orderId = options.orderId || ''
			this._loadOrder()
		},
		methods: {
			_loadOrder() {
				listUserCarMonthOrder({
					communityId: getCommunityId()
				}).then(list => {
					const orders = list || []
					let target = null
					if (this.orderId) {
						target = orders.find(o => o.orderId === this.orderId) || null
					}
					if (!target) target = orders[0] || null
					this.order = this._adapterOrder(target)
				}).catch(e => {
					this._toast((typeof e === 'string' ? e : (e && e.message)) || '订单加载失败')
				})
			},
			_adapterOrder(o) {
				if (!o) return {}
				return {
					orderId: o.orderId || '',
					plate: o.carNum || '',
					plan: o.cardName || '',
					months: this._monthSpan(o.startTime, o.endTime),
					fee: o.receivedAmount || o.receivableAmount || 0,
					from: this._toDate(o.startTime),
					to: this._toDate(o.endTime),
					time: this._toDateTime(o.createTime),
					payType: o.primeRateName || '微信支付',
					st: o.stateName || (o.state === '1001' ? '正常' : (o.state === '2002' ? '已退费' : '—'))
				}
			},
			_monthSpan(from, to) {
				const a = this._toDate(from)
				const b = this._toDate(to)
				if (!a || !b) return ''
				const d1 = new Date(a.replace(/-/g, '/'))
				const d2 = new Date(b.replace(/-/g, '/'))
				if (isNaN(d1.getTime()) || isNaN(d2.getTime())) return ''
				return Math.max(1, (d2.getFullYear() - d1.getFullYear()) * 12 + (d2.getMonth() - d1.getMonth()))
			},
			_toDate(v) {
				if (!v) return ''
				const s = String(v)
				if (s.indexOf('-') >= 0) return s.slice(0, 10)
				const d = new Date(Number(s))
				if (isNaN(d.getTime())) return ''
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2)
			},
			_toDateTime(v) {
				if (!v) return ''
				const s = String(v)
				if (s.indexOf('-') >= 0) return s.slice(0, 16)
				const d = new Date(Number(s))
				if (isNaN(d.getTime())) return ''
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2) +
					' ' + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2)
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
	.rd-page {
		padding-bottom: 60rpx;
	}

	.rd-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.rd-mt {
		margin-top: 24rpx;
	}

	.rd-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		padding: 36rpx;
		box-shadow: 0 12rpx 30rpx rgba(59, 130, 246, .25);
	}

	.rd-hero-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.rd-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.rd-state {
		color: #fff;
		font-size: 18rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 6rpx 18rpx;
	}

	.rd-label {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 20rpx;
	}

	.rd-fee {
		display: block;
		color: #fff;
		font-size: 56rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.rd-period {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 14rpx;
	}

	.rd-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.rd-info-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.rd-info-val {
		font-size: 22rpx;
		font-weight: 600;
		color: #334155;
	}

	.rd-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 20rpx 24rpx;
	}
</style>
