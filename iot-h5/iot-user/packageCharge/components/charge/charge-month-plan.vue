<template>
	<!-- 月卡套餐卡（购买/续费 · 供充电月卡页使用） -->
	<view class="c-card cmp-card">
		<view class="cmp-ic" :style="{ background: plan.color }">
			<text style="font-size:28rpx;">📇</text>
			<text class="cmp-ic-name">{{ plan.name.replace(' · ', '') }}</text>
		</view>
		<view style="flex:1;min-width:0;">
			<view class="cmp-name-row">
				<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">{{ plan.name }}</text>
				<text class="cmp-tag" v-if="plan.tag">{{ plan.tag }}</text>
			</view>
			<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ plan.desc }}</text>
		</view>
		<view style="text-align:right;">
			<text class="c-text-bold" style="font-size:32rpx;font-weight:700;color:#0f172a;">¥<text style="font-size:24rpx;">{{ plan.price }}</text></text>
			<view class="cmp-btn" @click="$emit('buy')">{{ renew ? '续费' : '立即开通' }}</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'charge-month-plan',
		props: {
			plan: {
				type: Object,
				required: true
			},
			// 续费模式（已开通月卡进入时显示"续费"）
			renew: {
				type: Boolean,
				default: false
			}
		}
	}
</script>

<style>
	.cmp-card {
		display: flex;
		align-items: center;
		gap: 24rpx;
		padding: 28rpx;
	}

	.cmp-ic {
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

	.cmp-ic-name {
		color: #fff;
		font-size: 14rpx;
		font-weight: 700;
	}

	.cmp-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.cmp-tag {
		font-size: 16rpx;
		color: #d97706;
		background: rgba(249, 115, 22, .08);
		border: 1rpx solid rgba(249, 115, 22, .25);
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.cmp-btn {
		margin-top: 10rpx;
		font-size: 20rpx;
		font-weight: 700;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 8rpx 26rpx;
		display: inline-block;
	}
</style>
