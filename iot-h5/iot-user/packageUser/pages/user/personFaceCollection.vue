<template>
	<view>
		<view class="flex justify-center logo">
			<view class="cu-avatar xl round margin-left logo-lage" :style="{backgroundImage: 'url(' + logoUrl + ')' }">
			</view>
		</view>
		<view class="padding flex flex-direction margin-top">
			<button class="cu-btn bg-blue lg pfc-btn" @click="_doFace()">采集</button>
		</view>
	</view>
</template>

<script>
	import {uploadImageAsync} from '@/api/user/userApi.js';
	import * as TanslateImage from '@/lib/java110/utils/translate-image.js';
	import {getCommunityId} from '@/api/community/communityApi.js';
	import {getMemberId,getOwnerFace,uploadOwnerPhoto} from '@/api/owner/ownerApi.js';
	
	export default {
		data() {
			return {
				logoUrl:'/static/images/face.png',
				memberId:'',
			}
		},
		onLoad(options) {
			this.memberId = getMemberId();
			this._loadOwnerFace();
		},
		methods: {
			_loadOwnerFace:function(){
				let _that =this;
				getOwnerFace({
					a:1
				}).then(_data=>{
					_that.logoUrl = _data;
				});
			},
			_uploadOwnerPhoto:function(photoPath){
				let _that =this;
				uploadOwnerPhoto({
					memberId:this.memberId,
					photo:photoPath,
					communityId:getCommunityId()
				}).then(_data=>{
					uni.showToast({
						icon:'none',
						title:'提交成功'
					});
					_that._loadOwnerFace();
				})
			},
			_doFace:function(){
				let that = this;
				wx.chooseImage({
					count: 1, // 选择数量
					sizeType: ['original', 'compressed'], //原图或压缩图
					sourceType: ['camera'], // 相册或拍摄
					success: (upImgRes) => {
						let tempFilePaths = upImgRes.tempFilePaths[0]
						that.$forceUpdate();
						//#ifdef H5
						TanslateImage.translate(tempFilePaths, (base64) => {
							let _objData = {
								uploadFile: base64,
								communityId: getCommunityId()
							}
							uploadImageAsync(_objData, that).then((res) => {
								console.log('res=',res)
								let _data= res.data;
								that.logoUrl = _data.url;
								that._uploadOwnerPhoto(_data.fileId);
							})
						})
						//#endif
				
						//#ifndef H5
						factory.base64.urlTobase64(tempFilePaths).then(function(base64) {
							let _objData = {
								uploadFile: base64,
								communityId: getCommunityId()
							}
							uploadImageAsync(_objData, that).then((res) => {
								let _data= res.data;
								that.logoUrl = _data.url;
								that._uploadOwnerPhoto(_data.fileId);
							})
						});
						//#endif
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	.logo {
		margin-top: 300upx;
		margin-bottom: 90upx;
	}

	.logo .logo-lage {
		height: 320upx;
		width: 320upx;
	}

	.login-nav {
		background-color: #00AA00;
		height: 120upx;
	}

	.login-nav text {
		color: #FFFFFF;
		font-size: 30upx;
	}
	.pfc-btn{
		width: 380upx;
		margin: 0 auto;
	}
</style>