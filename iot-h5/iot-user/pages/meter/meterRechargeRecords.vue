<template>
	<view class="tab-container bg-white">
		<view class="cu-list menu">
			<view class="cu-item arrow" v-if="records.length > 0" v-for="(item, key) in records" :key="key"
				:data-item="item" @click="toDetail(item)">
				<view class="content padding-tb-sm">
					<view>
						<text
							class="cuIcon-homefill text-green margin-right-xs"></text>{{item.machineName}}
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-right margin-right-xs"></text> 记录：{{item.chargeId}}
					</view>
				</view>
				<view class="action">
					{{item.stateName}}
				</view>
			</view>
			<view class="cu-item" v-if="records.length === 0">
				<view class="content">
					<text class="cuIcon-warn text-green"></text>
					<text class="text-grey">暂无充电记录</text>
				</view>
				<view class="action">

				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {getMeterRechargeRecord} from '../../api/meter/meterApi.js'
	
	export default {
		data() {
			return {
				records: [],
				communityId: '',
				createStaffId: '',
				address: '',
				roomId: '',
				roomName: ''
			}
		},
		components: {
			
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.communityId = options.communityId;
			this.createStaffId = options.createStaffId;
			this.address = options.address;
			this.roomId = options.roomId;
			this.roomName = options.roomName;
			let _that = this;
			_that._loadMeterRechargeRecord();
		},
		methods: {
			_loadMeterRechargeRecord: function() {
				let _that = this;
				getMeterRechargeRecord({
					communityId: this.communityId,
					page: -1,
					row: 10,
					address: this.address,
					roomId: this.roomId,
					roomName: this.roomName
				}).then(_data => {
					_that.records = _data.data;
					_that.records.forEach(function(_record) {
						if(_record.state == 'C') {
							_record.stateName = '充值成功';
						}
						if(_record.state == 'W') {
							_record.stateName = '充值中';
						}
					});
				})
			},
			toDetail: function(_record) {
				uni.navigateTo({
					url: '/pages/meter/meterRechargeRecord?chargeId=' + _record.chargeId + '&communityId=' + this.communityId
				})
			}
		}
	}
</script>

<style>
	.tab-container {
		/*border: 1px solid black;*/

		margin-top: 30rpx;
	}

	.tab-item {
		padding: 20rpx 30rpx;

		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}

	.tab-item-hover {
		background-color: #e6e6e6;
	}

	.tab-icon {
		width: 30rpx;
		height: 30rpx;
	}

	.tab-text {
		display: inline-block;
		margin-left: 10rpx;
		color: #1e1e1e;
	}

	.tab-arrow {
		display: inline-block;
		width: 20rpx;
		height: 20rpx;
		border: 1px solid #cdcdcd;

		border-left: none;
		border-bottom: none;

		transform: rotate(45deg);
	}

	.border-bottom .icon {
		font-size: 38rpx;
		line-height: 38rpx;
	}
</style>