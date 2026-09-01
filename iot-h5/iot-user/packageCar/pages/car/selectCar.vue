<template>
	<view>
		<view class="block__title">选择车辆</view>
		<view class="cu-list menu ">
			<view class="cu-item  arrow" v-for="(car, idx) in cars" :key="idx">
				<view class="content padding-tb-sm" is-link @click="chooseCar(car)">
					<view>
						<text
							class="cuIcon-clothesfill text-blue margin-right-xs"></text>{{car.carNum}}({{car.carTypeCdName}})
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-infofill margin-right-xs"></text>{{car.areaNum}}停车场{{car.num}}车位
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserCar
	} from '@/api/car/carApi.js';
	import {getUserId} from '@/api/user/userApi.js';
	import {getCommunityId} from '@/api/community/communityApi.js'
	export default {
		data() {
			return {
				communityId: '',
				createStaffId: '',
				cars: [],
			}
		},
		onLoad(options) {
			this.createStaffId = getUserId();
			this._loadCars();
		},
		methods: {
			_loadCars: function() {
				let _that = this;
				getUserCar({
					page: 1,
					row: 30,
					communityId: getCommunityId(),
				}).then((cars) => {
					_that.cars = cars;
				})
			},
			chooseCar: function(_car) {
				uni.navigateTo({
					url: '/packageFee/pages/fee/buyCarMonthCard?communityId=' + getCommunityId() + '&createUserId=' +
						this.createStaffId +
						"&carNum=" + _car.carNum
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