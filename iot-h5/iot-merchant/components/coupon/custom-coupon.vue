<template>
	<view>
		<view class="cu-form-group margin-top-sm">
			<view class="title">车牌号</view>
			<input placeholder="请输入车牌号"  v-model="carNum"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">优惠券</view>
			<picker @change="shopCouponChange" :value="shopCouponIndex" :range="coupons" :range-key="'name'">
				<view class="picker">
					{{shopCouponIndex > -1 ? couponName:'请选择优惠券'}}
				</view>
			</picker>
		</view>
		<view class="padding">
			<button class="cu-btn block bg-blue margin-tb-sm lg" @click="_saveCustomCoupon()" type="">手工赠送</button>
		</view>
	</view>
</template>

<script>
	import {queryShopCoupons,saveCustomCoupon} from '../../api/coupon/coupon.js';
	
	import {getCurrentShop,applyShopCode} from '../../api/shop/shop.js';
	export default {
		name:"customCoupon",
		data() {
			return {
				carNum:'',
				coupons:[{couponName:'请选择优惠券'}],
				shopCouponIndex:-1,
				couponShopId:'',
				couponName:'',
				code:''
			};
		},
		created() {
			this._loadShopCoupons();
			this._applyShopCode();
		},
		methods:{
			_loadShopCoupons:function(){
				let _that = this;
				queryShopCoupons(this,{
					page:1,
					row:100,
					shopId:getCurrentShop().shopId
				}).then(_data=>{
					_data.data.forEach(item=>{
						let _name = item.couponName+'-'+item.typeCdName+'-';
							if(item.typeCd == '1001'){
								_name += (item.value+"分钟")
							}
							if(item.typeCd == '2002'){
								_name += (item.value+"元")
							}
							if(item.typeCd == '3003'){
								_name += (item.value+"折")
							}
							if(item.typeCd == '4004'){
								_name += ("全免")
							}
							item.name = _name;
					})
					_that.coupons = _data.data;
				},err=>{
					
				})
			},
			shopCouponChange(e) {
				this.shopCouponIndex = e.detail.value;
				this.couponName = this.coupons[this.shopCouponIndex].name;
				this.couponShopId = this.coupons[this.shopCouponIndex].couponShopId;
			},
			_applyShopCode:function(){
				let _that = this;
				applyShopCode({
					shopId:getCurrentShop().shopId
				},this).then(_data=>{
					_that.code = _data.data;
				},err=>{
					
				})
			},
			_saveCustomCoupon:function(){
				if(!this.carNum){
					uni.showToast({
						icon:'none',
						title:'未填写车牌'
					});
					return ;
				}
				
				if(!this.couponShopId){
					uni.showToast({
						icon:'none',
						title:'未选择停车劵'
					});
					return ;
				}
				
				let _data = {
					shopId:getCurrentShop().shopId,
					giveWay:'2002',
					carNum:this.carNum,
					couponShopId:this.couponShopId,
					code:this.code
				}
			
				saveCustomCoupon(_data,this).then(_msg=>{
					uni.showToast({
						icon:'none',
						title:'赠送成功'
					});
					this.carNum="";
					this.couponShopId = "";
					this.couponName="";
					this.shopCouponIndex = -1;
					this._applyShopCode();
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					});
				})
				
			}
		}
	}
</script>

<style>

</style>