<template>
	<view class="co-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 在充订单标记（接口查询 state=1001） -->
		<view class="co-running-badge" v-if="runningCard" @click="openRunningOrder">
			<view class="co-rb-head">
				<view class="co-rb-title-row">
					<view class="c-pulse-dot" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
					<text class="co-rb-title">订单充电中 · {{ runningCard.pile }}</text>
				</view>
				<text class="co-rb-slot">{{ rbSlotText }}</text>
			</view>
			<view class="co-rb-foot">
				<text class="co-rb-text">已充 {{ rbTime }} · 费用 ¥{{ rbFee }} · 点击查看</text>
				<text class="co-rb-link">在充订单 ›</text>
			</view>
		</view>

		<!-- 统计卡 -->
		<view class="c-card co-mx co-mt co-stat-card">
			<view class="co-stat-left">
				<view class="co-stat-ic">
					<image src="/static/h5/images/charge/ic-bolt.png" class="co-stat-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<view class="co-stat-title">
						<text class="c-title">{{ recTitle }}</text>
						<text class="co-stat-count">{{ total }}</text>
						<text class="c-title">单</text>
					</view>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计消费 ¥{{ feeSum.toFixed(2) }} · 在充订单可进入停止充电</text>
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

		<!-- 在充订单置顶大卡片（接口 state=1001） -->
		<view class="co-running-card co-mx co-mt" v-if="runningCard" @click="openRunningOrder">
			<view class="co-rc-head">
				<view class="co-rc-title-row">
					<view class="c-pulse-dot" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
					<text class="co-rc-title">充电中 · {{ runningCard.pile }}</text>
					<text class="co-rc-slot" v-if="runningCard.slot">{{ runningCard.type === 'car' ? '枪 ' : '插座 ' }}{{ runningCard.slot }}</text>
				</view>
				<text class="co-rc-power-tag">{{ runningCard.type === 'car' ? '7kW 慢充' : '220W' }}</text>
			</view>
			<view class="co-rc-stats">
				<view class="co-rc-stat">
					<text class="co-rc-stat-label">已充时长</text>
					<text class="co-rc-stat-val">{{ rbTime }}</text>
				</view>
				<view class="co-rc-stat">
					<text class="co-rc-stat-label">当前功率</text>
					<text class="co-rc-stat-val">{{ runningCard.type === 'car' ? '7.0kW' : '220W' }}</text>
				</view>
				<view class="co-rc-stat">
					<text class="co-rc-stat-label">已充费用</text>
					<text class="co-rc-stat-val">¥{{ runningCard.fee.toFixed(2) }}</text>
				</view>
			</view>
			<view class="co-rc-foot">
				<text class="co-rc-note">点击查看详情 · 以结束结算为准</text>
				<text class="co-rc-link">停止充电 ›</text>
			</view>
		</view>

		<!-- 订单列表 -->
		<view class="co-list co-mx co-mt" v-if="orders.length">
			<view class="c-card co-item" v-for="(r, i) in orders" :key="r.orderId || i" @click="openOrder(i)">
				<view class="co-item-head">
					<view class="co-item-ic">
						<image v-if="r.type === 'car'" src="/static/h5/images/charge/ic-car.png" class="co-item-ic-img" mode="aspectFit"></image>
						<image v-else src="/static/h5/images/charge/ic-bike.png" class="co-item-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="co-item-name-row">
							<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ r.pile }}</text>
							<text class="co-item-type" :class="r.type === 'car' ? 'car' : 'bike'">{{ r.type === 'car' ? '汽车' : '电瓶车' }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ r.date }} · {{ r.way }} · {{ r.mins }} 分钟{{ r.kwh ? ' · 用电 ' + r.kwh.toFixed(2) + ' 度' : '' }}</text>
					</view>
					<view style="text-align:right;">
						<text class="co-item-fee" :class="r.fee > 0 ? 'pay' : 'free'">{{ r.fee > 0 ? '-¥' + r.fee.toFixed(2) : '免费' }}</text>
						<text class="c-text-sub" style="display:block;margin-top:4rpx;">{{ r.st }}</text>
					</view>
				</view>
			</view>
		</view>
		<view class="c-card co-empty co-mx co-mt" v-else>
			<image src="/static/h5/images/charge/ic-chart.png" class="co-empty-ic" mode="aspectFit"></image>
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
				loading: false,
				runningCard: null,
				tick: 0
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
			rbSlotText() {
				const r = this.runningCard
				if (!r) return ''
				return (r.type === 'car' ? '枪 ' : '插座 ') + (r.slot || '—') + ' · ' + (r.type === 'car' ? '7kW 慢充' : '220W')
			},
			rbTime() {
				this.tick
				const s = this.runningCard && this.runningCard.startTime
				if (!s) return '00:00'
				const t = new Date(String(s).replace(/-/g, '/')).getTime()
				const sec = isNaN(t) ? 0 : Math.max(0, Math.floor((Date.now() - t) / 1000))
				return ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
			},
			rbFee() {
				const r = this.runningCard
				return r ? r.fee.toFixed(2) : '0.00'
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
			this._loadRunning()
			if (this._timer) clearInterval(this._timer)
			this._timer = setInterval(() => {
				// 仅刷新在充卡片「已充时长」，不产生本地假数据
				this.tick++
			}, 1000)
		},
		onHide() {
			if (this._timer) clearInterval(this._timer)
		},
		onUnload() {
			if (this._timer) clearInterval(this._timer)
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
			// 在充订单（置顶大卡片 + 顶部横幅）
			_loadRunning() {
				if (!hasLogin()) {
					this.runningCard = null
					return
				}
				queryPhoneChargeOrder({
					page: 1,
					row: 10,
					state: '1001'
				}).then(res => {
					const list = adaptOrderList(res.data)
					this.runningCard = list.length ? list[0] : null
				}).catch(() => {
					this.runningCard = null
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
			openOrder(i) {
				const r = this.orders[i]
				if (!r || !r.orderId) return
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?orderId=' + r.orderId
				})
			},
			openRunningOrder() {
				const r = this.runningCard
				if (!r) return
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?mode=running&orderId=' + r.orderId
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

	.co-running-badge {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 26rpx;
		color: #fff;
		box-shadow: 0 10rpx 24rpx rgba(16, 185, 129, .25);
	}

	.co-rb-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.co-rb-title-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.co-rb-title {
		font-size: 24rpx;
		font-weight: 700;
	}

	.co-rb-slot {
		font-size: 18rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.co-rb-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 16rpx;
	}

	.co-rb-text {
		font-size: 20rpx;
		color: rgba(255, 255, 255, .85);
	}

	.co-rb-link {
		font-size: 20rpx;
		font-weight: 700;
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
		font-size: 16rpx;
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
		font-size: 24rpx;
		font-weight: 700;
	}

	.co-item-fee.pay {
		color: #0f172a;
	}

	.co-item-fee.free {
		color: #059669;
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

	/* 在充订单置顶大卡片 */
	.co-running-card {
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 28rpx;
		color: #fff;
		box-shadow: 0 10rpx 24rpx rgba(16, 185, 129, .25);
	}

	.co-rc-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.co-rc-title-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
		flex: 1;
		min-width: 0;
	}

	.co-rc-title {
		font-size: 24rpx;
		font-weight: 700;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.co-rc-slot {
		font-size: 18rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 4rpx 14rpx;
		white-space: nowrap;
	}

	.co-rc-power-tag {
		font-size: 18rpx;
		font-weight: 600;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
		white-space: nowrap;
	}

	.co-rc-stats {
		display: flex;
		gap: 12rpx;
		margin-top: 24rpx;
	}

	.co-rc-stat {
		flex: 1;
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 16rpx 0;
		text-align: center;
	}

	.co-rc-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 16rpx;
	}

	.co-rc-stat-val {
		display: block;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	.co-rc-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.co-rc-note {
		font-size: 20rpx;
		color: rgba(255, 255, 255, .8);
	}

	.co-rc-link {
		font-size: 20rpx;
		font-weight: 700;
	}
</style>
