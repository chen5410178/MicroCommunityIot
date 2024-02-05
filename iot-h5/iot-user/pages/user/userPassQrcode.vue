<template>
	<view>
		<view v-show="active == 0" class="margin-top">
			<view v-if="!passcode">
				<view style="height: 300upx;"></view>
				<view class="flex flex-direction padding">
					<button class="cu-btn bg-blue margin-tb-sm lg" @click="_applyPassQrcode()">通行码申请</button>
				</view>
			</view>

			<view class="u-qrcode" style="text-align: center;" v-show="passcode">
				<view style="height: 120upx;"></view>
				<canvas style="width: 300px; height: 300px; margin: 0 auto;" canvas-id="myQrcode"></canvas>
				<!-- 	<image :src="tempFilePath" style="width: 200px; height: 200px; margin: 0 auto;"></image> -->
				<view>通行二维码，{{expire}}后过期</view>
			</view>

		</view>
		<view v-show="active == 1" class="margin-top">

		</view>
	</view>
</template>

<script>
	import drawQrcode from '@/components/weapp-qrcode/weapp.qrcode.esm.js'

	import {
		refreshUserPassQrcode
	} from '../../api/owner/ownerApi.js';
	export default {
		data() {
			return {
				active: 0,
				communityId: '',
				pqsId: '',
				passcode: '',
				expire: ''
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.pqsId = options.pqsId;
			this.getPassQrcode();
		},
		methods: {
			tabSelect: function(_active) {
				this.active = _active;
				this.getPassQrcode();
			},
			getPassQrcode: function() {
				let _that = this;
				_that.passcode = "";
				refreshUserPassQrcode({
					openId: this.openId,
					communityId: this.communityId
				}).then(_data => {
					if (_data.data && _data.data.length > 0) {
						_that.passcode = _data.data[0].qrcode;
						_that.expire = _data.data[0].expire;
						setTimeout(function() {
							_that.showQrcode()
						}, 1000);
					}
				})
			},
			showQrcode: function() {
				drawQrcode({
					width: 280,
					height: 280,
					canvasId: 'myQrcode',
					text: this.passcode,
					callback: function(e) {}
				})
			}
		}
	}
</script>

<style>

</style>