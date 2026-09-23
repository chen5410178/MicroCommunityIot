<template>
	<view class="ab-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 英雄区：两轮真实订单数据 -->
		<view class="ab-hero">
			<view class="ab-head">
				<view class="ab-title-row">
					<view class="c-pulse-dot" v-if="!stopped" style="width:16rpx;height:16rpx;border-radius:50%;background:#fff;"></view>
					<text class="ab-title">充电中 · {{ order ? order.pile : '—' }}</text>
					<text class="ab-slot-tag" v-if="order && order.slot">插座 {{ order.slot }}</text>
				</view>
				<text class="ab-mode">{{ payWayText }}</text>
			</view>
			<view class="ab-stats">
				<view class="ab-stat">
					<text class="ab-stat-label">已充时长</text>
					<text class="ab-stat-val">{{ timeText }}</text>
				</view>
				<view class="ab-stat">
					<text class="ab-stat-label">已用电量</text>
					<text class="ab-stat-val">{{ kwhText }}</text>
				</view>
				<view class="ab-stat">
					<text class="ab-stat-label">已充费用</text>
					<text class="ab-stat-val" :style="feeLocked ? 'font-size:22rpx;' : ''">{{ feeText }}</text>
				</view>
			</view>
			<view class="ab-note">{{ note }}</view>
		</view>

		<!-- 计费与保护说明 -->
		<view class="c-card ab-mx ab-mt">
			<text class="c-title">充电说明</text>
			<view class="ab-info">
				<view class="ab-info-row"><text class="c-text-sub">订单编号</text><text class="ab-info-val">{{ order ? order.orderId : '—' }}</text></view>
				<view class="ab-info-row"><text class="c-text-sub">充电桩</text><text class="ab-info-val">{{ order ? order.pile : '—' }}</text></view>
				<view class="ab-info-row"><text class="c-text-sub">插座</text><text class="ab-info-val">{{ order && order.slot ? order.slot : '—' }}</text></view>
				<view class="ab-info-row"><text class="c-text-sub">开始时间</text><text class="ab-info-val">{{ order && order.startTime ? order.startTime : '—' }}</text></view>
				<view class="ab-info-row"><text class="c-text-sub">支付方式</text><text class="ab-info-val">{{ order ? order.way : '—' }}</text></view>
				<view class="ab-info-row"><text class="c-text-sub">保护机制</text><text class="ab-info-val">充满自停 / 定时断电</text></view>
				<view class="ab-info-row"><text class="c-text-sub">计费说明</text><text class="ab-info-val">按实际充电量结算 · 剩余金额自动退回</text></view>
			</view>
		</view>

		<!-- 实时数据说明 -->
		<view class="c-card ab-mx ab-mt">
			<text class="c-title">实时数据说明</text>
			<view class="ab-info">
				<text class="c-text-sub" style="line-height:1.7;">电瓶车充电桩不支持实时功率上报，本页仅展示订单真实数据：已充时长按开始时间自动递增，已用电量按设备上报每 30 秒刷新，费用在结算完成后展示。中途停止不影响结算金额的准确性。</text>
			</view>
		</view>

		<!-- 停止充电 -->
		<view class="ab-mx ab-mt" style="padding-bottom:80rpx;">
			<view v-if="showStopBtn" class="c-btn-danger" :style="stopping ? 'opacity:.6' : ''" @click="askStop">{{ stopping ? '停止中…' : '停止充电' }}</view>
			<text v-else-if="stopped" class="ab-stop-tip">已提交停止，等待结算完成</text>
			<text v-else class="ab-readonly-tip">该订单非本人订单 · 仅可查看订单信息</text>
		</view>

		<!-- 停止充电确认 -->
		<view class="c-mask" v-if="stopShow" @click="cancelStop"></view>
		<view class="c-dialog" v-if="stopShow">
			<view class="ab-dialog-head">
				<view class="ab-dialog-ic">
					<image :src="imgUrl + '/h5/images/common/ic-alert.png'" class="ab-dialog-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-title" style="display:block;">停止充电</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">确定要停止本次充电吗？</text>
				</view>
			</view>
			<view class="ab-dialog-warn">停止后按实际充电量结算 · 剩余金额自动退还余额。中途停止可能影响本次充电效果，请确认是否停止。</view>
			<view class="ab-dialog-btns">
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
				pageKind: 'bike',
				pollInterval: 30000
			}
		}
	}
</script>

<style>
	.ab-page {
		padding-bottom: 60rpx;
	}

	.ab-page .c-title {
		font-size: 30rpx;
	}

	.ab-page .c-text-sub {
		font-size: 24rpx;
	}

	.ab-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.ab-mt {
		margin-top: 24rpx;
	}

	.ab-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 32rpx;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .25);
	}

	.ab-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.ab-title-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.ab-title {
		color: #fff;
		font-size: 30rpx;
		font-weight: 700;
	}

	.ab-slot-tag {
		color: #fff;
		font-size: 22rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.ab-mode {
		color: #fff;
		font-size: 22rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 6rpx 20rpx;
	}

	.ab-stats {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 12rpx;
		margin-top: 28rpx;
	}

	.ab-stat {
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 18rpx 0;
		text-align: center;
	}

	.ab-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.ab-stat-val {
		display: block;
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	.ab-note {
		margin-top: 20rpx;
		border-radius: 20rpx;
		background: rgba(255, 255, 255, .1);
		padding: 16rpx 20rpx;
		color: rgba(255, 255, 255, .85);
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ab-info {
		padding: 20rpx 28rpx 28rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.ab-info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.ab-info-val {
		font-size: 26rpx;
		font-weight: 600;
		color: #334155;
		text-align: right;
	}

	.ab-stop-tip {
		display: block;
		text-align: center;
		color: #94a3b8;
		font-size: 24rpx;
		margin-top: 16rpx;
	}

	.ab-readonly-tip {
		display: block;
		text-align: center;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		padding: 24rpx;
		color: #059669;
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ab-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.ab-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(239, 68, 68, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.ab-dialog-ic-img {
		width: 32rpx;
		height: 32rpx;
	}

	.ab-dialog-warn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 24rpx;
		line-height: 1.6;
	}

	.ab-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}
</style>
