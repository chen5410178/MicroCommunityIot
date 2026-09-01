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
				<view v-for="(w, i) in filterLogs" :key="i" class="wl-log" :class="{ clickable: !w.in && !w.refund }"
					@click="openLog(w)">
					<text class="wl-log-date">{{ w.date }}</text>
					<view style="flex:1;min-width:0;">
						<text class="wl-log-name">{{ w.name }}</text>
						<text class="wl-log-sub" v-if="!w.in && !w.refund">点击查看订单</text>
					</view>
					<text class="wl-log-amt" :class="w.in ? 'in' : 'out'">{{ w.in ? '¥' : '-¥' }}{{ w.amt.toFixed(2) }}</text>
					<text class="wl-log-arrow" v-if="!w.in && !w.refund">›</text>
				</view>
				<view class="wl-log-empty" v-if="!filterLogs.length">
					<text class="c-text-sub">该时间段暂无{{ tabType === 'in' ? '充值' : '支出' }}记录</text>
				</view>
			</view>
		</view>

		<!-- 微信退款弹窗 -->
		<wallet-refund :show.sync="refundShow" :balance="balance" @success="onRefundSuccess" />

		<!-- 微信支付弹窗（充值） -->
		<view class="c-mask" v-if="payShow" @click="payShow = false"></view>
		<view class="c-dialog" v-if="payShow" style="padding:0;overflow:hidden;">
			<view class="wl-pay-head">
				<text class="wl-pay-title">微信支付 · 充值余额</text>
				<text class="wl-pay-amount">¥{{ payAmount.toFixed(2) }}</text>
				<text class="wl-pay-desc">充值到余额{{ payGift > 0 ? '（赠送 ¥' + payGift + '）' : '' }}</text>
			</view>
			<view style="padding:32rpx;">
				<view class="wl-pay-row">
					<view class="wl-pay-ic">📞</view>
					<view style="flex:1;">
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;display:block;">微信支付</text>
						<text class="c-text-sub">使用零钱支付</text>
					</view>
					<text style="color:#10b981;font-size:26rpx;font-weight:700;">✓</text>
				</view>
				<view class="c-btn-primary" @click="confirmPay">确认支付</view>
				<text class="wl-pay-demo-tip">演示环境 · 点击确认支付即完成</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		rechargeTiers
	} from '@/api/mock/chargeMockData.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import walletRefund from '@/packageCharge/components/charge/wallet-refund.vue'

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
				payShow: false,
				payAmount: 0,
				payGift: 0,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			balance() {
				return chargeStore.balance
			},
			filterLogs() {
				return chargeStore.walletLogs.filter(w => w.in === (this.tabType === 'in') && (this.rangeDays === 0 || (w
					.days ?? 0) <= this.rangeDays))
			}
		},
		onShow() {
			chargeStore.saveWalletLogs()
		},
		methods: {
			pickTier(i) {
				this.tierIdx = i
				this.customAmount = ''
			},
			setRange(d) {
				this.rangeDays = d
			},
			setTab(t) {
				this.tabType = t
			},
			openLog(w) {
				if (w.in || w.refund) return
				const realIdx = w.recIdx !== undefined ? w.recIdx : 0
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?orderIdx=' + realIdx
				})
			},
			doRecharge() {
				if (!hasLogin()) {
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const custom = parseFloat(this.customAmount)
				const tier = this.rechargeTiers[this.tierIdx]
				const amt = custom > 0 ? custom : tier.v
				const gift = custom > 0 ? 0 : tier.gift
				if (!amt || amt < 1) {
					this._toast('请输入充值金额')
					return
				}
				this.payAmount = amt
				this.payGift = gift
				this.payShow = true
			},
			confirmPay() {
				const amt = this.payAmount
				const gift = this.payGift
				chargeStore.setBalance(chargeStore.balance + amt + gift)
				chargeStore.walletLogs.unshift({
					name: '微信充值' + (gift > 0 ? '（送 ¥' + gift + '）' : ''),
					date: '今天 ' + new Date().getHours() + ':' + ('0' + new Date().getMinutes()).slice(-2),
					amt: amt + gift,
					in: true,
					days: 0
				})
				chargeStore.saveWalletLogs()
				this.payShow = false
				this._toast('充值成功 · 到账 ¥' + (amt + gift).toFixed(2))
				chargeStore.addMsg('wallet', '余额变动提醒', '充值成功！¥' + (amt + gift).toFixed(2) + ' 已到账，当前余额 ¥' + chargeStore.balance
					.toFixed(2) + '，随时为您的出行充满能量。')
			},
			refundBalance() {
				this.refundShow = true
			},
			onRefundSuccess(amt) {
				chargeStore.setBalance(chargeStore.balance - amt)
				chargeStore.walletLogs.unshift({
					name: '微信退款 · 原路退回',
					date: '今天 ' + new Date().getHours() + ':' + ('0' + new Date().getMinutes()).slice(-2),
					amt: amt,
					in: false,
					refund: true,
					days: 0
				})
				chargeStore.saveWalletLogs()
				this._toast('退款成功 · 已原路退回 ¥' + amt.toFixed(2))
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

	.wl-pay-head {
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 28rpx;
	}

	.wl-pay-title {
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
	}

	.wl-pay-amount {
		display: block;
		color: #fff;
		font-size: 44rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.wl-pay-desc {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 4rpx;
	}

	.wl-pay-row {
		display: flex;
		align-items: center;
		gap: 20rpx;
		margin-bottom: 28rpx;
	}

	.wl-pay-ic {
		width: 80rpx;
		height: 80rpx;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
	}

	.wl-pay-demo-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 20rpx;
		margin-top: 16rpx;
	}
</style>
