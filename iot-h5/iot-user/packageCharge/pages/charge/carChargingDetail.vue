<template>
	<view class="ac-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 英雄区：四轮实时数据 -->
		<view class="ac-hero">
			<view class="ac-head">
				<view class="ac-title-row">
					<view class="c-pulse-dot" v-if="!stopped" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
					<text class="ac-title">充电中 · {{ order ? order.pile : '—' }}</text>
					<text class="ac-slot-tag" v-if="order && order.slot">枪 {{ order.slot }}</text>
				</view>
				<text class="ac-mode">{{ payWayText }}</text>
			</view>
			<view class="ac-stats">
				<view class="ac-stat">
					<text class="ac-stat-label">已充时长</text>
					<text class="ac-stat-val">{{ timeText }}</text>
				</view>
				<view class="ac-stat">
					<text class="ac-stat-label">当前功率</text>
					<text class="ac-stat-val">{{ powerText }}</text>
				</view>
				<view class="ac-stat">
					<text class="ac-stat-label">已用电量</text>
					<text class="ac-stat-val">{{ kwhText }}</text>
				</view>
				<view class="ac-stat">
					<text class="ac-stat-label">已充费用</text>
					<text class="ac-stat-val">¥{{ fee.toFixed(2) }}</text>
				</view>
			</view>
			<view class="ac-detail">
				<view class="ac-detail-row">
					<text class="ac-detail-label">SOC 充电进度</text>
					<text class="ac-detail-val">{{ socText }}</text>
				</view>
				<view class="ac-detail-row">
					<text class="ac-detail-label">预计剩余</text>
					<text class="ac-detail-val">{{ leftMinText }}</text>
				</view>
			</view>
			<view class="ac-note">{{ note }}</view>
		</view>

		<!-- 订单信息 -->
		<view class="c-card ac-mx ac-mt">
			<text class="c-title">充电订单</text>
			<view class="ac-info">
				<view class="ac-info-row"><text class="c-text-sub">订单编号</text><text class="ac-info-val">{{ order ? order.orderId : '—' }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">充电桩</text><text class="ac-info-val">{{ order ? order.pile : '—' }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">充电枪</text><text class="ac-info-val">{{ order && order.slot ? order.slot : '—' }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">开始时间</text><text class="ac-info-val">{{ order && order.startTime ? order.startTime : '—' }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">订单类型</text><text class="ac-info-val">{{ chargeInfoText }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">支付方式</text><text class="ac-info-val">{{ order ? order.way : '—' }}</text></view>
				<view class="ac-info-row"><text class="c-text-sub">计费说明</text><text class="ac-info-val">按电量计费 · 金额耗尽自动断电</text></view>
			</view>
		</view>

		<!-- 停止充电 -->
		<view class="ac-mx ac-mt" style="padding-bottom:80rpx;">
			<view v-if="showStopBtn" class="c-btn-danger" :style="stopping ? 'opacity:.6' : ''" @click="askStop">{{ stopping ? '停止中…' : '停止充电' }}</view>
			<text v-else-if="stopped" class="ac-stop-tip">已提交停止，等待结算完成</text>
			<text v-else class="ac-readonly-tip">该订单非本人订单 · 仅可查看订单信息</text>
		</view>

		<!-- 停止充电确认 -->
		<view class="c-mask" v-if="stopShow" @click="cancelStop"></view>
		<view class="c-dialog" v-if="stopShow">
			<view class="ac-dialog-head">
				<view class="ac-dialog-ic">
					<image :src="imgUrl + '/h5/images/common/ic-alert.png'" class="ac-dialog-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-title" style="display:block;">停止充电</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">确定要停止本次充电吗？</text>
				</view>
			</view>
			<view class="ac-dialog-warn">停止后按实际充电量结算 · 剩余金额自动退还余额。中途停止可能影响本次充电效果，请确认是否停止。</view>
			<view class="ac-dialog-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="cancelStop">再充一会</view>
				<view class="c-btn-danger" style="flex:1;" @click="confirmStop">确认停止</view>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeRunningMixin from '@/common/mixin/chargeRunningMixin.js'

	export default {
		mixins: [chargeRunningMixin],
		data() {
			return {
				pageKind: 'car',
				pollInterval: 5000
			}
		}
	}
</script>

<style>
	.ac-page {
		padding-bottom: 60rpx;
	}

	.ac-page .c-title {
		font-size: 30rpx;
	}

	.ac-page .c-text-sub {
		font-size: 24rpx;
	}

	.ac-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.ac-mt {
		margin-top: 24rpx;
	}

	.ac-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 32rpx;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .25);
	}

	.ac-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.ac-title-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.ac-title {
		color: #fff;
		font-size: 30rpx;
		font-weight: 700;
	}

	.ac-slot-tag {
		color: #fff;
		font-size: 22rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.ac-mode {
		color: #fff;
		font-size: 22rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 6rpx 20rpx;
	}

	.ac-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 12rpx;
		margin-top: 28rpx;
	}

	.ac-stat {
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 18rpx 0;
		text-align: center;
	}

	.ac-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.ac-stat-val {
		display: block;
		color: #fff;
		font-size: 30rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	.ac-detail {
		margin-top: 20rpx;
		display: flex;
		gap: 20rpx;
	}

	.ac-detail-row {
		flex: 1;
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 16rpx 20rpx;
	}

	.ac-detail-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 20rpx;
	}

	.ac-detail-val {
		display: block;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	.ac-note {
		margin-top: 20rpx;
		border-radius: 20rpx;
		background: rgba(255, 255, 255, .1);
		padding: 16rpx 20rpx;
		color: rgba(255, 255, 255, .85);
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ac-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.ac-info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.ac-info-val {
		font-size: 26rpx;
		font-weight: 600;
		color: #334155;
		text-align: right;
	}

	.ac-stop-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 24rpx;
		margin-top: 16rpx;
	}

	.ac-readonly-tip {
		display: block;
		text-align: center;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		padding: 24rpx;
		color: #059669;
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ac-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.ac-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(239, 68, 68, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.ac-dialog-ic-img {
		width: 32rpx;
		height: 32rpx;
	}

	.ac-dialog-warn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ac-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}
</style>
