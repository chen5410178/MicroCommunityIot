<template>
	<view>
		<view class="cu-bar bg-white solid-bottom " v-if="orderAddress && orderAddress.username">
			<view class="action">
				<text class="cuIcon-title text-orange "></text>收货信息
			</view>
			<view class="action">
			</view>
		</view>
		<view class="cu-list menu" v-if="orderAddress && orderAddress.username">
			<view class="cu-item">
				<view class="title">用户名</view>
				<text>{{orderAddress.username}}</text>
			</view>
			<view class="cu-item">
				<view class="title">手机号</view>
				<text>{{orderAddress.tel}}</text>
			</view>
			<view class="cu-item">
				<view class="title">收货地址</view>
				<text>{{orderInfo.address}}</text>
			</view>
			<view class="cu-item">
				<view class="title">备注</view>
				<text>{{orderInfo.remark}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import {queryStoreOrderAddress} from '@/api/order/orderApi.js'
	export default {
		name:"orderAddress",
		data() {
			return {
				orderAddress:{
					
				}
			};
		},
		created() {
			this._queryStoreOrderAddress();
		},
		props:{
			orderId:{
				type:String,
				require:true
			}
		},
		methods:{
			_queryStoreOrderAddress: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				queryStoreOrderAddress(this, {
					page: 1,
					row: 1,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					let _datas = _data.data
					if (_datas && _datas.length > 0) {
						this.orderAddress = _datas[0]
					}
				})
			},
		}
		
	}
</script>

<style>

</style>