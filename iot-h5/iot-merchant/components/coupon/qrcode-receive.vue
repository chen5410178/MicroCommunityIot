<template>
	<view>
		<view class="cu-list menu-avatar margin-top-sm" v-for="(item,index) in coupons" :key="index" @tap="_toShowQrCodePage(item)">
			<view class="cu-item arrow">
				<view class="content content-left" >
					<view class="text-grey">
						<text class="ellip">{{item.couponName}}-{{item.typeCdName}}-
							<span v-if="item.typeCd == '1001'">{{item.value}}分钟</span>
							<span v-if="item.typeCd == '2002'">{{item.value}}元</span>
							<span v-if="item.typeCd == '3003'">{{item.value}}折</span>
							<span v-if="item.typeCd == '4004'">全免</span>
						</text>
					</view>
					<view class="text-gray text-sm flex">
						<view class="text-cut">
							{{item.communityName}}-{{item.paName}}
						</view>
					</view>
				</view>
				<view  class="rob-order text-df text-white bg-green text-center">
					{{item.quantity}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {queryShopCoupons} from '../../api/coupon/coupon.js';
	
	import {getCurrentShop} from '../../api/shop/shop.js';
	export default {
		name:"qrCodeReceive",
		data() {
			return {
				coupons:[],
			};
		},
		created() {
			this._loadShopCoupons();
		},
		methods:{
			_loadShopCoupons:function(){
				let _that = this;
				queryShopCoupons(this,{
					page:1,
					row:100,
					shopId:getCurrentShop().shopId
				}).then(_data=>{
					_that.coupons = _data.data;
				},err=>{
					
				})
			},
			
			_toShowQrCodePage:function(_item){
				uni.navigateTo({
					url:'/pages/coupon/showQrCodePage?couponShopId='+_item.couponShopId+"&shopId="+getCurrentShop().shopId
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