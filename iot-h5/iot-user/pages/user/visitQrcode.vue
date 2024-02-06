<template>
	<view>
		<view class="margin-top">
			<view class="u-qrcode" style="text-align: center;" v-show="passcode">
				<view style="height: 120upx;"></view>
				<canvas style="width: 300px; height: 300px; margin: 0 auto;" canvas-id="myQrcode"></canvas>
				<view>访客二维码，访客扫码后申请拜访</view>
			</view>
		</view>
	</view>
</template>

<script>
	import drawQrcode from '@/components/weapp-qrcode/weapp.qrcode.esm.js'

	import {
		getVisitQrCodeUrl
	} from '../../api/owner/ownerApi.js';
	import {
		getCommunityId
	} from '../../api/community/communityApi.js';
	export default {
		data() {
			return {
				active: 0,
				communityId: '',
				passcode: '',
			}
		},
		onLoad(options) {
			this.getPassQrcode();
		},
		methods: {
			getPassQrcode: function() {
				let _that = this;
				_that.passcode = "";
				getVisitQrCodeUrl({
					communityId: getCommunityId()
				}).then(_data => {
					_that.passcode = _data.url;
					setTimeout(function() {
						_that.showQrcode()
					}, 1000);
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
			},
			
		}
	}
</script>

<style>

</style>