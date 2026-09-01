<template>
	<view>
		<view class="block__title">选择表</view>
		<view class="cu-list menu ">
			<view class="cu-item  arrow" v-for="(meter, idx) in meters" :key="idx">
				<view class="content padding-tb-sm" is-link @click="chooseMeter(meter)">
					<view>
						<text
							class="cuIcon-clothesfill text-blue margin-right-xs"></text>{{meter.machineName}}({{meter.roomName}})
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-infofill margin-right-xs"></text>表号：{{meter.address}}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		listMeterMachine
	} from '@/packageMeter/api/meterApi.js'
	export default {
		data() {
			return {
				communityId: '',
				roomId: '',
				roomName: '',
				createStaffId: '',
				meters: [],
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.roomId = options.roomId;
			this.roomName = options.roomName;
			this.createStaffId = options.createStaffId;
			this._loadMeters();
		},
		methods: {
			_loadMeters: function() {
				let _that = this;
				listMeterMachine({
					page: -1,
					row: 50,
					communityId: this.communityId,
					roomId: this.roomId,
					roomName: this.roomName
				}).then((_meters) => {
					_that.meters = _meters.data;
				})
			},
			chooseMeter: function(_meter) {
				uni.navigateTo({
					url: '/packageMeter/pages/meter/preStoreMeter?communityId=' + this.communityId + '&createStaffId=' +
						this.createStaffId +
						"&address=" + _meter.address
				})
			},
		}
	}
</script>

<style lang="scss">
	.block__title {
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 60rpx 30rpx 20rpx;
	}
</style>