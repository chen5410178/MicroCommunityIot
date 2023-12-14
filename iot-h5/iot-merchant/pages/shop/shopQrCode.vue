<template>
	<view>
		<view class="padding-xl margin-top">
			<canvas style="width: 500upx;height: 500upx;  margin: 0 auto;" canvas-id="oweFeeQrcode"></canvas>
		</view>

		<view class="padding flex flex-direction margin-top">
			<button class="cu-btn bg-blue margin-tb-sm lg" @tap="_closeModal()">关闭</button>
		</view>
	</view>
</template>

<script>
	const qrCode = require('@/lib/weapp-qrcode.js')

	export default {
		data() {
			return {
				qrCode: ''
			}
		},
		onLoad(options) {
			this.vc.onLoad(options);
			this._generatorQrCode();
		},
		methods: {
			_generatorQrCode: function() {
				let _that = this;
				_that.qrCode = this.getCurrentShop().phoneIndexUrl;
				console.log(_that.qrCode)
				setTimeout(function() {
					new qrCode('oweFeeQrcode', {
						text: _that.qrCode,
						width: 250,
						height: 250,
						colorDark: "#333333",
						colorLight: "#FFFFFF",
						correctLevel: qrCode.CorrectLevel.L
					});
				}, 1000)
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
