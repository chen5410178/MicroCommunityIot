<template>
	<view>
		<view class="header flex justify-between">
			<view class="header-item flex justify-between"  @click="_toCodeUrl('pass')">
				<view class="h-i-name"><text>通行码</text></view>
				<view>
					<image src="/static/images/qrcode.png"></image>
				</view>
				
			</view>
			<view class="header-item flex justify-between"  @click="_toCodeUrl('visit')">
				<view class="h-i-name"><text>访客码</text></view>
				<view>
					<image src="/static/images/qrcode-visit.png"></image>
				</view>
				
			</view>
		</view>
	</view>
</template>

<script>
	import {
		hasLogin
	} from '../../api/user/sessionApi.js';
	import {
		getAppUser
	} from '@/api/user/appUser.js';
	export default {
		name: "indexHeader",
		data() {
			return {
				
			};
		},
		methods: {
			_toCodeUrl: function(_action) {
				if (!hasLogin()) {
					uni.navigateTo({
						url: '/pages/user/login'
					});
					return;
				}

				//todo 查询房屋认证
				let _that = this;
				getAppUser().then((_data) => {
					if (_action == 'pass') {
						_that.passQrcode(_data);
						return;
					} else if (_action == 'visit') {
						_that.visitQrcode(_data);
						return;
					}
				}, _err => {
					uni.showToast({
						icon: 'none',
						title: '请先房屋认证'
					})
				});
			},
			passQrcode: function(_data) {
				uni.navigateTo({
					url: '/packageUser/pages/user/userPassQrcode'
				})
			},
			visitQrcode: function(_data) {
				uni.navigateTo({
					url: '/packageUser/pages/user/visitQrcode'
				})
			}
		}
	}
</script>

<style lang="scss">
	.header {
		width: 100%;
		//color: #FFFFFF;

		.header-item {
			padding:30upx;
			background-color: #FFFFFF;
			width: 48%;
			border-radius: 10upx;
			//margin-top: 50upx;
			text-align: center;
			font-size: 32upx;
			color: #323437;
			line-height: 1;
			
			.h-i-name{
				line-height: 58upx;
			}

			image {
				height: 60upx;
				width: 60upx;
			}
		}
	}
</style>