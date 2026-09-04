<template>
	<view>
		<view class="success-msg">
			<view class="padding flex align-center">
				<view class="flex-sub text-center">
					<view class="text-xsl">
						<text class="cuIcon-roundcheckfill text-green"></text>
					</view>
					<view class="text-msg">车辆进场成功</view>
				</view>
			</view>
		</view>

		<view class="info-card bg-white">
			<view class="info-item">
				<text class="label">车牌号</text>
				<text class="value plate">{{ carNum }}</text>
			</view>
			<view class="info-item">
				<text class="label">进场时间</text>
				<text class="value">{{ inTime }}</text>
			</view>
		</view>

		<view class="padding flex flex-direction margin-top">
			<button class="cu-btn bg-green lg" @click="_goHome()">去首页</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				carNum: '',
				inTime: ''
			}
		},
		onLoad(options) {
			this.carNum = decodeURIComponent(options.carNum || '');
			this.inTime = decodeURIComponent(options.inTime || '');
		},
		methods: {
			_goHome: function() {
				uni.reLaunch({
					url: '/pages/index/index'
				});
			}
		}
	}
</script>

<style>
	.success-msg {
		padding-top: 100rpx;
		padding-bottom: 50rpx;
	}

	.text-xsl {
		font-size: 180rpx;
		line-height: 1.2;
	}

	.text-msg {
		font-size: 40rpx;
		padding-top: 20rpx;
	}

	.info-card {
		margin: 0 30rpx;
		border-radius: 16rpx;
		padding: 20rpx 30rpx 30rpx 30rpx;
	}

	.info-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #eeeeee;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.label {
		width: 160rpx;
		font-size: 30rpx;
		color: #888888;
	}

	.value {
		font-size: 30rpx;
		color: #333333;
	}

	.plate {
		font-size: 48rpx;
		font-weight: 700;
		letter-spacing: 4rpx;
	}

	.margin-top {
		margin-top: 60rpx;
	}
</style>
