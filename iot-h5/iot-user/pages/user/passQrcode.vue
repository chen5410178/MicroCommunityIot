<template>
	<view>
		<scroll-view scroll-x class="bg-white nav ">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="active==0?'text-blue cur':''" @tap="tabSelect(0)">
					通行码
				</view>
				<view class="cu-item flex-sub" :class="active==1?'text-blue cur':''" @tap="tabSelect(1)">
					远程开门
				</view>
			</view>
		</scroll-view>
		<view v-show="active == 0" class="margin-top">
			<view v-if="!passcode">
				<apply-pass-qrcode :communityId='communityId' :openId="openId" :pqsId="pqsId"
					:getPassQrcode="getPassQrcode"></apply-pass-qrcode>
			</view>

			<view class="u-qrcode" style="text-align: center;" v-show="passcode">
				<canvas style="width: 200px; height: 200px; margin: 0 auto;" canvas-id="myQrcode"></canvas>
			<!-- 	<image :src="tempFilePath" style="width: 200px; height: 200px; margin: 0 auto;"></image> -->
			</view>

		</view>
		<view v-show="active == 1" class="margin-top">

		</view>
	</view>
</template>

<script>
	import applyPassQrcode from '../../components/pass/applyPassQrcode.vue';
	import drawQrcode from '@/components/weapp-qrcode/weapp.qrcode.esm.js'
	
	import {
		refreshPassQrcode
	} from '../../api/owner/ownerApi.js';
	export default {
		data() {
			return {
				active: 0,
				communityId: '',
				pqsId: '',
				openId: '123123123',
				passcode: ''
			}
		},
		components: {
			applyPassQrcode
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.pqsId = options.pqsId;
			this.getPassQrcode();
		},
		methods: {
			tabSelect: function(_active) {
				this.active = _active;
				if (this.active == 1) {
					//this.$refs.applyFaceDetailRef.loadApplyFaceDetail();
				} else {
					this.getPassQrcode();
				}
			},
			getPassQrcode: function() {
				let _that = this;
				_that.passcode = "";
				refreshPassQrcode({
					openId: this.openId,
					communityId: this.communityId
				}).then(_data => {
					if (_data && _data.length > 0) {
						_that.passcode = _data[0].qrcode;
						drawQrcode({
						  width: 200,
						  height: 200,
						  canvasId: 'myQrcode',
						  // ctx: wx.createCanvasContext('myQrcode'),
						  text: _that.passcode,
						  callback: function(e){
						  }
						})
					}
				})
			}
		}
	}
</script>

<style>

</style>