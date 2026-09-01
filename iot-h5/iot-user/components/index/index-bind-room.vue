<template>
	<view>
		<view class="index-undo radius-lg"  v-if="hasOwner == 'N'">
			<view class="undo-menu flex justify-start flex-wrap">
				<view class="menu-item" @click="_toBindRoom()">
					<view style="position: relative;">
						<image src="/static/images/bindRoom.png"></image>
						<view><text>认证房屋</text></view>
					</view>
				</view>
			</view>
		</view>

		<view class="index-undo radius-lg"  v-if="hasOwner == 'D'">
			<view class="undo-menu flex justify-start flex-wrap">
				<view class="menu-item">
					<view style="position: relative;">
						<image src="/static/images/bindRoom.png"></image>
						<view><text>认证审核中</text></view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		hasLogin
	} from '@/api/user/sessionApi.js';
	export default {
		name: "indexBindRoom",
		data() {
			return {

			};
		},
		props:{
			hasOwner:{
				type: String,
				default: 'W',
			},
		},
		methods: {
			_toBindRoom: function() {
				//todo 没有登陆跳转至登陆页面
				if (!hasLogin()) {
					uni.navigateTo({
						url: '/pages/user/login'
					});
					return;
				}
				uni.navigateTo({
					url: '/packageProperty/pages/roomCertification/roomCertification'
				})
			}
		}
	}
</script>

<style lang="scss">
	.index-undo {
		background-color: #ffffff;


		.undo-menu {
			padding: 20upx;

			.menu-item {

				width: 100%;
				padding: 50upx;
				text-align: center;

				image {
					width: 160upx;
					height: 160upx;
				}
			}
		}
	}
</style>