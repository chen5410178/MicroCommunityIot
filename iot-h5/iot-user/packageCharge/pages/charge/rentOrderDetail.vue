<template>
	<view class="rd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 订单头部 -->
		<view class="rd-hero">
			<view class="rd-hero-head">
				<text class="rd-title">{{ order.plate }} · {{ order.plan }}</text>
				<text class="rd-state">{{ order.st }}</text>
			</view>
			<text class="rd-label">实付金额</text>
			<text class="rd-fee">¥{{ order.fee }}</text>
			<text class="rd-period">有效期 {{ order.from }} → {{ order.to }}</text>
		</view>

		<!-- 订单信息 -->
		<view class="c-card rd-mx rd-mt">
			<text class="c-title">订单信息</text>
			<view class="rd-info">
				<view class="rd-info-row"><text class="c-text-sub">订单编号</text><text class="rd-info-val">RO{{ String(order.id).padStart(8, '0') }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">车牌号码</text><text class="rd-info-val">{{ order.plate }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">月租套餐</text><text class="rd-info-val">{{ order.plan }}（{{ order.months }} 个月）</text></view>
				<view class="rd-info-row"><text class="c-text-sub">支付方式</text><text class="rd-info-val">微信支付</text></view>
				<view class="rd-info-row"><text class="c-text-sub">支付时间</text><text class="rd-info-val">{{ order.time }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">订单状态</text><text class="rd-info-val" style="color:#059669;">{{ order.st }}</text></view>
				<view class="rd-info-row"><text class="c-text-sub">实付金额</text><text class="rd-info-val" style="font-weight:700;">¥{{ order.fee }}</text></view>
			</view>
		</view>

		<view class="rd-tip rd-mx rd-mt">
			<text style="font-size:20rpx;line-height:1.6;color:#3b82f6;">月租生效期间车辆进出小区按套餐计费，到期前可随时在车辆服务页续费。</text>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'

	export default {
		data() {
			return {
				order: {}
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad(options) {
			const idx = parseInt(options.idx || 0)
			this.order = chargeStore.rentOrders[idx] || chargeStore.rentOrders[0] || {}
		}
	}
</script>

<style>
	.rd-page {
		padding-bottom: 60rpx;
	}

	.rd-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.rd-mt {
		margin-top: 24rpx;
	}

	.rd-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		padding: 36rpx;
		box-shadow: 0 12rpx 30rpx rgba(59, 130, 246, .25);
	}

	.rd-hero-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.rd-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.rd-state {
		color: #fff;
		font-size: 18rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 6rpx 18rpx;
	}

	.rd-label {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 20rpx;
	}

	.rd-fee {
		display: block;
		color: #fff;
		font-size: 56rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.rd-period {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 14rpx;
	}

	.rd-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.rd-info-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.rd-info-val {
		font-size: 22rpx;
		font-weight: 600;
		color: #334155;
	}

	.rd-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 20rpx 24rpx;
	}
</style>
