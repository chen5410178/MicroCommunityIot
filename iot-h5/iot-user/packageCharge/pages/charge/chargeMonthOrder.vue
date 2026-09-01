<template>
	<view>
		<view class="margin-top" v-if="orders.length > 0">
			<view class="bg-white margin-bottom padding-sm margin-sm radius-sm" v-for="(item,index) in orders"
				:key="index">
				<view class="apply-title flex justify-between">
					<view>
						<text class="text-bold">{{item.cardName}}</text>
					</view>
					<view class="flex justify-start" >
						{{item.dayHours}}小时
					</view>
				</view>
				<view class="apply-content flex justify-start flex-wrap">
					<view class="item">
						<text>有效期:</text>
						<text >{{item.startTime}}
                            ~{{item.endTime}}</text>
					</view>
					
					<view class="item">
						<text>购买时间:</text>
						<text>{{item.createTime}}</text>
					</view>
					<view class="item">
						<text>收银员:</text>
						<text>{{item.cashierName || '-'}}</text>
					</view>
					<view class="item">
						<text>金额:</text>
						<text>{{item.receivedAmount || '-'}}</text>
					</view>
					<view class="item">
						<text>订单编号:</text>
						<text>{{item.orderId || '-'}}</text>
					</view>
					
				</view>
			</view>
		</view>
		<view v-else>
			<no-data-page></no-data-page>
		</view>
	</view>
</template>

<script>
	import {getUserChargeMonthOrder} from '@/api/machine/machineApi.js';
	import {getCommunityId} from '@/api/community/communityApi.js';
	import noDataPage from '@/components/no-data-page/no-data-page.vue'
	export default {
		data() {
			return {
				orders:[]
			}
		},
		onLoad(options) {
			this.loadChargeMonthOrders();
		},
		components:{
			noDataPage,
		},
		methods: {
			loadChargeMonthOrders:function(){
				let _that =this;
				getUserChargeMonthOrder({
					communityId:getCommunityId()
				}).then(_data=>{
					_that.orders = _data;
				})
			}
		}
	}
</script>

<style lang="scss">
.q-query {
		background-color: #FFF;
		padding: 15upx;

		.q-item {
			width: 30%;
			margin-left: 15upx;
			padding: 10upx 15upx;
			border-radius: 15upx;
			background-color: #f1f1f1;

			.q-input {
				height: 40upx;
			}

			margin-bottom: 15upx;
		}

		.q-item-btn {
			width: 30%;
			margin-left: 15upx;

			.q-input {
				height: 60upx;
			}
		}
	}
	.apply-title {
		height: 60upx;
		line-height: 50upx;
		border-bottom: 1upx solid #F1F1F1;
	}
	
	.apply-content {
		.item {
			width: 50%;
			margin-top: 20upx;
		}
	}
	
	.radius-sm {
		border-radius: 16upx;
	}
</style>

