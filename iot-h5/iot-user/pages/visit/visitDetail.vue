<template>
	<view>
		<view class="block__title">
			<text>访客二维码</text>
			<button class="cu-btn bg-blue" @click="_toTempFilePath()">保存</button>
		</view>
		<view class="u-qrcode" style="text-align: center;">
			<canvas style="width: 200px; height: 200px; margin: 0 auto;" canvas-id="myQrcode" v-show="!needLongTapSaveImg"></canvas>
			<image :src="tempFilePath" style="width: 200px; height: 200px; margin: 0 auto;" v-show="needLongTapSaveImg"></image>
		</view>
		
		<view class="block__title">
			<text>来访信息</text>
		</view>
		<view class="bg-white margin-bottom padding">
			<view class="flex padding-bottom-xs solid-bottom justify-between">
				<view>审核状态</view>
				<view class="text-gray">{{visitInfo.stateName}}</view>
			</view>
			<view class="flex margin-top justify-between">
				<view class="text-gray">创建时间</view>
				<view class="text-gray">{{visitInfo.createTime}}</view>
			</view>
			<view class="flex margin-top justify-between">
				<view class="text-gray">访客姓名</view>
				<view class="text-gray">{{visitInfo.name}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">访客联系方式</view>
				<view class="text-gray">{{visitInfo.phoneNumber}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">访客性别</view>
				<view class="text-gray">{{visitInfo.visitGender=='0'?'男':'女'}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">访客类型</view>
				<view class="text-gray">{{visitInfo.visitTypeName}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray w100">来访事由</view>
				<view class="text-gray">{{visitInfo.visitCase}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">预计来访时间</view>
				<view class="text-gray">{{visitInfo.visitTime}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">预计离开时间</view>
				<view class="text-gray">{{visitInfo.departureTime}}</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-if="visitInfo.roomName">
				<view class="text-gray">拜访房屋</view>
				<view class="text-gray">{{visitInfo.roomName}}</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-if="visitInfo.ownerName">
				<view class="text-gray">拜访业主</view>
				<view class="text-gray">{{visitInfo.ownerName}}</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-show="visitInfo.msg">
				<view class="text-gray w100">审核备注</view>
				<view class="text-gray">{{visitInfo.msg}}</view>
			</view>
		</view>
	</view>
</template>

<script>
	import context from '../../lib/java110/Java110Context.js';
	import {listOwnerVisit}  from '../../api/visit/visitApi.js'
	import drawQrcode from '@/components/weapp-qrcode/weapp.qrcode.esm.js'
	const constant = context.constant;
	const util = context.util;
	export default {
		data() {
			return {
				visitInfo: {},
				ownerInfo: {},
				qrcodeValue: '',
				tempFilePath: '',
				needLongTapSaveImg: false,
			}
		},
		components: {
		},

		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			let _that = this;
			this.$nextTick(() => {
				drawQrcode({
				  width: 200,
				  height: 200,
				  canvasId: 'myQrcode',
				  // ctx: wx.createCanvasContext('myQrcode'),
				  text: options.qrcode,
				  callback: function(e){
					uni.canvasToTempFilePath({
						x: 0,
						y: 0,
						width: 400,
						height: 400,
						destWidth: 400,
						destHeight: 400,
						canvasId: 'myQrcode',
						success: function(res) {
							// 在H5平台下，tempFilePath 为 base64
							_that.tempFilePath = res.tempFilePath
							//#ifdef H5
							_that.needLongTapSaveImg = true;
							//#endif
						} 
					})
				  }
				})
			});
			_that._loadVisitInfo(options.vId);
		},
		onReachBottom : function(){},
		methods: {
			_toTempFilePath: function() {
				if(this.tempFilePath == ''){
					uni.showToast({
						title: '图片未生成',
						icon: 'none'
					})
					return;
				}
				//#ifndef H5
				uni.saveImageToPhotosAlbum({
					filePath: this.tempFilePath,
					success: function() {
						uni.showToast({
							title: '已保存至相册',
							icon: 'none'
						})
					}
				});
				//#endif
				//#ifdef H5
				uni.showToast({
					title: '请长按图片-保存至相册'
				})
				//#endif
			},
			
			_loadVisitInfo: function(vId){
				let _that = this;
				let _objData = {
					page: 1,
					row: 1,
					visitId: vId,
					ownerId: this.ownerInfo.ownerId,
					userId: this.ownerInfo.userId,
					communityId: this.ownerInfo.communityId
				}
				listOwnerVisit(_objData).then(function(_visits){
					_that.visitInfo = _visits.data[0];
				})
			}
		}
	}
</script>

<style>
	.solid-bottom::after {
		border-bottom: 2upx solid rgba(0, 0, 0, 0.1);
	}

	.ppf_item {
		padding: 0rpx 0rpx 0rpx 0rpx;
	}

	.block__title {
		display: flex;
		justify-content: space-between;
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 40rpx 30rpx 20rpx;
	}

	.button_up_blank {
		height: 40rpx;
	}

	.block__bottom {
		height: 180rpx;
	}
	.u-qrcode{
		width: 256px;
		margin: 0 auto;
	}
	.w100{
		width: 500rpx;
	}
</style>
