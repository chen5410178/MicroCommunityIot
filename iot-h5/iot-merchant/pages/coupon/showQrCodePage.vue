<template>
	<view>
		<view class="padding-xl margin-top">
			<canvas style="width: 520upx;height: 520upx;  margin: 0 auto;" canvas-id="couponQrcode"></canvas>
		</view>
		
		<view class="padding flex flex-direction margin-top">
			<button class="cu-btn bg-blue margin-tb-sm lg" @tap="_closeModal()">关闭</button>
		</view>
	</view>
</template>

<script>
	const qrCode = require('@/lib/weapp-qrcode.js');
	import {getCurrentShop,applyShopCode} from '../../api/shop/shop.js';
	import {hasSession} from '../../api/user/sessionApi.js';
	export default {
		data() {
			return {
				qrCode: '',
				couponShopId:'',
				code:'',
				shopId:'',
			}
		},
		onLoad(options) {
			this.vc.onLoad(options);
			this.couponShopId = options.couponShopId;
			this.code = options.code;
			this.shopId = options.shopId;
			this._applyShopCode();
			
		},
		onShow() {
			hasSession();
		},
		methods: {
			_generatorQrCode: function() {
				let _that = this;
				let _config = uni.getStorageSync('java110SystemConfig');
				this.qrCode = _config.mallUrl+"/#/pages/coupon/getParkingCoupon?code="+this.code+"&couponShopId="+this.couponShopId+"&shopId="+this.shopId
				console.log(_that.qrCode)
				setTimeout(function() {
					new qrCode('couponQrcode', {
						text: _that.qrCode,
						width: 250,
						height: 250,
						colorDark: "#333333",
						colorLight: "#FFFFFF",
						correctLevel: qrCode.CorrectLevel.L
					});
				}, 1000)
			},
			_applyShopCode:function(){
				let _that = this;
				applyShopCode({
					shopId:getCurrentShop().shopId
				},this).then(_data=>{
					_that.code = _data.data;
					_that._generatorQrCode();
				},err=>{
					
				})
			},
			_closeModal: function() {
				uni.navigateBack({
					delta: 1
				})
			}
		}
	}
</script>

<style>

</style>
