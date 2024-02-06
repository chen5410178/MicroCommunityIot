<template>
	<view>
		<view class="header flex justify-around align-center">
			<view class="header-item " v-for="(item,index) in headerMenus" :key="index" @click="_toCodeUrl(item)">
				<view style="position: relative;">
					<image :src="item.icon"></image>
					<view><text>{{item.name}}</text></view>
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
				headerMenus: [{
					icon: '/static/images/qrcode.png',
					name: '通行码',
					action: 'pass'
				}, {
					icon: '/static/images/qrcode-visit.png',
					name: '访客码',
					action: 'visit'
				}]
			};
		},
		methods: {
			_toCodeUrl: function(_item) {
				if (!hasLogin()) {
					uni.navigateTo({
						url: '/pages/user/login'
					});
					return;
				}

				//todo 查询房屋认证
				let _that = this;
				let _action = _item.action;
				getAppUser().then((_data) => {
					if (_action == 'pass') {
						_that.passQrcode(_data);
						return;
					}else if(_action == 'visit'){
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
					url:'/pages/user/userPassQrcode'
				})
			},
			visitQrcode:function(_data){
				uni.navigateTo({
					url:'/pages/user/visitQrcode'
				})
			}
		}
	}
</script>

<style lang="scss">
	.header {
		width: 100%;
		/** #ifndef H5 **/
		height: 320upx;
		/** #endif **/
		/** #ifdef H5 **/
		height: 320upx;
		/** #endif **/
		background-color: #368CFE;
		color: #FFFFFF;

		.header-item {
			//margin-top: 50upx;
			text-align: center;

			image {
				height: 100upx;
				width: 100upx;
			}
		}
	}
</style>