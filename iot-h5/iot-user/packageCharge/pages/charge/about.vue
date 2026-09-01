<template>
	<view class="ab-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="ab-logo-area">
			<view class="ab-logo">⚡</view>
			<text class="ab-name">云充电</text>
			<text class="c-text-sub" style="margin-top:8rpx;">版本 v1.3.0 · 智慧出行</text>
		</view>

		<view class="ab-menu c-card ab-mx">
			<view class="ab-menu-item" @click="openAgree('用户服务协议')">
				<text style="font-size:30rpx;">📄</text>
				<text class="ab-menu-text">用户服务协议</text>
				<text style="color:#cbd5e1;font-size:24rpx;">›</text>
			</view>
			<view class="ab-menu-item" @click="openAgree('隐私保护指引')">
				<text style="font-size:30rpx;">🛡</text>
				<text class="ab-menu-text">隐私保护指引</text>
				<text style="color:#cbd5e1;font-size:24rpx;">›</text>
			</view>
		</view>

		<view class="ab-mx ab-mt">
			<view class="c-btn-ghost" style="height:96rpx;line-height:96rpx;font-size:26rpx;font-weight:700;color:#334155;" @click="checkUpdate">检查更新</view>
		</view>
		<text class="ab-footer">云充电科技有限公司 · 粤ICP备2026000000号</text>
		<text class="ab-footer-sub">本页面为原型演示 · 最终以实际上线版本为准</text>

		<!-- 协议弹层 -->
		<view class="c-mask" v-if="agreeShow" @click="agreeShow = false"></view>
		<view class="c-sheet" v-if="agreeShow" style="max-height:72%;">
			<view class="ab-agree-head">
				<text class="c-title" style="font-size:28rpx;">{{ agreeTitle }}</text>
				<text class="ab-agree-close" @click="agreeShow = false">关闭</text>
			</view>
			<scroll-view scroll-y class="ab-agree-body">
				<text class="ab-agree-text">{{ agreeText }}</text>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		agreeTexts
	} from '@/api/mock/invoiceData.js'

	export default {
		data() {
			return {
				agreeShow: false,
				agreeTitle: '',
				agreeText: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		methods: {
			openAgree(type) {
				this.agreeTitle = type
				this.agreeText = agreeTexts[type] || '暂无内容'
				this.agreeShow = true
			},
			checkUpdate() {
				uni.showToast({
					title: '当前已是最新版本 v1.3.0',
					icon: 'none'
				})
			}
		}
	}
</script>

<style>
	.ab-page {
		padding-bottom: 60rpx;
	}

	.ab-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.ab-mt {
		margin-top: 24rpx;
	}

	.ab-logo-area {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-top: 80rpx;
	}

	.ab-logo {
		width: 160rpx;
		height: 160rpx;
		border-radius: 44rpx;
		background: linear-gradient(135deg, #34d399, #059669);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 64rpx;
		box-shadow: 0 16rpx 40rpx rgba(16, 185, 129, .3);
	}

	.ab-name {
		font-size: 34rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 24rpx;
	}

	.ab-menu {
		margin-top: 48rpx;
		overflow: hidden;
	}

	.ab-menu-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.ab-menu-item:last-child {
		border-bottom: none;
	}

	.ab-menu-text {
		flex: 1;
		font-size: 24rpx;
		font-weight: 500;
		color: #334155;
	}

	.ab-footer {
		display: block;
		text-align: center;
		font-size: 20rpx;
		color: #94a3b8;
		margin-top: 48rpx;
	}

	.ab-footer-sub {
		display: block;
		text-align: center;
		font-size: 18rpx;
		color: #cbd5e1;
		margin-top: 8rpx;
	}

	.ab-agree-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.ab-agree-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.ab-agree-body {
		padding: 0 36rpx 60rpx;
		max-height: 46vh;
	}

	.ab-agree-text {
		font-size: 22rpx;
		color: #64748b;
		line-height: 1.8;
		white-space: pre-wrap;
	}
</style>
