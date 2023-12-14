<template>
	<view>
		<scroll-view scroll-x class="bg-white nav">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="active==0?'text-green cur':''" @tap="tabSelect(0)">
					扫码领劵
				</view>
				<view class="cu-item flex-sub" :class="active==1?'text-green cur':''" @tap="tabSelect(1)">
					手工送劵
				</view>
				<view class="cu-item flex-sub" :class="active==2?'text-green cur':''" @tap="tabSelect(2)">
					订单
				</view>
			</view>
		</scroll-view>
		<view v-if="active == 0">
			<qr-code-receive></qr-code-receive>
		</view>
		<view v-if="active == 1">
			<custom-coupon></custom-coupon>
		</view>
		<view v-if="active == 2">
			<coupon-car></coupon-car>
		</view>

	</view>
</template>
<script>
	import qrCodeReceive from '../../components/coupon/qrcode-receive.vue';
	
	import customCoupon from '../../components/coupon/custom-coupon.vue';
	
	import couponCar from '../../components/coupon/coupon-car.vue'
	export default {
		data() {
			return {
				active: 0,
			};
		},
		onLoad: function(options) {
			
		},
		components:{
			qrCodeReceive,
			customCoupon,
			couponCar
		},
		/**
		 * 页面相关事件处理函数--监听用户下拉动作
		 */
		onPullDownRefresh: function() {

		},

		methods: {
			tabSelect: function(_active) {
				this.active = _active;
				//this._loadCompaint(_active);
			},
			

		}
	};
</script>
<style>
</style>
