<template>
	<view class="tab-container bg-white">
		<view class="cu-list menu">
			<view class="cu-item arrow" v-if="orders.length > 0" v-for="(item, key) in orders" :key="key"
				:data-item="item" @click="toDetail(item)">
				<view class="content padding-tb-sm">
					<view>
						<text
							class="cuIcon-homefill text-green margin-right-xs"></text>{{item.machineName}}-{{item.portName}}
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-right margin-right-xs"></text> 订单：{{item.orderId}}
					</view>
				</view>
				<view class="action">
					{{item.remark || item.stateName}}
				</view>
			</view>
			<view class="cu-item" v-if="orders.length === 0">
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
	import noDataPage from '@/components/no-data-page/no-data-page.vue';
	import {
		getChargeMachineOrder
	} from '@/api/machine/machineApi.js';
	



	import {
		getUserTel
	} from '@/api/user/userApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'
	export default {
		data() {
			return {
				orders: [],
				personTel: '',
				communityId:''
			}
		},
		components: {
			noDataPage
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.communityId = options.communityId;
			if(!this.communityId){
				this.communityId = getCommunityId();
			}
			let _that = this;
			_that._loadChargeMachineOrder();
		},
		onShow() {
			this._loadChargeMachineOrder();
		},
		methods: {
			_loadChargeMachineOrder: function() {
				let _that = this;
				getChargeMachineOrder({
					communityId:this.communityId,
					page: 1,
					row: 15,
					personTel: getUserTel()
				}).then(_data => {
					_that.orders = _data;
				})
			},
			toDetail: function(_order) {
				if(_order.chargeType =="1001"){
					uni.navigateTo({
						url: '/packageCharge/pages/charge/chargeMachineOrder?orderId=' + _order.orderId+'&communityId='+this.communityId
					})
				}else{
					uni.navigateTo({
						url: '/packageCharge/pages/charge/chargeCarOrder?orderId=' + _order.orderId+'&communityId='+this.communityId
					})
				}
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