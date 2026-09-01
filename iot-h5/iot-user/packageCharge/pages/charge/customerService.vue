<template>
	<view class="cs-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="c-card cs-mx cs-mt">
			<view class="cs-card-head">
				<view class="cs-card-ic">📞</view>
				<view style="flex:1;">
					<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;display:block;">云充电客服</text>
					<text class="c-text-bold" style="font-size:32rpx;font-weight:700;color:#0f172a;margin-top:8rpx;display:block;">400-880-1234</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">服务时间 8:00-22:00 · 全年无休</text>
				</view>
				<view class="cs-call-btn" @click="callShow = true">拨打</view>
			</view>
		</view>

		<!-- 拨号模拟弹窗 -->
		<view class="c-mask" v-if="callShow" @click="callShow = false"></view>
		<view class="c-dialog" v-if="callShow" style="text-align:center;">
			<view class="cs-call-ic">📞</view>
			<text class="c-text-bold" style="font-size:30rpx;font-weight:700;color:#0f172a;margin-top:20rpx;display:block;letter-spacing:4rpx;">400-880-1234</text>
			<view class="cs-call-status">
				<view class="c-pulse-dot" style="width:12rpx;height:12rpx;border-radius:50%;background:#10b981;"></view>
				<text class="c-text-sub">正在呼叫客服 · 请稍候…</text>
			</view>
			<view class="c-btn-ghost" style="margin-top:28rpx;" @click="callShow = false">取消</view>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'

	export default {
		data() {
			return {
				callShow: false
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		}
	}
</script>

<style>
	.cs-page {
		padding-bottom: 60rpx;
	}

	.cs-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.cs-mt {
		margin-top: 24rpx;
	}

	.cs-card-head {
		display: flex;
		align-items: center;
		gap: 24rpx;
		padding: 32rpx;
	}

	.cs-card-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: linear-gradient(135deg, #818cf8, #8b5cf6);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 40rpx;
	}

	.cs-call-btn {
		background: #10b981;
		color: #fff;
		font-size: 22rpx;
		font-weight: 700;
		border-radius: 999rpx;
		padding: 12rpx 32rpx;
		flex-shrink: 0;
	}

	.cs-call-ic {
		width: 104rpx;
		height: 104rpx;
		border-radius: 50%;
		background: rgba(99, 102, 241, .1);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 44rpx;
		margin: 20rpx auto 0;
	}

	.cs-call-status {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		margin-top: 16rpx;
	}
</style>
