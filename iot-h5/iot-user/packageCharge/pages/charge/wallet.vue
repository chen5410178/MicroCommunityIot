<template>
	<view class="wl-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 余额卡 -->
		<view class="wl-balance-card">
			<text class="wl-balance-label">余额（可退）</text>
			<text class="wl-balance-num">¥{{ balance.toFixed(2) }}</text>
			<view class="wl-balance-actions">
				<view class="wl-refund-btn" @click="refundBalance">申请退款</view>
				<text class="wl-balance-note">余额长期有效 · 支持原路退回</text>
			</view>
		</view>

		<!-- 充值金额 -->
		<view class="wl-sec-title">充值金额</view>
		<view class="wl-tier-grid">
			<view v-for="(t, i) in rechargeTiers" :key="i" class="wl-tier" :class="{ active: tierIdx === i && !customAmount }"
				@click="pickTier(i)">
				<text class="wl-tier-gift" v-if="t.gift > 0">送 ¥{{ t.gift }}</text>
				<text class="wl-tier-val">¥{{ t.v }}</text>
				<text class="wl-tier-sub">微信支付</text>
			</view>
		</view>
		<view class="wl-custom-row">
			<text class="wl-custom-label">自定义金额</text>
			<view class="wl-custom-input-wrap">
				<text style="font-size:24rpx;font-weight:700;color:#334155;">¥</text>
				<input v-model="customAmount" type="digit" placeholder="0" style="width:200rpx;text-align:right;font-size:28rpx;font-weight:700;color:#0f172a;" />
			</view>
		</view>
		<view class="wl-mx">
			<view class="c-btn-primary" @click="doRecharge">立即充值</view>
		</view>

		<!-- 交易明细 -->
		<view class="wl-sec-title">交易明细</view>
		<view class="c-card wl-log-card">
			<view class="wl-range-bar">
				<view class="c-wallet-rng" :class="{ active: rangeDays === 30 }" @click="setRange(30)">近 1 月</view>
				<view class="c-wallet-rng" :class="{ active: rangeDays === 90 }" @click="setRange(90)">近 3 月</view>
				<view class="c-wallet-rng" :class="{ active: rangeDays === 0 }" @click="setRange(0)">全部</view>
			</view>
			<view class="wl-tab-bar">
				<view class="c-wtab" :class="{ active: tabType === 'in' }" @click="setTab('in')">充值</view>
				<view class="c-wtab" :class="{ active: tabType === 'out' }" @click="setTab('out')">支出</view>
			</view>
			<view class="wl-logs">
				<view v-for="(w, i) in displayLogs" :key="i" class="wl-log" :class="{ clickable: !w.in && !w.refund && w.orderId }"
					@click="openLog(w)">
					<text class="wl-log-date">{{ w.date }}</text>
					<view style="flex:1;min-width:0;">
						<text class="wl-log-name">{{ w.name }}</text>
						<text class="wl-log-sub" v-if="!w.in && !w.refund && w.orderId">点击查看订单</text>
					</view>
					<text class="wl-log-amt" :class="w.in ? 'in' : 'out'">{{ w.in ? '¥' : '-¥' }}{{ w.amt.toFixed(2) }}</text>
					<text class="wl-log-arrow" v-if="!w.in && !w.refund && w.orderId">›</text>
				</view>
				<view class="wl-log-empty" v-if="!displayLogs.length">
					<text class="c-text-sub">该时间段暂无{{ tabType === 'in' ? '充值' : '支出' }}记录</text>
				</view>
			</view>
		</view>

		<!-- 微信退款弹窗 -->
		<wallet-refund :show.sync="refundShow" :balance="balance" @success="onRefundSuccess" />

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		queryOwnerAccount,
		queryAppUserAccountDetail
	} from '@/api/user/userApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import walletRefund from '@/packageCharge/components/charge/wallet-refund.vue'
	import {
		saveTargetUrl,
		buildCurrentPageUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js'

	// 充值档位（固定 UI 配置）
	const rechargeTiers = [{
		v: 10,
		gift: 0
	}, {
		v: 20,
		gift: 0
	}, {
		v: 50,
		gift: 0
	}]

	export default {
		components: {
			walletRefund
		},
		data() {
			return {
				rechargeTiers,
				tierIdx: 1,
				customAmount: '',
				tabType: 'in',
				rangeDays: 30,
				refundShow: false,
				toastMsg: '',
			balance: 0,
			logs: [],
			displayLogs: []
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onShow() {
			this._loadWallet()
		},
		methods: {
			_loadWallet() {
				if (!hasLogin()) return
				queryOwnerAccount({
					page: 1,
					row: 20,
					communityId: getCommunityId(),
					acctType: '2003'
				}).then(list => {
					if (!list || !list.length) {
						this.balance = 0
						return
					}
					this.balance = parseFloat(list[0].amount) || 0
				}).catch(() => {})
				queryAppUserAccountDetail({
					page: 1,
					row: 100,
					communityId: getCommunityId(),
					acctType: '2003'
				}).then(detailList => {
					this.logs = (detailList || []).map(this._mapLog)
					this._filterLogs()
				}).catch(() => {})
			},
			_mapLog(d) {
				const isIn = String(d.detailType) === '1001'
				const isRefund = String(d.detailType) === '3003'
				return {
					date: this._fmtTime(d.createTime),
					name: d.remark || (isIn ? '充值' : '支出'),
					amt: parseFloat(d.amount) || 0,
					in: isIn,
					refund: isRefund,
					days: this._daysDiff(d.createTime),
					orderId: d.orderId || ''
				}
			},
			_fmtTime(t) {
				if (!t) return ''
				let d = new Date(String(t).replace(/-/g, '/'))
				if (isNaN(d.getTime())) return String(t || '')
				return ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2) + ' ' + ('0' + d.getHours())
					.slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2)
			},
			_daysDiff(t) {
				if (!t) return 999
				let d = new Date(String(t).replace(/-/g, '/'))
				if (isNaN(d.getTime())) return 999
				const now = new Date()
				const start = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
				const that = new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime()
				return Math.max(0, Math.round((start - that) / 86400000))
			},
			_filterLogs() {
				this.displayLogs = this.logs.filter(w => w.in === (this.tabType === 'in') && (this.rangeDays === 0 || (w
					.days ?? 0) <= this.rangeDays))
			},
			pickTier(i) {
				this.tierIdx = i
				this.customAmount = ''
			},
			setRange(d) {
				this.rangeDays = d
				this._filterLogs()
			},
			setTab(t) {
				this.tabType = t
				this._filterLogs()
			},
			openLog(w) {
				if (w.in || w.refund || !w.orderId) return
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?orderId=' + w.orderId
				})
			},
			doRecharge() {
				if (!hasLogin()) {
					this._toast('请先登录')
					saveTargetUrl(buildCurrentPageUrl())
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const custom = parseFloat(this.customAmount)
				const tier = this.rechargeTiers[this.tierIdx]
				const amt = custom > 0 ? custom : tier.v
				if (!amt || amt < 1) {
					this._toast('请输入充值金额')
					return
				}
				let url = '/packageAccount/pages/account/preStoreAccount?communityId=' + getCommunityId()
				if (amt > 0) url += '&amount=' + amt
				uni.navigateTo({ url })
			},
			refundBalance() {
				if (!hasLogin()) {
					this._toast('请先登录')
					saveTargetUrl(buildCurrentPageUrl())
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				if (this.balance <= 0) {
					this._toast('当前无可退余额')
					return
				}
				this.refundShow = true
			},
			onRefundSuccess(amt) {
				this._toast('退款成功 · 已原路退回 ¥' + amt.toFixed(2))
				this._loadWallet()
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
	.wl-page {
		padding-bottom: 60rpx;
	}

	.wl-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
		margin-top: 24rpx;
	}

	.wl-balance-card {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		padding: 36rpx;
		box-shadow: 0 12rpx 30rpx rgba(59, 130, 246, .2);
	}

	.wl-balance-label {
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
	}

	.wl-balance-num {
		display: block;
		color: #fff;
		font-size: 60rpx;
		font-weight: 700;
		margin-top: 12rpx;
	}

	.wl-balance-actions {
		display: flex;
		align-items: center;
		gap: 20rpx;
		margin-top: 24rpx;
	}

	.wl-refund-btn {
		font-size: 20rpx;
		color: #fff;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 10rpx 26rpx;
	}

	.wl-balance-note {
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.wl-sec-title {
		margin: 32rpx 28rpx 16rpx;
		font-size: 26rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.wl-tier-grid {
		margin: 0 28rpx;
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
	}

	.wl-tier {
		position: relative;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border-radius: 20rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		padding: 26rpx 0;
		text-align: center;
		transition: all .2s;
	}

	.wl-tier.active {
		border-color: #10b981;
		background: rgba(16, 185, 129, .05);
	}

	.wl-tier-val {
		display: block;
		font-size: 30rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.wl-tier-gift {
		position: absolute;
		top: -12rpx;
		right: 10rpx;
		font-size: 16rpx;
		color: #fff;
		background: #fb923c;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
		line-height: 1.6;
	}

	.wl-tier-sub {
		display: block;
		font-size: 18rpx;
		color: #94a3b8;
		margin-top: 6rpx;
	}

	.wl-custom-row {
		margin: 20rpx 28rpx 0;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		padding: 22rpx 26rpx;
	}

	.wl-custom-label {
		font-size: 24rpx;
		color: #94a3b8;
	}

	.wl-custom-input-wrap {
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.wl-log-card {
		margin: 0 28rpx;
		overflow: hidden;
	}

	.wl-range-bar {
		display: flex;
		gap: 16rpx;
		padding: 24rpx 26rpx 20rpx;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.wl-range-bar .c-wallet-rng {
		font-size: 20rpx;
		font-weight: 600;
		border-radius: 999rpx;
		padding: 10rpx 24rpx;
	}

	.wl-tab-bar {
		display: flex;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.wl-tab-bar .c-wtab {
		flex: 1;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
		font-size: 24rpx;
		font-weight: 700;
	}

	.wl-logs {}

	.wl-log {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 24rpx 26rpx;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.wl-log.clickable:active {
		background: #f8fafc;
	}

	.wl-log-date {
		width: 200rpx;
		font-size: 22rpx;
		color: #64748b;
		flex-shrink: 0;
	}

	.wl-log-name {
		display: block;
		font-size: 22rpx;
		color: #1e293b;
		font-weight: 500;
	}

	.wl-log-sub {
		display: block;
		font-size: 18rpx;
		color: #cbd5e1;
		margin-top: 4rpx;
	}

	.wl-log-amt {
		font-size: 22rpx;
		font-weight: 600;
		flex-shrink: 0;
	}

	.wl-log-amt.in {
		color: #059669;
	}

	.wl-log-amt.out {
		color: #334155;
	}

	.wl-log-arrow {
		color: #cbd5e1;
		font-size: 24rpx;
	}

	.wl-log-empty {
		padding: 60rpx 0;
		text-align: center;
	}

	.wl-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.wl-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(16, 185, 129, .1);
		color: #059669;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
	}

	.wl-refund-box {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
	}

	.wl-refund-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.wl-refund-input-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 20rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		padding: 20rpx 24rpx;
	}

	.wl-refund-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 16rpx;
	}

	.wl-refund-all {
		font-size: 20rpx;
		color: #3b82f6;
		font-weight: 600;
	}

	.wl-refund-tip {
		display: block;
		margin-top: 20rpx;
		font-size: 20rpx;
		color: #94a3b8;
		line-height: 1.6;
	}

	.wl-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.wl-spinner {
		width: 64rpx;
		height: 64rpx;
		border-radius: 50%;
		border: 6rpx solid #bfdbfe;
		border-top-color: #3b82f6;
		animation: wlSpin 1s linear infinite;
	}

	@keyframes wlSpin {
		to {
			transform: rotate(360deg);
		}
	}

	.wl-refund-done-ic {
		width: 96rpx;
		height: 96rpx;
		border-radius: 50%;
		background: rgba(16, 185, 129, .1);
		color: #059669;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 44rpx;
		font-weight: 700;
	}

	.wl-refund-done-amt {
		font-size: 44rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 12rpx;
	}
</style>
