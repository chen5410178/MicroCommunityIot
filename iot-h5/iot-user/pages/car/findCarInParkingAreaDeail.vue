<template>
	<view>
		<scroll-view scroll-y>
			<view class="block__title">车辆信息</view>
			<view class="cu-list menu fee-last">
				<view class="cu-item">
					<view class="content">
						<text class="text-grey">车辆</text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{carNum }}</text>
					</view>
				</view>
				<view class="cu-item">
					<view class="content">
						<text class="text-grey">停车时间</text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{updateTime }}</text>
					</view>
				</view>
				<view class="cu-item">
					<view class="content">
						<text class="text-grey">停车场</text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{areaNum }}</text>
					</view>
				</view>
				<view class="cu-item">
					<view class="content">
						<text class="text-grey">停车位</text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{psNum }}</text>
					</view>
				</view>
				<view class="cu-item">
					<view class="content">
						<text class="text-grey">照片</text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{imagePhoto }}</text>
					</view>
				</view>
				
				<view class="cu-bar btn-group" style="margin-top: 20upx;">
					<button @click="onReQuery()"
						class="cu-btn bg-grey shadow-blur round lg">重新查询</button>
				</view>
			</view>
		</scroll-view>
		
	</view>
	
</template>

<script>

	
	import {
		getCarInParkingArea
	} from '@/api/car/carApi.js';
	
	export default {
		data() {
			return {
				carNum: '',
				paId: '',
				communityId: '',
				updateTime:'',
				areaNum:'',
				psNum:'',
				imagePhoto:''
			};
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.paId = options.paId;
			this.carNum = options.carNum;
			this.communityId = options.communityId;
			this._findCar();
		},
		methods: {
			_findCar: function() {
				let _that = this;
				getCarInParkingArea({
					paId: this.paId,
					carNum: this.carNum
				}).then(_data => {
					if (_data.data <1) {
						uni.showToast({
							icon: 'none',
							title: '未查到停车费'
						})
						return;
					}
					let data = _data.data[0];
					_that.updateTime = data.updateTime;
					_that.areaNum = data.areaNum;
					_that.psNum = data.psNum;
					_that.imagePhoto = data.imagePhoto;
				})
			},
			onReQuery:function(){
				uni.navigateBack({
					delta:1
				})
			}
			
		}
	};
</script>
<style>
	.ppf_item {
		padding: 0rpx 0rpx 0rpx 0rpx;
	}

	.block__title {
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 40rpx 30rpx 20rpx;
	}

	.button_up_blank {
		height: 40rpx;
	}

	.block__bottom {
		height: 180rpx;
	}

	.fee-last {
		margin-bottom: 100upx;
	}

	.cu-btn.lgplus {
		padding: 0 20px;
		font-size: 18px;
		height: 100upx;

	}

	.cu-btn.sharp {
		border-radius: 0upx;
	}

	.line-height {
		line-height: 100upx;
	}
	.temp-remark{
		color: #888888;
		padding:10upx;
	}
</style>
