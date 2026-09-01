<template>
	<!-- 申请开票弹层（抬头：个人/企业 · 供电子发票页使用） -->
	<view>
		<view class="c-mask" v-if="show" @click="close"></view>
		<view class="c-sheet" v-if="show">
			<view class="is-head">
				<text class="c-title" style="font-size:28rpx;">申请开票</text>
				<text class="is-close" @click="close">关闭</text>
			</view>
			<view class="is-body">
				<view class="is-tabs">
					<view class="is-tab" :class="{ active: invType === 'personal' }" @click="invType = 'personal'">个人抬头</view>
					<view class="is-tab" :class="{ active: invType === 'company' }" @click="invType = 'company'">企业抬头</view>
				</view>

				<view v-if="invType === 'personal'">
					<view class="c-form-input is-row">
						<text class="c-text-sub">姓名</text>
						<input v-model="personName" placeholder="请输入开票人姓名" />
					</view>
					<view class="c-form-input is-row">
						<text class="c-text-sub">身份证号</text>
						<input v-model="personId" placeholder="选填 · 用于税务核验" />
					</view>
				</view>
				<view v-else>
					<view class="c-form-input is-row">
						<text class="c-text-sub">企业名称</text>
						<input v-model="companyName" placeholder="与营业执照一致" />
					</view>
					<view class="c-form-input is-row">
						<text class="c-text-sub">税号</text>
						<input v-model="companyTax" placeholder="纳税人识别号" />
					</view>
				</view>
				<view class="c-form-input is-row">
					<text class="c-text-sub">邮箱</text>
					<input v-model="mail" placeholder="接收电子发票" />
				</view>

				<view class="is-sum">
					<text style="font-size:22rpx;color:#b45309;">开票金额（勾选订单）</text>
					<text style="font-size:28rpx;font-weight:700;color:#d97706;">¥{{ sum.toFixed(2) }}</text>
				</view>
				<view class="c-btn-primary" @click="submit">提交开票申请</view>
				<text class="is-tip">电子发票开具后自动推送至微信卡包与邮箱</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'invoice-sheet',
		props: {
			show: {
				type: Boolean,
				default: false
			},
			// 勾选订单合计金额
			sum: {
				type: Number,
				default: 0
			},
			// 默认抬头姓名（个人）
			defaultName: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				invType: 'personal',
				personName: '',
				personId: '',
				companyName: '',
				companyTax: '',
				mail: ''
			}
		},
		watch: {
			show(val) {
				if (val) {
					this.mail = ''
					this.personName = this.defaultName
					this.personId = ''
					this.companyName = ''
					this.companyTax = ''
				}
			}
		},
		methods: {
			close() {
				this.$emit('update:show', false)
			},
			submit() {
				let title
				if (this.invType === 'personal') {
					if (!this.personName.trim()) {
						uni.showToast({
							title: '请输入个人姓名',
							icon: 'none'
						})
						return
					}
					title = '个人 · ' + this.personName.trim() + (this.personId.trim() ? '（' + this.personId.trim() + '）' : '')
				} else {
					if (!this.companyName.trim()) {
						uni.showToast({
							title: '请输入企业名称',
							icon: 'none'
						})
						return
					}
					if (!this.companyTax.trim()) {
						uni.showToast({
							title: '请输入税号',
							icon: 'none'
						})
						return
					}
					title = '企业 · ' + this.companyName.trim() + '（' + this.companyTax.trim() + '）'
				}
				if (!this.mail.trim()) {
					uni.showToast({
						title: '请输入接收邮箱',
						icon: 'none'
					})
					return
				}
				this.$emit('submit', {
					title: title,
					mail: this.mail.trim()
				})
			}
		}
	}
</script>

<style>
	.is-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.is-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.is-body {
		padding: 0 36rpx 60rpx;
	}

	.is-tabs {
		display: flex;
		gap: 8rpx;
		background: #f1f5f9;
		border-radius: 20rpx;
		padding: 8rpx;
	}

	.is-tab {
		flex: 1;
		height: 68rpx;
		line-height: 68rpx;
		text-align: center;
		font-size: 24rpx;
		font-weight: 600;
		border-radius: 16rpx;
		color: #64748b;
	}

	.is-tab.active {
		background: #fff;
		color: #059669;
		box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, .08);
	}

	.is-row {
		margin-top: 20rpx;
	}

	.is-sum {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(249, 115, 22, .08);
		border: 1rpx solid rgba(249, 115, 22, .15);
		padding: 20rpx 24rpx;
	}

	.is-tip {
		display: block;
		text-align: center;
		font-size: 20rpx;
		color: #94a3b8;
		margin-top: 20rpx;
	}
</style>
