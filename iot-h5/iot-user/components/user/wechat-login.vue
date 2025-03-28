<template>
	<view class="login-container margin-top-sm">
		<!-- 确保方法名一致 -->
		<button open-type="getPhoneNumber" @getphonenumber="_getPhoneNumber" class="login-btn">
			微信手机号一键登录
		</button>
	</view>
</template>

<script>
	import {
		wechatLogin
	} from '@/api/user/userApi.js';
	import {
		saveOwnerStorage,
		saveUserLoginInfo,
		removeUserLoginInfo,
		getLoginFlag
	} from '@/lib/java110/utils/StorageUtil.js';
	export default {
		name: "wechatLogin",
		data() {
			return {
				encryptedData: '',
				iv: '',
				code: '',
				appId:''
			};
		},
		methods: {
			_initWechatLogin: function() {
				let _that = this;
				uni.login({
					success: res => {
						if (res.code) {
							_that.code = res.code;
							let accountInfo = uni.getAccountInfoSync();
							_that.appId = accountInfo.miniProgram.appId;
						} else {
							console.error('获取登录code失败:', res);
						}
					}
				});
			},
			_getPhoneNumber(e) {
				if (e.detail.errMsg === "getPhoneNumber:ok") {
					// 获取到加密数据
					const {
						encryptedData,
						iv
					} = e.detail;
					// 发送到后端解密
					this.encryptedData = encryptedData;
					this.iv = iv;
					this._loginByPhoneNumber();
				} else {
					uni.showToast({
						title: '获取手机号失败',
						icon: 'none'
					});
				}
			},
			_loginByPhoneNumber() {
				wechatLogin({
						encryptedData: this.encryptedData,
						iv: this.iv,
						code: this.code,
						appId:this.appId
					})
					.then(_user => {
						//todo 保存登录信息
						saveUserLoginInfo(_user.userId, _user.token, _user.key);
						let _communityId = _user.communityId;
						let qrcodeCommunityId = uni.getStorageSync('QRCODE_COMMUNITY_ID');
						if(qrcodeCommunityId){
							_communityId = qrcodeCommunityId;
						}
						uni.navigateTo({
							url:'/pages/user/loginInitWechat?communityId='+_communityId
						});
					}, err => {
						uni.showToast({
							icon: 'none',
							title: err
						})
					})
			}
		}
	}
</script>

<style lang="scss">
	.container {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 80rpx 40rpx;
	}

	.login-title {
		font-size: 40rpx;
		font-weight: bold;
		margin-bottom: 80rpx;
	}

	.login-btn {
		width: 100%;
		height: 80rpx;
		line-height: 90rpx;
		border-radius: 10rpx;
		background: #07C160;
		color: white;
		font-size: 32rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.wechat-icon {
		width: 40rpx;
		height: 40rpx;
		margin-right: 15rpx;
	}

	.phone-display {
		margin-top: 60rpx;
		font-size: 32rpx;
		color: #666;
	}
</style>