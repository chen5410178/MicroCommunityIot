<template>
	<!-- 微信退款弹窗（对接微信退款接口 · 原路退回 · 供钱包页使用） -->
	<view>
		<view class="c-mask" v-if="show" @click="close"></view>
		<view class="c-dialog" v-if="show">
			<template v-if="step === 'form'">
				<view class="wr-head">
					<view class="wr-ic">
						<image :src="imgUrl + '/h5/images/charge/ic-receipt.png'" class="wr-ic-img" mode="aspectFit"></image>
					</view>
					<view>
						<text class="c-title" style="display:block;">微信退款</text>
						<text class="c-text-sub" style="display:block;margin-top:6rpx;">原路退回至微信支付账户</text>
					</view>
				</view>
				<view class="wr-box">
					<view class="wr-row">
						<text class="c-text-sub">退款金额</text>
						<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;">¥{{ balance.toFixed(2) }}</text>
					</view>
					<view class="wr-foot">
						<text class="c-text-sub" style="font-size:18rpx;">余额全额原路退回</text>
						<text class="c-text-sub" style="font-size:18rpx;">到账时间：1-3 个工作日</text>
					</view>
				</view>
				<view class="wr-btns">
					<view class="c-btn-ghost" style="flex:1;" @click="close">取消</view>
					<view class="c-btn-primary" style="flex:1;" @click="confirm">确认退款</view>
				</view>
			</template>
			<template v-else-if="step === 'proc'">
				<view style="display:flex;flex-direction:column;align-items:center;padding:40rpx 0;">
					<view class="wr-spinner"></view>
					<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#1e293b;margin-top:24rpx;">正在调用微信退款接口</text>
					<text class="c-text-sub" style="margin-top:12rpx;">请勿关闭本页面</text>
				</view>
			</template>
			<template v-else>
				<view style="display:flex;flex-direction:column;align-items:center;padding:20rpx 0;">
					<view class="wr-done-ic">
						<image :src="imgUrl + '/h5/images/common/ic-check.png'" class="wr-done-ic-img" mode="aspectFit"></image>
					</view>
					<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;margin-top:20rpx;">退款成功</text>
					<text class="wr-done-amt">¥{{ doneAmt.toFixed(2) }}</text>
					<text class="c-text-sub" style="margin-top:12rpx;">已原路退回微信零钱 · 1-3 个工作日到账</text>
					<text class="c-text-sub" style="margin-top:8rpx;" v-if="refundNo">退款单号 <text style="font-weight:600;color:#64748b;">{{ refundNo }}</text></text>
					<view class="c-btn-primary" style="width:100%;margin-top:28rpx;" @click="finish">完成</view>
				</view>
			</template>
		</view>
	</view>
</template>

<script>
	import {
		refundOwnerAccount
	} from '@/api/user/userApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'

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
			},
			acctId: {
				type: String,
				default: ''
			},
			communityId: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				step: 'form',
				refundNo: '',
				doneAmt: 0,
				submitting: false
			}
		},
		watch: {
			show(val) {
				if (val) {
					this.step = 'form'
					this.refundNo = ''
					this.doneAmt = 0
					this.submitting = false
				}
			}
		},
		methods: {
			close() {
				if (this.submitting) return
				this.$emit('update:show', false)
			},
			confirm() {
				if (!this.acctId) {
					uni.showToast({
						title: '账户信息获取失败，请稍后重试',
						icon: 'none'
					})
					return
				}

				this.submitting = true
				this.step = 'proc'
				refundOwnerAccount({
					communityId: this.communityId || getCommunityId(),
					acctId: this.acctId
				}).then(data => {
					this.refundNo = (data && data.refundId) || ''
					const refundFee = data && data.refundFee != null ? parseFloat(data.refundFee) : NaN
					this.doneAmt = isNaN(refundFee) ? this.balance : refundFee
					this.step = 'done'
					this.$emit('success', data)
				}).catch(err => {
					this.submitting = false
					this.step = 'form'
					uni.showToast({
						title: typeof err === 'string' && err ? err : '退款失败，请稍后重试',
						icon: 'none',
						duration: 3000
					})
				})
			},
			finish() {
				this.submitting = false
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
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.wr-ic-img {
		width: 34rpx;
		height: 34rpx;
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

	.wr-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 16rpx;
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
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.wr-done-ic-img {
		width: 48rpx;
		height: 48rpx;
	}

	.wr-done-amt {
		font-size: 44rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 12rpx;
	}
</style>
