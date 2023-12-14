<template>
	<view>
		<view class="cu-list menu-avatar margin-top-sm" v-for="(item,index) in couponCars" :key="index" >
			<view class="cu-item arrow">
				<view class="content content-left" >
					<view class="text-grey">
						<text class="ellip">{{item.carNum}}-{{item.couponName}}-</text>
						<text v-if="item.typeCd == '1001'">{{item.value}}分钟</text>
						<text v-if="item.typeCd == '2002'">{{item.value}}元</text>
						<text v-if="item.typeCd == '3003'">{{item.value}}折</text>
						<text v-if="item.typeCd == '4004'">全免</text>
					</view>
					<view class="text-gray text-sm flex">
						<view class="text-cut">
							过期时间:{{item.endTime}}
						</view>
					</view>
				</view>
				<view  class="text-gray">
					{{item.stateName}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	
	import {queryShopCouponCars} from '../../api/coupon/coupon.js';
	import {getCurrentShop} from '../../api/shop/shop.js';
	export default {
		name:"couponCar",
		data() {
			return {
				couponCars:[]
			};
		},
		created() {
			this._loadShopCouponCars();

		},
		methods:{
			_loadShopCouponCars:function(){
				let _that = this;
				queryShopCouponCars(this,{
					page:1,
					row:100,
					shopId:getCurrentShop().shopId
				}).then(_data=>{
					_that.couponCars = _data.data;
				},err=>{
					
				})
			},
			
			_toShowQrCodePage:function(_item){
				uni.navigateTo({
					url:'/pages/coupon/showQrCodePage?couponShopId='+_item.couponShopId
				})
			}
		}
	}
</script>

<style>
    .cu-list.menu-avatar>.cu-item .content-left {
		left: 30upx;
	}
	.rob-order{
		width: 80upx;
		height: 80upx;
		line-height: 80upx;
		border-radius: 50%;
	}
</style>