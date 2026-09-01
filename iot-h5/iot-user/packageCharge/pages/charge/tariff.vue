<template>
	<view class="tf-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="c-card tf-mx tf-mt">
			<text class="c-title" style="display:block;">电动车分时电价</text>
			<text class="c-text-sub" style="display:block;margin-top:8rpx;">尖峰平谷 4 个时段差异化计费 · 单价 = 电费 + 服务费 · 当前计费时段已高亮</text>
			<view class="tf-rows">
				<view v-for="(t, i) in tariffs" :key="i" class="tf-row" :class="{ current: t === curTariff }">
					<view class="tf-dot" :style="{ background: t.color }"></view>
					<view style="flex:1;min-width:0;">
						<view class="tf-name-row">
							<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#1e293b;">{{ t.name }}段</text>
							<text class="tf-now-tag" v-if="t === curTariff">当前</text>
						</view>
						<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ t.time }}</text>
						<text class="c-text-sub" style="margin-top:4rpx;display:block;">电费 ¥{{ t.elec.toFixed(2) }}/度 + 服务费 ¥{{ t.serv.toFixed(2) }}/度</text>
					</view>
					<text class="tf-rate" :class="{ current: t === curTariff }">¥{{ t.rate.toFixed(2) }}<text class="c-text-sub" style="font-size:18rpx;">/度</text></text>
				</view>
			</view>
		</view>

		<view class="tf-tip tf-mx tf-mt">
			<text style="font-size:20rpx;line-height:1.6;color:#2563eb;">小程序端仅支持查看费率。如需调整尖峰平谷费率，请前往物业 PC 管理后台「充电服务 · 分时电价」设置，保存后自动同步至小程序。</text>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		defaultTariffs,
		tariffAt
	} from '@/api/mock/chargeMockData.js'

	export default {
		data() {
			return {
				tariffs: JSON.parse(JSON.stringify(defaultTariffs))
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			curTariff() {
				return tariffAt(new Date().getHours(), this.tariffs)
			}
		}
	}
</script>

<style>
	.tf-page {
		padding-bottom: 60rpx;
	}

	.tf-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.tf-mt {
		margin-top: 24rpx;
	}

	.tf-rows {
		margin-top: 24rpx;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.tf-row {
		display: flex;
		align-items: center;
		gap: 16rpx;
		border: 1rpx solid #f1f5f9;
		border-radius: 20rpx;
		padding: 20rpx;
	}

	.tf-row.current {
		border-color: rgba(16, 185, 129, .3);
		background: rgba(16, 185, 129, .06);
	}

	.tf-dot {
		width: 18rpx;
		height: 18rpx;
		border-radius: 50%;
		flex-shrink: 0;
	}

	.tf-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.tf-now-tag {
		font-size: 16rpx;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.tf-rate {
		font-size: 26rpx;
		font-weight: 700;
		color: #1e293b;
		flex-shrink: 0;
	}

	.tf-rate.current {
		color: #059669;
	}

	.tf-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 20rpx 24rpx;
	}
</style>
