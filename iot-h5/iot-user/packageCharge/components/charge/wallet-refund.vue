<template>
	<!-- 微信退款弹窗（对接微信退款接口 · 原路退回 · 供钱包页使用） -->
	<view>
		<view class="c-mask" v-if="show" @click="close"></view>
		<view class="c-dialog" v-if="show">
			<template v-if="step === 'form'">
				<view class="wr-head">
					<view class="wr-ic">📁</view>
					<view>
						<text class="c-title" style="display:block;">微信退款</text>
						<text class="c-text-sub" style="display:block;margin-top:6rpx;">原路退回至微信支付账户</text>
					</view>
				</view>
				<view class="wr-box">
					<view class="wr-row">
						<text class="c-text-sub">可退余额</text>
						<text class="c-text-bold" style="font-size:24rpx;font-weight:600;color:#334155;">¥{{ balance.toFixed(2) }}</text>
					</view>
					<view class="wr-input-row">
						<text class="c-text-sub">退款金额</text>
						<view style="display:flex;align-items:center;gap:8rpx;">
							<text style="font-size:24rpx;font-weight:700;color:#334155;">¥</text>
							<input v-model="amount" type="digit" placeholder="0.00" style="width:200rpx;text-align:right;font-size:28rpx;font-weight:700;color:#0f172a;" />
						</view>
					</view>
					<view class="wr-foot">
						<text class="wr-all" @click="amount = balance.toFixed(2)">全额退款</text>
						<text class="c-text-sub" style="font-size:18rpx;">到账时间：1-3 个工作日</text>
					</view>
				</view>
				<text class="wr-tip">退款将调用微信退款接口，按原支付渠道退回微信零钱，提交后不可撤销。</text>
				<view class="wr-btns">
					<view class="c-btn-ghost" style="flex:1;" @click="close">取消</view>
					<view class="c-btn-primary" style="flex:1;" @click="confirm">确认退款</view>
				</view>
			</template>
			<template v-else-if="step === 'proc'">
				<view style="display:flex;flex-direction:column;align-items:center;padding:40rpx 0;">
					<view class="wr-spinner"></view>
					<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#1e293b;margin-top:24rpx;">正在调用微信退款接口</text>
					<text class="c-text-sub" style="margin-top:12rpx;">商户退款单号 <text style="font-weight:600;color:#475569;">{{ refundNo }}</text></text>
				</view>
			</template>
			<template v-else>
				<view style="display:flex;flex-direction:column;align-items:center;padding:20rpx 0;">
					<view class="wr-done-ic">✓</view>
					<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;margin-top:20rpx;">退款成功</text>
					<text class="wr-done-amt">¥{{ doneAmt.toFixed(2) }}</text>
					<text class="c-text-sub" style="margin-top:12rpx;">微信退款接口返回成功 · 原路退回微信零钱</text>
					<text class="c-text-sub" style="margin-top:8rpx;">商户退款单号 <text style="font-weight:600;color:#64748b;">{{ refundNo }}</text></text>
					<view class="c-btn-primary" style="width:100%;margin-top:28rpx;" @click="finish">完成</view>
				</view>
			</template>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'wallet-refund',
		props: {
			show: {
				type: Boolean,
				default: false
			},
			balance: {
				type: Number,
				default: 0
			}
		},
		data() {
			return {
				step: 'form',
				amount: '',
				refundNo: '',
				doneAmt: 0
			}
		},
		watch: {
			show(val) {
				if (val) {
					this.step = 'form'
					this.amount = ''
				}
			}
		},
		methods: {
			close() {
				this.$emit('update:show', false)
			},
			confirm() {
				const amt = Math.round(parseFloat(this.amount || '0') * 100) / 100
				if (!amt || amt <= 0) {
					uni.showToast({
						title: '请输入退款金额',
						icon: 'none'
					})
					return
				}
				if (amt > this.balance) {
					uni.showToast({
						title: '退款金额不能大于余额',
						icon: 'none'
					})
					return
				}
				this.refundNo = 'RF' + String(Date.now()).slice(-12)
				this.step = 'proc'
				setTimeout(() => {
					this.doneAmt = amt
					this.step = 'done'
					this.$emit('success', amt)
				}, 800)
			},
			finish() {
				this.$emit('update:show', false)
			}
		}
	}
</script>

<style>
	.wr-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.wr-ic {
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

	.wr-box {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
	}

	.wr-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.wr-input-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 20rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		padding: 20rpx 24rpx;
	}

	.wr-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 16rpx;
	}

	.wr-all {
		font-size: 20rpx;
		color: #3b82f6;
		font-weight: 600;
	}

	.wr-tip {
		display: block;
		margin-top: 20rpx;
		font-size: 20rpx;
		color: #94a3b8;
		line-height: 1.6;
	}

	.wr-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.wr-spinner {
		width: 64rpx;
		height: 64rpx;
		border-radius: 50%;
		border: 6rpx solid #bfdbfe;
		border-top-color: #3b82f6;
		animation: wrSpin 1s linear infinite;
	}

	@keyframes wrSpin {
		to {
			transform: rotate(360deg);
		}
	}

	.wr-done-ic {
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

	.wr-done-amt {
		font-size: 44rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 12rpx;
	}
</style>
