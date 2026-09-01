<template>
	<view class="cp-page">
		<view class="cp-body">
			<text class="cp-title">修改登录密码</text>
			<text class="cp-sub">修改后请使用新密码登录</text>
			<view class="c-form-input cp-form-row">
				<text class="cuIcon-lock cp-input-ic"></text>
				<input class="cp-pwd-input" v-model="oldPwd" type="password" maxlength="20" placeholder="请输入原密码" placeholder-class="c-placeholder" />
			</view>
			<view class="c-form-input cp-form-row">
				<text class="cuIcon-lock cp-input-ic"></text>
				<input class="cp-pwd-input" v-model="newPwd" :type="showPwd ? 'text' : 'password'" maxlength="20" placeholder="设置新密码（6-20 位）" placeholder-class="c-placeholder" />
				<text class="cp-pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '隐藏' : '显示' }}</text>
			</view>
			<view class="c-form-input cp-form-row">
				<text class="cuIcon-lock cp-input-ic"></text>
				<input class="cp-pwd-input" v-model="newPwd2" :type="showPwd ? 'text' : 'password'" maxlength="20" placeholder="确认新密码" placeholder-class="c-placeholder" />
			</view>
			<view class="c-btn-primary cp-submit" @click="submitPwd">确认修改</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import {
		updateUserPwd
	} from '@/api/user/userApi.js'
	import {
		removeUserLoginInfo
	} from '@/lib/java110/utils/StorageUtil.js'

	export default {
		data() {
			return {
				oldPwd: '',
				newPwd: '',
				newPwd2: '',
				showPwd: false,
				toastMsg: ''
			}
		},
		methods: {
			submitPwd() {
				if (!this.oldPwd) {
					this._toast('请输入原密码')
					return
				}
				if (this.newPwd.length < 6 || this.newPwd.length > 20) {
					this._toast('新密码长度需 6-20 位')
					return
				}
				if (this.newPwd !== this.newPwd2) {
					this._toast('两次输入的新密码不一致')
					return
				}
				updateUserPwd({
					oldPwd: this.oldPwd,
					newPwd: this.newPwd
				}).then(() => {
					this._toast('密码修改成功，请重新登录')
					setTimeout(() => {
						removeUserLoginInfo()
						uni.removeStorageSync('userInfo')
						uni.removeStorageSync('charge_demo_user')
						uni.reLaunch({
							url: '/pages/user/login'
						})
					}, 1200)
				}).catch(err => {
					this._toast(err || '修改失败')
				})
			},
			_toast(msg) {
				this.toastMsg = msg
				clearTimeout(this._toastTimer)
				this._toastTimer = setTimeout(() => {
					this.toastMsg = ''
				}, 2000)
			}
		}
	}
</script>

<style>
	.cp-page {
		min-height: 100vh;
		background: #fff;
	}

	.cp-body {
		padding: 80rpx 48rpx 0;
	}

	.cp-title {
		display: block;
		text-align: center;
		font-size: 32rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.cp-sub {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 10rpx;
	}

	.cp-form-row {
		margin-top: 24rpx;
	}

	.cp-input-ic {
		font-size: 30rpx;
		color: #94a3b8;
		flex-shrink: 0;
	}

	.cp-pwd-toggle {
		font-size: 22rpx;
		color: #94a3b8;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.cp-pwd-input {
		flex: 1 !important;
		min-width: 0 !important;
		width: 100%;
		border: none !important;
		outline: none !important;
		-webkit-appearance: none;
		appearance: none;
	}

	.cp-submit {
		margin-top: 48rpx;
	}
</style>
