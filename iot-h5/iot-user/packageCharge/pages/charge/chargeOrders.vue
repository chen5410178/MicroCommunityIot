<template>
	<view class="co-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 在充订单标记 -->
		<view class="co-running-badge" v-if="runningOrder" @click="openRunningOrder">
			<view class="co-rb-head">
				<view class="co-rb-title-row">
					<view class="c-pulse-dot" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
					<text class="co-rb-title">订单充电中 · {{ runningOrder.pile }}</text>
				</view>
				<text class="co-rb-slot">{{ rbSlotText }}</text>
			</view>
			<view class="co-rb-foot">
				<text class="co-rb-text">已充 {{ rbTime }} · 费用 ¥{{ rbFee }} · 点击查看</text>
				<text class="co-rb-link">在充订单 ›</text>
			</view>
		</view>

		<!-- 统计卡 -->
		<view class="c-card co-mx co-mt">
			<view class="co-stat-left">
				<view class="co-stat-ic">
					<image src="/static/h5/images/charge/ic-bolt.png" class="co-stat-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<view class="co-stat-title">
						<text class="c-title">{{ recTitle }}</text>
						<text class="co-stat-count">{{ list.length }}</text>
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

		<!-- 订单列表 -->
		<view class="co-list co-mx co-mt" v-if="list.length">
			<view class="c-card co-item" v-for="(r, i) in list" :key="i" @click="openOrder(i)">
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
		<view class="c-card co-empty" v-else>
			<image src="/static/h5/images/charge/ic-chart.png" class="co-empty-ic" mode="aspectFit"></image>
			<text class="c-text-sub" style="margin-top:16rpx;">暂无{{ rangeName }}{{ recType === 'car' ? '汽车' : recType === 'bike' ? '电瓶车' : '' }}充电订单</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'

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
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			recRange() {
				return chargeStore.recRange
			},
			recType() {
				return chargeStore.recType
			},
			rangeName() {
				const r = this.rangeOptions.find(x => x.val === this.recRange)
				return r ? r.name : '全部时间'
			},
			runningOrder() {
				return chargeStore.runningOrder
			},
			rbSlotText() {
				const r = this.runningOrder
				if (!r) return ''
				return (r.type === 'car' ? '枪 ' : '插座 ') + (r.slot || '—') + ' · ' + (r.type === 'car' ? '7kW 慢充' : '220W')
			},
			rbTime() {
				const sec = Math.max(0, Math.floor((Date.now() - chargeStore.chgStart) / 1000))
				return ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
			},
			rbFee() {
				return chargeStore.chgFee.toFixed(2)
			},
			list() {
				return this._rangeRecords().filter(r => this.recType === 'all' || r.type === this.recType)
			},
			recTitle() {
				const t = this.recRange === 'month' ? '本月订单' : this.recRange === 'all' ? '全部订单' : this.rangeName + '订单'
				return t + (this.recType !== 'all' ? ' · ' + (this.recType === 'car' ? '汽车' : '电瓶车') : '')
			},
			feeSum() {
				return this.list.reduce((s, r) => s + (r.fee || 0), 0)
			}
		},
		onShow() {
			// 充电中实时刷新在充标记
			if (chargeStore.runningOrder) {
				chargeStore.simulateTick()
			}
			if (this._timer) clearInterval(this._timer)
			this._timer = setInterval(() => {
				chargeStore.simulateTick()
			}, 1000)
		},
		onHide() {
			if (this._timer) clearInterval(this._timer)
		},
		onUnload() {
			if (this._timer) clearInterval(this._timer)
		},
		methods: {
			_rangeRecords() {
				const records = chargeStore.records
				const range = this.recRange
				if (range === 'month') return records.filter(r => this._isThisMonth(r.date))
				if (range === 'all') return records
				const now = new Date()
				const today0 = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
				const limit = range === 'today' ? 0 : range === '7d' ? 6 : 29
				return records.filter(r => {
					const t = this._parseDate(r.date).getTime()
					return t >= today0 - limit * 86400000
				})
			},
			_isThisMonth(dateStr) {
				if (!dateStr) return false
				if (dateStr.indexOf('今天') === 0 || dateStr.indexOf('昨天') === 0) return true
				const m = /^(\d{2})-/.exec(dateStr)
				return m ? +m[1] === new Date().getMonth() + 1 : true
			},
			_parseDate(dateStr) {
				const now = new Date()
				let m = /^今天 (\d+):(\d+)/.exec(dateStr)
				if (m) {
					const d = new Date(now)
					d.setHours(+m[1], +m[2], 0, 0)
					return d
				}
				m = /^昨天 (\d+):(\d+)/.exec(dateStr)
				if (m) {
					const d = new Date(now)
					d.setDate(d.getDate() - 1)
					d.setHours(+m[1], +m[2], 0, 0)
					return d
				}
				m = /^(\d{2})-(\d{2}) (\d+):(\d+)/.exec(dateStr)
				if (m) return new Date(2026, +m[1] - 1, +m[2], +m[3], +m[4], 0, 0)
				return now
			},
			setRange(val) {
				chargeStore.recRange = val
				uni.setStorageSync('charge_rec_range', val)
				this.rangeMenu = false
				this._toast('已按「' + this.rangeName + '」筛选')
			},
			setType(t) {
				chargeStore.recType = t
				uni.setStorageSync('charge_rec_type', t)
			},
			openOrder(i) {
				const r = this.list[i]
				const realIdx = chargeStore.records.indexOf(r)
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?orderIdx=' + realIdx
				})
			},
			openRunningOrder() {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?mode=running'
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

	.co-stat-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 26rpx;
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
		padding: 0 26rpx 26rpx;
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
</style>
