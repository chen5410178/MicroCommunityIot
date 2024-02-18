<template>
	<view>
		<view class="cu-list menu  margin-top margin-bottom">
			<view class="cu-item arrow" @click="_changeCommunity()">
				<view class="content">
					<text class="cuIcon-settings text-gray"></text>
					<text class="text-grey">切换小区</text>
				</view>
			</view>
			<view class="cu-item arrow" @tap="_logout()" v-if="login">
				<view class="content">
					<text class="cuIcon-circlefill text-yellow"></text>
					<text class="text-grey">退出登录</text>
				</view>
			</view>
		</view>

		<view class="cu-modal" :class="logoutUser==true?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">退出系统</view>
					<view class="action" @tap="_cancleLogout()">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					您确认退出系统吗？
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action margin-0 flex-sub  solid-left" @tap="_cancleLogout()">取消</view>
					<view class="action margin-0 flex-sub text-green solid-left" @tap="_doLogoutUser()">确定</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>

import {hasLogin} from '../../api/user/sessionApi.js';
import {userLogout} from '../../api/user/userApi.js';
	export default {
		name: "my-system",
		data() {
			return {
				logoutUser: false,
				login: true
			};
		},
		created() {
			this._judgeHasLogin();
		},
		methods: {
			_judgeHasLogin: function() {
				let _that = this; //查询用户信息
				if (!_that.ckeckUserInfo()) {
					_that.login = false;
					return;
				}
				_that.login = true;
			},
			ckeckUserInfo: function() {
				return hasLogin();
			},
			// 设置
			_changeCommunity: function() {
				if (!hasLogin()) {
					uni.navigateTo({
						url:'/pages/user/login'
					})
					return;
				}
				this.vc.navigateTo({
					url: '/pages/property/changeCommunity',
				});
			},
			//退出系统
			_logout: function() {
				this.logoutUser = true;
			},
			_cancleLogout: function() {
				this.logoutUser = false;
			},
			_doLogoutUser: function() {
				let _that = this;
				userLogout({
					token: uni.getStorageSync('token')
				}).then(_data=>{
					_that.logoutUser = false;
					uni.navigateTo({
						url:'/pages/user/login'
					});
				});
			}
		}
	}
</script>

<style>

</style>
