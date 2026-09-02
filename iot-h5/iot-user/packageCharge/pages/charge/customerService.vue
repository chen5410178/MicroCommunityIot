<template>
	<view class="cs-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="c-card cs-mx cs-mt" v-if="serviceTel">
			<view class="cs-card-head">
				<view class="cs-card-ic">
					<image :src="imgUrl + '/h5/images/charge/ic-phone-white.png'" class="cs-card-ic-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;">
					<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;display:block;">{{ serviceName || '小区客服' }}</text>
					<text class="c-text-bold" style="font-size:32rpx;font-weight:700;color:#0f172a;margin-top:8rpx;display:block;">{{ serviceTel }}</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">服务时间 8:00-22:00 · 全年无休</text>
				</view>
				<view class="cs-call-btn" @click="callService">拨打</view>
			</view>
		</view>
		<view class="c-card cs-mx cs-mt" v-else>
			<view class="cs-card-head">
				<view class="cs-card-ic">
					<image :src="imgUrl + '/h5/images/charge/ic-phone-white.png'" class="cs-card-ic-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;">
					<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;display:block;">{{ serviceName || '小区客服' }}</text>
					<text class="c-text-sub" style="margin-top:8rpx;display:block;">暂无联系电话</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'
	import {
		getSystemPrivacy
	} from '@/api/system/systemApi.js'

	export default {
		data() {
			return {}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			serviceTel() {
				return chargeStore.serviceTel
			},
			serviceName() {
				return chargeStore.serviceName
			}
		},
		onShow() {
			if (!chargeStore.serviceTel) {
				this._loadService()
			}
		},
		methods: {
			_loadService() {
				getSystemPrivacy(getCommunityId()).then(_res => {
					if (_res && _res.data) {
						chargeStore.setService(_res.data)
					}
				}).catch(() => {})
			},
			callService() {
				if (!chargeStore.serviceTel) return
				uni.makePhoneCall({
					phoneNumber: chargeStore.serviceTel
				})
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
	}

	.cs-card-ic-img {
		width: 44rpx;
		height: 44rpx;
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
</style>
