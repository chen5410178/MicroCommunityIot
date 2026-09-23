<template>
	<view class="co-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 统计卡 -->
		<view class="c-card co-mx co-mt co-stat-card">
			<view class="co-stat-left">
				<view class="co-stat-ic">
					<image :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="co-stat-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<view class="co-stat-title">
						<text class="c-title">{{ recTitle }}</text>
						<text class="co-stat-count">{{ total }}</text>
						<text class="c-title">单</text>
					</view>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计消费 ¥{{ feeSum.toFixed(2) }} · 在充订单点击可进入详情停止充电</text>
				</view>
			</view>
			<view class="co-range-wrap">
				<view class="co-range-btn" @click="rangeMenu = !rangeMenu">
					<text>{{ rangeName }}</text>
					<text class="co-arrow" :class="{ open: rangeMenu }">▾</text>
				</view>
				<view class="co-range-menu" v-if="rangeMenu">
					<view v-for="(r, k) in rangeOptions" :key="k" class="co-range-item" :class="{ on: recRange === r.val }"
						@click="setRange(r.val)">
						<text>{{ r.name }}</text>
						<text class="co-range-check" v-if="recRange === r.val">✓</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 类型筛选 -->
		<view class="co-filter co-mx co-mt">
			<view class="c-rec-fbtn" :class="{ active: recType === 'all' }" @click="setType('all')">全部</view>
			<view class="c-rec-fbtn" :class="{ active: recType === 'bike' }" @click="setType('bike')">电瓶车</view>
			<view class="c-rec-fbtn" :class="{ active: recType === 'car' }" @click="setType('car')">汽车</view>
		</view>

		<!-- 订单列表 -->
		<view class="co-list co-mx co-mt" v-if="orders.length">
			<view class="c-card co-item" v-for="(r, i) in orders" :key="r.orderId || i" @click="openOrder(i)">
				<view class="co-item-head">
					<view class="co-item-ic">
						<image v-if="r.type === 'car'" :src="imgUrl + '/h5/images/charge/ic-car.png'" class="co-item-ic-img" mode="aspectFit"></image>
						<image v-else :src="imgUrl + '/h5/images/charge/ic-bike.png'" class="co-item-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="co-item-name-row">
							<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;">{{ r.pile }}</text>
							<text class="co-item-type" :class="r.type === 'car' ? 'car' : 'bike'">{{ r.type === 'car' ? '汽车' : '电瓶车' }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ r.date }} · {{ r.way }} · {{ r.mins }} 分钟{{ r.kwh ? ' · 用电 ' + r.kwh.toFixed(2) + ' 度' : '' }}</text>
					</view>
					<view style="text-align:right;flex-shrink:0;">
						<view class="co-charging-tag" v-if="r.state === '1001'">
							<view class="c-pulse-dot" style="width:12rpx;height:12rpx;border-radius:50%;background:#059669;"></view>
							<text>充电中</text>
						</view>
						<text class="co-item-fee" :class="r.fee > 0 ? 'pay' : 'free'">{{ r.state === '1001' ? '点击查看详情 ›' : (r.fee > 0 ? '-¥' + r.fee.toFixed(2) : '免费') }}</text>
						<text class="c-text-sub" style="display:block;margin-top:4rpx;">{{ r.st }}</text>
					</view>
				</view>
			</view>
		</view>
		<view class="c-card co-empty co-mx co-mt" v-else>
			<image :src="imgUrl + '/h5/images/charge/ic-chart.png'" class="co-empty-ic" mode="aspectFit"></image>
			<text class="c-text-sub" style="margin-top:16rpx;">{{ loading ? '订单加载中…' : '暂无' + rangeName + (recType === 'car' ? '汽车' : recType === 'bike' ? '电瓶车' : '') + '充电订单' }}</text>
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

	export default {
		data() {
			return {
				rangeOptions: [{
					name: '全部时间',
					val: 'all'
				}, {
					name: '今天',
					val: 'today'
				}, {
					name: '最近 7 天',
					val: '7d'
				}, {
					name: '最近 30 天',
					val: '30d'
				}, {
					name: '本月',
					val: 'month'
				}],
				rangeMenu: false,
				toastMsg: '',
				recRange: chargeStore.recRange || 'all',
				recType: chargeStore.recType || 'all',
				orders: [],
				total: 0,
				page: 1,
				loading: false
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			rangeName() {
				const r = this.rangeOptions.find(x => x.val === this.recRange)
				return r ? r.name : '全部时间'
			},
			recTitle() {
				const t = this.recRange === 'month' ? '本月订单' : this.recRange === 'all' ? '全部订单' : this.rangeName + '订单'
				return t + (this.recType !== 'all' ? ' · ' + (this.recType === 'car' ? '汽车' : '电瓶车') : '')
			},
			feeSum() {
				return this.orders.reduce((s, r) => s + (r.fee || 0), 0)
			}
		},
		onShow() {
			this._loadOrders()
		},
		methods: {
			// 时间范围 → 接口 queryStartTime（yyyy-MM-dd HH:mm:ss），全部时间不传
			_rangeStart(range) {
				const now = new Date()
				const fmt = d => {
					return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2) +
						' ' + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2) + ':' + ('0' + d.getSeconds())
						.slice(-2)
				}
				if (range === 'today') {
					const d = new Date(now)
					d.setHours(0, 0, 0, 0)
					return fmt(d)
				}
				if (range === '7d') {
					const d = new Date(now)
					d.setDate(d.getDate() - 7)
					d.setHours(0, 0, 0, 0)
					return fmt(d)
				}
				if (range === '30d') {
					const d = new Date(now)
					d.setDate(d.getDate() - 30)
					d.setHours(0, 0, 0, 0)
					return fmt(d)
				}
				if (range === 'month') {
					const d = new Date(now.getFullYear(), now.getMonth(), 1)
					d.setHours(0, 0, 0, 0)
					return fmt(d)
				}
				return ''
			},
			_buildQueryParams() {
				const p = {
					page: this.page,
					row: 100
				}
				const start = this._rangeStart(this.recRange)
				if (start) p.queryStartTime = start
				const ct = this.recType === 'car' ? '1002' : this.recType === 'bike' ? '1001' : ''
				if (ct) p.chargeType = ct
				return p
			},
			_loadOrders() {
				if (!hasLogin()) {
					this.orders = []
					this.total = 0
					this.loading = false
					return
				}
				this.loading = true
				queryPhoneChargeOrder(this._buildQueryParams()).then(res => {
					this.orders = adaptOrderList(res.data)
					this.total = res.records || this.orders.length
					this.loading = false
				}).catch(() => {
					this.orders = []
					this.total = 0
					this.loading = false
					this._toast('订单加载失败')
				})
			},
			setRange(val) {
				this.recRange = val
				chargeStore.recRange = val
				uni.setStorageSync('charge_rec_range', val)
				this.rangeMenu = false
				this.page = 1
				this._loadOrders()
				this._toast('已按「' + this.rangeName + '」筛选')
			},
			setType(t) {
				this.recType = t
				chargeStore.recType = t
				uni.setStorageSync('charge_rec_type', t)
				this.page = 1
				this._loadOrders()
			},
			// 按「状态 + 车型」分流：充电中按两轮/四轮分开，其余进已完成详情
			openOrder(i) {
				const r = this.orders[i]
				if (!r || !r.orderId) return
				const type = r.type === 'car' ? 'car' : 'bike'
				const chargeType = type === 'car' ? '1002' : '1001'
				let path
				if (r.state === '1001') {
					path = type === 'car' ?
						'/packageCharge/pages/charge/carChargingDetail' :
						'/packageCharge/pages/charge/bikeChargingDetail'
				} else {
					path = '/packageCharge/pages/charge/orderDone'
				}
				uni.navigateTo({
					url: path + '?orderId=' + r.orderId + '&chargeType=' + chargeType
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
	.co-page {
		padding-bottom: 60rpx;
	}

	.co-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.co-mt {
		margin-top: 24rpx;
	}

	.co-stat-card {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.co-stat-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 26rpx;
		flex: 1;
		min-width: 0;
	}

	.co-stat-ic {
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.co-stat-ic-img {
		width: 34rpx;
		height: 34rpx;
	}

	.co-stat-title {
		display: flex;
		align-items: center;
		gap: 6rpx;
	}

	.co-stat-count {
		color: #059669;
		font-size: 26rpx;
		font-weight: 700;
	}

	.co-range-wrap {
		position: relative;
		padding: 0 26rpx;
		flex-shrink: 0;
	}

	.co-range-btn {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 20rpx;
		font-weight: 600;
		color: #059669;
		background: rgba(16, 185, 129, .08);
		border: 1rpx solid rgba(16, 185, 129, .25);
		border-radius: 999rpx;
		padding: 10rpx 24rpx;
		align-self: flex-start;
	}

	.co-arrow {
		font-size: 18rpx;
		transition: transform .2s;
	}

	.co-arrow.open {
		transform: rotate(180deg);
	}

	.co-range-menu {
		position: absolute;
		right: 26rpx;
		top: 72rpx;
		z-index: 100;
		width: 280rpx;
		background: #fff;
		border-radius: 20rpx;
		box-shadow: 0 10rpx 40rpx rgba(15, 23, 42, .15);
		padding: 12rpx 0;
	}

	.co-range-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 28rpx;
		font-size: 22rpx;
		font-weight: 600;
		color: #475569;
	}

	.co-range-item.on {
		color: #059669;
	}

	.co-range-check {
		color: #059669;
		font-size: 20rpx;
	}

	.co-filter {
		display: flex;
		gap: 16rpx;
	}

	.co-filter .c-rec-fbtn {
		flex: 1;
	}

	.co-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.co-item {
		padding: 26rpx;
	}

	.co-item .c-text-sub {
		font-size: 24rpx;
	}

	.co-item-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.co-item-ic {
		width: 72rpx;
		height: 72rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.co-item-ic-img {
		width: 36rpx;
		height: 36rpx;
	}

	.co-item-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.co-item-type {
		font-size: 18rpx;
		font-weight: 600;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.co-item-type.bike {
		background: rgba(16, 185, 129, .1);
		color: #059669;
	}

	.co-item-type.car {
		background: rgba(59, 130, 246, .1);
		color: #3b82f6;
	}

	.co-item-fee {
		font-size: 28rpx;
		font-weight: 700;
	}

	.co-item-fee.pay {
		color: #0f172a;
	}

	.co-item-fee.free {
		color: #059669;
	}

	.co-charging-tag {
		display: inline-flex;
		align-items: center;
		gap: 8rpx;
		font-size: 20rpx;
		font-weight: 700;
		color: #059669;
		background: rgba(16, 185, 129, .1);
		border: 1rpx solid rgba(16, 185, 129, .3);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
		margin-bottom: 8rpx;
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
