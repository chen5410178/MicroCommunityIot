<template>
	<view class="cm-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 我的月卡 -->
		<view class="cm-my-card">
			<view class="cm-my-head">
				<text class="cm-my-title">我的充电月卡</text>
				<text class="cm-my-sub">开通后充电更省钱</text>
			</view>
			<template v-if="myCard">
				<view class="cm-my-content">
					<view>
						<text class="cm-my-name">{{ myCard.name }}</text>
						<text class="cm-my-expire">{{ myCard.type === 'month' ? '到期 ' + myCard.expire : '有效期至 ' + myCard.expire }}</text>
					</view>
					<view class="cm-my-left">
						<text class="cm-my-left-num">{{ myCard.left }}</text>
						<text class="cm-my-left-unit">{{ myCard.type === 'month' ? '天剩余' : '次剩余' }}</text>
					</view>
				</view>
			</template>
			<template v-else>
				<view class="cm-my-content">
					<view>
						<text class="cm-my-name" style="font-size:22rpx;color:rgba(255,255,255,.7);font-weight:400;">暂未开通月卡 · 开通后充电更省钱</text>
						<text class="cm-my-expire" style="margin-top:8rpx;">推荐 <text style="font-weight:700;">畅充月卡 ¥29.9</text>，30 天不限次数，每天首充免 4 小时</text>
					</view>
				</view>
			</template>
		</view>

		<!-- 三段 Tab -->
		<view class="cm-tab-bar">
			<view class="c-mtab-btn" :class="{ active: tab === 'plans' }" @click="switchTab('plans')">月卡套餐</view>
			<view class="c-mtab-btn" :class="{ active: tab === 'orders' }" @click="switchTab('orders')">月卡订单</view>
			<view class="c-mtab-btn" :class="{ active: tab === 'records' }" @click="switchTab('records')">使用记录</view>
		</view>

		<!-- 月卡套餐 -->
		<template v-if="tab === 'plans'">
			<view class="cm-sec-head">
				<text class="c-title">{{ planFilter ? '续费套餐 · ' + (planFilter === 'month' ? '畅充月卡' : '次卡') : '月卡套餐' }}</text>
				<text class="c-text-sub">{{ planFilter ? '仅显示当前月卡同类型套餐' : '全部套餐可用余额 / 微信支付' }}</text>
			</view>
			<view class="cm-plans">
				<view class="c-card cm-plan" v-for="(p, i) in showPlans" :key="i">
					<view class="cm-plan-ic" :style="{ background: p.color }">
						<text style="font-size:28rpx;">📇</text>
						<text class="cm-plan-ic-name">{{ p.name.replace(' · ', '') }}</text>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="cm-plan-name-row">
							<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">{{ p.name }}</text>
							<text class="cm-plan-tag" v-if="p.tag">{{ p.tag }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ p.desc }}</text>
					</view>
					<view style="text-align:right;">
						<text class="c-text-bold" style="font-size:32rpx;font-weight:700;color:#0f172a;">¥<text style="font-size:24rpx;">{{ p.price }}</text></text>
						<view class="cm-buy-btn" @click="buyPlan(i)">{{ myCard && planFilter ? '续费' : '立即开通' }}</view>
					</view>
				</view>
			</view>
			<view class="cm-rule-tip">
				<text style="font-size:20rpx;line-height:1.6;color:#d97706;">月卡规则：畅充月卡每日首次充电前 4 小时免费，超出部分按 ¥0.25/小时计费；次卡按次抵扣，单次上限 8 小时。<text style="font-weight:600;">月卡仅适用于电瓶车充电桩，电动车充电请使用余额按电量计费。</text></text>
			</view>
		</template>

		<!-- 月卡订单 -->
		<template v-else-if="tab === 'orders'">
			<view class="c-card cm-stat-card">
				<view class="cm-stat-ic">📇</view>
				<view>
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">月卡订单 <text style="color:#f97316;">{{ cardOrders.length }}</text> 笔</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计购买 <text style="color:#f97316;font-weight:600;">¥{{ cardOrdersSum.toFixed(2) }}</text></text>
				</view>
				<text class="c-text-sub">购买记录</text>
			</view>
			<view class="cm-orders">
				<view class="c-card cm-order" v-for="(o, i) in cardOrders" :key="i">
					<view class="cm-order-ic">📇</view>
					<view style="flex:1;min-width:0;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ o.name }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.date }}</text></text>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ o.type === 'month' ? '30 天有效' : '次卡' }} · 有效期至 {{ o.expire }}</text>
					</view>
					<view style="text-align:right;">
						<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">-¥{{ o.price.toFixed(2) }}</text>
						<text class="c-text-sub" style="display:block;margin-top:4rpx;" :style="{ color: o.st === '生效中' ? '#059669' : '#cbd5e1' }">{{ o.st }}</text>
					</view>
				</view>
			</view>
			<text class="cm-foot-tip">月卡购买记录 · 购买成功后自动生成</text>
		</template>

		<!-- 使用记录 -->
		<template v-else>
			<view class="c-card cm-stat-card">
				<view class="cm-stat-ic" style="background:rgba(16,185,129,.1);color:#059669;">⚡</view>
				<view>
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">月卡使用 <text style="color:#059669;">{{ cardRecords.length }}</text> 笔</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计充电 <text style="color:#059669;font-weight:600;">{{ cardMins }}</text> 分钟</text>
				</view>
				<text class="c-text-sub">免费充电</text>
			</view>
			<view class="cm-orders">
				<view class="c-card cm-order" v-for="(r, i) in cardRecords" :key="i" @click="openOrder(r)">
					<view class="cm-order-ic" style="background:rgba(16,185,129,.1);color:#059669;">⚡</view>
					<view style="flex:1;min-width:0;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ r.pile }} <text class="c-text-sub" style="font-size:18rpx;">{{ r.date }}</text></text>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ r.mins }} 分钟{{ r.kwh ? ' · 用电 ' + r.kwh.toFixed(2) + ' 度' : '' }}</text>
					</view>
					<view style="text-align:right;">
						<text class="cm-free-tag" :class="r.way === '畅充月卡' ? 'month' : 'times'">{{ r.way }}</text>
						<text class="cm-free-text">免费</text>
					</view>
				</view>
			</view>
			<text class="cm-foot-tip">仅显示使用月卡 / 次卡抵扣的充电订单</text>
		</template>

		<!-- 微信支付弹窗 -->
		<view class="c-mask" v-if="payShow" @click="payShow = false"></view>
		<view class="c-dialog" v-if="payShow" style="padding:0;overflow:hidden;">
			<view class="cm-pay-head">
				<text class="cm-pay-title">微信支付</text>
				<text class="cm-pay-amount">¥{{ payPlan.price.toFixed(2) }}</text>
				<text class="cm-pay-desc">购买「{{ payPlan.name }}」</text>
			</view>
			<view style="padding:32rpx;">
				<view class="cm-pay-row">
					<view class="cm-pay-ic">📞</view>
					<view style="flex:1;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;display:block;">微信支付</text>
						<text class="c-text-sub">使用零钱支付</text>
					</view>
					<text style="color:#10b981;font-size:26rpx;font-weight:700;">✓</text>
				</view>
				<view class="c-btn-primary" @click="confirmPay">确认支付</view>
				<text class="cm-pay-demo-tip">演示环境 · 点击确认支付即完成</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		mockPlans
	} from '@/api/mock/chargeMockData.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		data() {
			return {
				tab: 'plans',
				plans: mockPlans,
				payShow: false,
				payPlan: null,
				payPlanIdx: -1,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			myCard() {
				return chargeStore.myCard
			},
			planFilter() {
				return chargeStore.planFilter
			},
			showPlans() {
				if (!this.planFilter) return this.plans
				return this.plans.filter(p => this.planFilter === 'month' ? p.name === '畅充月卡' : p.name.indexOf('次卡') === 0)
			},
			cardOrders() {
				return chargeStore.cardOrders
			},
			cardOrdersSum() {
				return this.cardOrders.reduce((s, o) => s + o.price, 0)
			},
			cardRecords() {
				return chargeStore.records.filter(r => r.way !== '余额')
			},
			cardMins() {
				return this.cardRecords.reduce((s, r) => s + (r.mins || 0), 0)
			}
		},
		onLoad(options) {
			// 进入默认展示套餐 Tab；支持 ?tab=orders 直达月卡订单
			this.tab = options.tab || 'plans'
		},
		methods: {
			switchTab(t) {
				this.tab = t
			},
			buyPlan(i) {
				if (!hasLogin()) {
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const realIdx = this.plans.indexOf(this.showPlans[i])
				this.payPlan = this.plans[realIdx]
				this.payPlanIdx = realIdx
				this.payShow = true
			},
			confirmPay() {
				const p = this.plans[this.payPlanIdx]
				const d = new Date()
				d.setDate(d.getDate() + (this.payPlanIdx === 0 ? 30 : (this.payPlanIdx === 1 ? 90 : 180)))
				chargeStore.setMyCard({
					name: p.name,
					type: this.payPlanIdx === 0 ? 'month' : 'times',
					left: this.payPlanIdx === 0 ? 30 : (this.payPlanIdx === 1 ? 10 : 30),
					expire: (d.getMonth() + 1) + '月' + d.getDate() + '日'
				})
				chargeStore.planFilter = null
				chargeStore.cardOrders.unshift({
					name: p.name,
					price: p.price,
					type: this.payPlanIdx === 0 ? 'month' : 'times',
					date: '今天 ' + new Date().getHours() + ':' + ('0' + new Date().getMinutes()).slice(-2),
					expire: chargeStore.myCard.expire,
					st: '生效中'
				})
				chargeStore.saveCardOrders()
				this.payShow = false
				this._toast('已开通「' + p.name + '」· 立即生效')
				chargeStore.addMsg('card', '月卡购买成功', '已开通「' + p.name + '」¥' + p.price.toFixed(2) + ' · 有效期至 ' + chargeStore.myCard
					.expire + ' · 已立即生效，充电自动使用月卡抵扣。')
			},
			openOrder(r) {
				const realIdx = chargeStore.records.indexOf(r)
				if (realIdx >= 0) {
					uni.navigateTo({
						url: '/packageCharge/pages/charge/orderDetail?orderIdx=' + realIdx
					})
				}
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
	.cm-page {
		padding-bottom: 60rpx;
	}

	.cm-my-card {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #f59e0b, #f97316);
		padding: 28rpx;
	}

	.cm-my-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.cm-my-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.cm-my-sub {
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.cm-my-content {
		display: flex;
		align-items: flex-end;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.cm-my-name {
		display: block;
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
	}

	.cm-my-expire {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 4rpx;
	}

	.cm-my-left {
		display: flex;
		align-items: baseline;
	}

	.cm-my-left-num {
		color: #fff;
		font-size: 48rpx;
		font-weight: 700;
		line-height: 1;
	}

	.cm-my-left-unit {
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-left: 8rpx;
	}

	.cm-tab-bar {
		margin: 24rpx 28rpx 0;
		background: #f1f5f9;
		border-radius: 20rpx;
		padding: 8rpx;
		display: flex;
	}

	.cm-tab-bar .c-mtab-btn {
		flex: 1;
		height: 64rpx;
		line-height: 64rpx;
		text-align: center;
		font-size: 22rpx;
		font-weight: 700;
		border-radius: 16rpx;
	}

	.cm-sec-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 28rpx 28rpx 16rpx;
	}

	.cm-plans {
		margin: 0 28rpx;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.cm-plan {
		display: flex;
		align-items: center;
		gap: 24rpx;
		padding: 28rpx;
	}

	.cm-plan-ic {
		width: 104rpx;
		height: 104rpx;
		border-radius: 24rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 4rpx;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, .08);
	}

	.cm-plan-ic-name {
		color: #fff;
		font-size: 14rpx;
		font-weight: 700;
	}

	.cm-plan-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.cm-plan-tag {
		font-size: 16rpx;
		color: #d97706;
		background: rgba(249, 115, 22, .08);
		border: 1rpx solid rgba(249, 115, 22, .25);
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.cm-buy-btn {
		margin-top: 10rpx;
		font-size: 20rpx;
		font-weight: 700;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 8rpx 26rpx;
		display: inline-block;
	}

	.cm-rule-tip {
		margin: 24rpx 28rpx 0;
		border-radius: 20rpx;
		background: rgba(249, 115, 22, .08);
		border: 1rpx solid rgba(249, 115, 22, .15);
		padding: 20rpx 24rpx;
	}

	.cm-stat-card {
		margin: 24rpx 28rpx 0;
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 26rpx;
	}

	.cm-stat-ic {
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		background: rgba(249, 115, 22, .1);
		color: #f97316;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
	}

	.cm-orders {
		margin: 20rpx 28rpx 0;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.cm-order {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 26rpx;
	}

	.cm-order-ic {
		width: 72rpx;
		height: 72rpx;
		border-radius: 20rpx;
		background: rgba(249, 115, 22, .1);
		color: #f97316;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
	}

	.cm-free-tag {
		font-size: 16rpx;
		font-weight: 600;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.cm-free-tag.month {
		background: rgba(249, 115, 22, .1);
		color: #f97316;
	}

	.cm-free-tag.times {
		background: rgba(59, 130, 246, .1);
		color: #3b82f6;
	}

	.cm-free-text {
		display: block;
		margin-top: 4rpx;
		font-size: 20rpx;
		font-weight: 700;
		color: #059669;
		text-align: right;
	}

	.cm-foot-tip {
		display: block;
		text-align: center;
		color: #cbd5e1;
		font-size: 20rpx;
		margin-top: 28rpx;
	}

	.cm-pay-head {
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 28rpx;
	}

	.cm-pay-title {
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
	}

	.cm-pay-amount {
		display: block;
		color: #fff;
		font-size: 44rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.cm-pay-desc {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 4rpx;
	}

	.cm-pay-row {
		display: flex;
		align-items: center;
		gap: 20rpx;
		margin-bottom: 28rpx;
	}

	.cm-pay-ic {
		width: 80rpx;
		height: 80rpx;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
	}

	.cm-pay-demo-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 20rpx;
		margin-top: 16rpx;
	}
</style>
