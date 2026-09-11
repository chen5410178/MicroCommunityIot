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
						<text class="cm-my-expire" style="margin-top:8rpx;">开通后每日享免费充电时长 · 购买立即生效</text>
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
				<text class="c-title">{{ planFilter ? '续费套餐 · ' + (planFilter === 'month' ? '月卡' : '次卡') : '月卡套餐' }}</text>
				<text class="c-text-sub">{{ planFilter ? '仅显示当前月卡同类型套餐' : '全部套餐微信支付购买' }}</text>
			</view>
			<view class="cm-plans">
				<view class="c-card cm-plan" v-for="(p, i) in showPlans" :key="i">
					<view class="cm-plan-ic" :style="{ background: p.color }">
						<image :src="imgUrl + '/h5/images/charge/ic-calendar-white.png'" class="cm-plan-ic-img" mode="aspectFit"></image>
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
			<view class="c-card cm-empty" v-if="!showPlans.length">
				<text class="c-text-sub">暂无可购买套餐</text>
			</view>
			<view class="cm-rule-tip">
				<text style="font-size:20rpx;line-height:1.6;color:#d97706;">月卡规则：按月开通，有效期内每日享免费充电时长，超出部分按电费 + 服务费计费。<text style="font-weight:600;">月卡仅适用于电瓶车充电桩，电动车充电请使用余额按电量计费。</text></text>
			</view>
		</template>

		<!-- 月卡订单 -->
		<template v-else-if="tab === 'orders'">
			<view class="c-card cm-stat-card">
				<view class="cm-stat-ic">
					<image :src="imgUrl + '/h5/images/charge/ic-card.png'" class="cm-stat-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">月卡订单 <text style="color:#f97316;">{{ cardOrders.length }}</text> 笔</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计购买 <text style="color:#f97316;font-weight:600;">¥{{ cardOrdersSum.toFixed(2) }}</text></text>
				</view>
				<text class="c-text-sub">购买记录</text>
			</view>
			<view class="cm-orders">
				<view class="c-card cm-order" v-for="(o, i) in cardOrders" :key="i">
					<view class="cm-order-ic">
						<image :src="imgUrl + '/h5/images/charge/ic-card.png'" class="cm-order-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ o.name }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.date }}</text></text>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ o.startTime }} ~ {{ o.endTime }}</text>
					</view>
					<view style="text-align:right;">
						<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">-¥{{ o.price.toFixed(2) }}</text>
						<text class="c-text-sub" style="display:block;margin-top:4rpx;" :style="{ color: o.st === '生效中' ? '#059669' : '#cbd5e1' }">{{ o.st }}</text>
					</view>
				</view>
			</view>
			<view class="c-card cm-empty" v-if="!cardOrders.length">
				<text class="c-text-sub">暂无月卡订单 · 购买后展示</text>
			</view>
			<text class="cm-foot-tip">月卡购买记录 · 购买成功后自动生成</text>
		</template>

		<!-- 使用记录 -->
		<template v-else>
			<view class="c-card cm-stat-card">
				<view class="cm-stat-ic" style="background:rgba(16,185,129,.1);">
					<image :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="cm-stat-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">月卡使用 <text style="color:#059669;">{{ records.length }}</text> 笔</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">累计充电 <text style="color:#059669;font-weight:600;">{{ cardMins }}</text> 分钟</text>
				</view>
				<text class="c-text-sub">免费充电</text>
			</view>
			<view class="cm-orders">
				<view class="c-card cm-order" v-for="(r, i) in records" :key="i" @click="openOrder(r)">
					<view class="cm-order-ic" style="background:rgba(16,185,129,.1);">
						<image :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="cm-order-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ r.pile }} <text class="c-text-sub" style="font-size:18rpx;">{{ r.date }}</text></text>
						<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ r.mins }} 分钟{{ r.kwh ? ' · 用电 ' + r.kwh.toFixed(2) + ' 度' : '' }}</text>
					</view>
					<view style="text-align:right;">
						<text class="cm-free-tag month">{{ r.way }}</text>
						<text class="cm-free-text">免费</text>
					</view>
				</view>
			</view>
			<view class="c-card cm-empty" v-if="!records.length">
				<text class="c-text-sub">暂无月卡使用记录</text>
			</view>
			<text class="cm-foot-tip">仅显示使用月卡抵扣的充电订单</text>
		</template>

		<!-- 微信支付弹窗已移除：购买走 cashier 收银台真实支付 -->

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		listChargeMonthCard,
		listUserChargeMonthOrder,
		queryPhoneChargeOrder
	} from '@/packageCharge/api/chargeApi.js'
	import {
		adaptOrderList
	} from '@/api/charge/orderAdapter.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'
	import {
		getUserId,
		getUserTel
	} from '@/api/user/userApi.js'
	import {
		getWAppId
	} from '@/lib/java110/utils/StorageUtil.js'
	import {
		saveTargetUrl,
		buildCurrentPageUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js'

	export default {
		data() {
			return {
				tab: 'plans',
				themeDark: false,
				myCard: null,
				planFilter: null,
				plans: [],
				showPlans: [],
				cardOrders: [],
				cardOrdersSum: 0,
				records: [],
				cardMins: 0,
				toastMsg: ''
			}
		},
		onLoad(options) {
			// 进入默认展示套餐 Tab；支持 ?tab=orders 直达月卡订单
			this.tab = options.tab || 'plans'
			this.themeDark = chargeStore.themeDark
			this.planFilter = chargeStore.planFilter
			this.myCard = chargeStore.myCard
		},
		onShow() {
			if (!hasLogin()) {
				this.plans = []
				this.showPlans = []
				this.cardOrders = []
				this.cardOrdersSum = 0
				this.records = []
				this.cardMins = 0
				this.myCard = null
				return
			}
			// 顶部"我的月卡"（订单 tab 共用同一接口）：支付回调/返回本页时拉取回填
			this._loadMonthOrders()
			// 当前 tab 数据按需加载（未加载过才查；切换 tab 由 switchTab 主动查询）
			if (this.tab === 'plans' && !this.plans.length) this._loadPlans()
			else if (this.tab === 'records' && !this.records.length) this._loadRecords()
		},
		methods: {
			// 切换 tab 即查询对应接口
			switchTab(t) {
				this.tab = t
				if (t === 'plans') this._loadPlans()
				else if (t === 'orders') this._loadMonthOrders()
				else this._loadRecords()
			},
			// ===== 接口数据加载 =====
			_loadPlans() {
				listChargeMonthCard({
					page: 1,
					row: 100,
					communityId: getCommunityId()
				}).then(list => {
					this.plans = (list || []).map((c, i) => this._adaptPlan(c, i))
					this._calcShowPlans()
				}).catch(() => {
					this.plans = []
					this.showPlans = []
				})
			},
			_calcShowPlans() {
				this.showPlans = this.planFilter
					? this.plans.filter(p => this.planFilter === 'month' ? (p.cardMonth || 0) >= 1 : p.type === 'times')
					: this.plans
			},
			// 我的月卡 + 月卡订单（同一接口）
			_loadMonthOrders() {
				listUserChargeMonthOrder({
					communityId: getCommunityId()
				}).then(list => {
					const orders = list || []
					this.cardOrders = orders.map(o => this._adaptMonthOrder(o))
					this.cardOrdersSum = this.cardOrders.reduce((s, o) => s + o.price, 0)
					chargeStore.updateMyCardFromApi(orders)
					this.myCard = chargeStore.myCard
				}).catch(() => {
					this.cardOrders = []
					this.cardOrdersSum = 0
					chargeStore.setMyCard(null)
					this.myCard = null
				})
			},
			// 使用记录：过滤月卡抵扣订单（acctDetailId === '-1'）
			_loadRecords() {
				queryPhoneChargeOrder({
					page: 1,
					row: 50
				}).then(res => {
					this.records = adaptOrderList(res.data).filter(r => r.acctDetailId === '-1')
					this.cardMins = this.records.reduce((s, r) => s + (r.mins || 0), 0)
				}).catch(() => {
					this.records = []
					this.cardMins = 0
				})
			},
			// ===== 字段适配 =====
			_adaptPlan(c, i) {
				const colors = [
					'linear-gradient(135deg,#fb923c,#f59e0b)',
					'linear-gradient(135deg,#34d399,#10b981)',
					'linear-gradient(135deg,#818cf8,#6366f1)',
					'linear-gradient(135deg,#f472b6,#ec4899)'
				]
				const month = parseInt(c.cardMonth || 1) || 1
				return {
					id: c.cardId || '',
					name: c.cardName || '',
					price: parseFloat(c.cardPrice || 0) || 0,
					desc: month + ' 个月 · 每日 ' + (c.dayHours || 0) + ' 小时',
					tag: i === 0 ? '推荐' : '',
					color: colors[(month - 1) % colors.length],
					type: 'month',
					cardMonth: month,
					dayHours: c.dayHours || 0
				}
			},
			_adaptMonthOrder(o) {
				const end = new Date(String(o.endTime || '').replace(/-/g, '/')).getTime()
				return {
					orderId: o.orderId || '',
					name: o.cardName || '',
					price: parseFloat(o.receivedAmount || 0) || 0,
					date: this._fmtDate(o.createTime),
					startTime: this._fmtDateTime(o.startTime),
					endTime: this._fmtDateTime(o.endTime),
					st: !isNaN(end) && end > Date.now() ? '生效中' : '已过期'
				}
			},
			_fmtDate(t) {
				const s = String(t || '')
				const m = /^(\d{4})[-/](\d{2})[-/](\d{2})/.exec(s)
				if (m) return m[1] + '-' + m[2] + '-' + m[3]
				const d = new Date(typeof t === 'number' && t < 1e12 ? t * 1000 : t)
				if (isNaN(d.getTime())) return s
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2)
			},
			_fmtDateTime(v) {
				const s = String(v || '')
				const m = /^(\d{4})[-/](\d{2})[-/](\d{2})[ T](\d{2}):(\d{2})/.exec(s)
				if (m) return m[1] + '-' + m[2] + '-' + m[3] + ' ' + m[4] + ':' + m[5]
				return s
			},
			// ===== 购买/续费（真实微信支付：cashier 收银台 + preStoreChargeCard 回调） =====
			buyPlan(i) {
				if (!hasLogin()) {
					this._toast('请先登录')
					saveTargetUrl(buildCurrentPageUrl())
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const realIdx = this.plans.indexOf(this.showPlans[i])
				const p = this.plans[realIdx]
				if (!p) return
				let _appId = ''
				// #ifdef MP-WEIXIN
				_appId = uni.getAccountInfoSync().miniProgram.appId
				// #endif
				// #ifdef H5
				_appId = getWAppId()
				// #endif
				const _objData = {
					business: 'preStoreChargeCard',
					communityId: getCommunityId(),
					personTel: getUserTel(),
					feeName: '月卡充值',
					receivedAmount: p.price,
					cardId: p.id,
					tradeType: 'JSAPI',
					appId: _appId
				}
				uni.setStorageSync('doing_cashier', _objData)
				uni.navigateTo({
					url: '/packageFee/pages/fee/cashier?money=' + p.price +
						'&business=preStoreChargeCard&communityId=' +
						getCommunityId() + '&cashierUserId=' + getUserId()
				})
			},
			openOrder(r) {
				if (!r.orderId) return
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?orderId=' + r.orderId
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

	.cm-plan-ic-img {
		width: 40rpx;
		height: 40rpx;
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
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cm-stat-ic-img {
		width: 34rpx;
		height: 34rpx;
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
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cm-order-ic-img {
		width: 34rpx;
		height: 34rpx;
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

	.cm-empty {
		margin: 20rpx 28rpx 0;
		padding: 60rpx 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>
