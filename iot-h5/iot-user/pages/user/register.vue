<template>
	<view class="rg-page">
		<view class="rg-body">
			<view class="rg-logo">
				<image :src="imgUrl + '/h5/images/common/ic-user-plus.png'" class="rg-logo-img" mode="aspectFit"></image>
			</view>
			<text class="rg-title">注册充电账号</text>
			<text class="rg-sub">注册成功后自动登录 · 充电记录与余额同步</text>

			<view class="c-form-input rg-form-row">
				<text style="font-size:24rpx;font-weight:600;color:#94a3b8;">+86</text>
				<text style="width:1rpx;height:32rpx;background:#e2e8f0;"></text>
				<input v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="c-placeholder" />
			</view>
			<view class="c-form-input rg-form-row">
				<input v-model="code" type="number" maxlength="4" placeholder="请输入验证码" placeholder-class="c-placeholder" />
				<text class="rg-sms-btn" :class="{ disabled: btnDisabled }" @click="sendMsg">{{ btnText }}</text>
			</view>
			<view class="c-form-input rg-form-row">
				<text class="cuIcon-lock rg-input-ic"></text>
				<input class="rg-pwd-input" v-model="password" :type="showPwd ? 'text' : 'password'" maxlength="20" placeholder="设置密码（6-20 位）" placeholder-class="c-placeholder" />
				<text class="rg-pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '隐藏' : '显示' }}</text>
			</view>
			<view class="c-form-input rg-form-row">
				<text class="cuIcon-lock rg-input-ic"></text>
				<input class="rg-pwd-input" v-model="password2" :type="showPwd2 ? 'text' : 'password'" maxlength="20" placeholder="确认密码" placeholder-class="c-placeholder" />
				<text class="rg-pwd-toggle" @click="showPwd2 = !showPwd2">{{ showPwd2 ? '隐藏' : '显示' }}</text>
			</view>

			<view class="c-btn-primary rg-submit" @click="doRegister">注册并登录</view>
			<text class="rg-login-link">已有账号？<text style="color:#059669;font-weight:600;" @click="goLogin">去登录</text></text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import {
		sendSmsCode,
		userRegister,
		ownerLogin
	} from '@/api/user/userApi.js'
	import {
		saveUserLoginInfo
	} from '@/lib/java110/utils/StorageUtil.js'

	export default {
		data() {
			return {
				phone: '',
				code: '',
				password: '',
				password2: '',
				showPwd: false,
				showPwd2: false,
				btnDisabled: false,
				btnText: '获取验证码',
				toastMsg: ''
			}
		},
		methods: {
			sendMsg() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				sendSmsCode(this.phone, {
					second: 60,
					btnValue: '获取验证码',
					btnDisabled: false
				}).catch(() => {})
				this.btnDisabled = true
				let left = 60
				this.btnText = left + 's 后重发'
				this._smsTimer = setInterval(() => {
					left--
					if (left <= 0) {
						clearInterval(this._smsTimer)
						this.btnDisabled = false
						this.btnText = '获取验证码'
					} else {
						this.btnText = left + 's 后重发'
					}
				}, 1000)
			},
			doRegister() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				if (!this.code) {
					this._toast('请输入验证码')
					return
				}
				if (this.password.length < 6 || this.password.length > 20) {
					this._toast('密码长度需 6-20 位')
					return
				}
				if (this.password !== this.password2) {
					this._toast('两次输入的密码不一致')
					return
				}
				userRegister({
					link: this.phone,
					msgCode: this.code,
					password: this.password
				}).then(() => {
					// 注册成功后自动登录
					ownerLogin({
						link: this.phone,
						passwd: this.password,
						loginByPhone: false
					}).then(_user => {
						saveUserLoginInfo(_user.userId, _user.token, _user.key)
						uni.setStorageSync('charge_demo_user', {
							name: this._maskPhone(this.phone),
							mode: '手机号快捷登录'
						})
						this._toast('注册成功 · 已自动登录')
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/index/index'
							})
						}, 800)
					}).catch(() => {
						this._toast('注册成功 · 请登录')
						setTimeout(() => {
							uni.navigateBack({
								delta: 1
							})
						}, 800)
					})
				}).catch(() => {})
			},
			_maskPhone(p) {
				return p.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
			},
			goLogin() {
				uni.navigateBack({
					delta: 1
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
	.rg-page {
		min-height: 100vh;
		background: #fff;
	}

	.rg-body {
		padding: 64rpx 48rpx 0;
	}

	.rg-logo {
		width: 120rpx;
		height: 120rpx;
		border-radius: 32rpx;
		background: linear-gradient(135deg, #34d399, #10b981);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .3);
	}

	.rg-logo-img {
		width: 60rpx;
		height: 60rpx;
	}

	.rg-title {
		display: block;
		text-align: center;
		font-size: 32rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 28rpx;
	}

	.rg-sub {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 10rpx;
	}

	.rg-form-row {
		margin-top: 24rpx;
	}

	.rg-input-ic {
		font-size: 30rpx;
		color: #94a3b8;
		flex-shrink: 0;
	}

	.rg-sms-btn {
		font-size: 22rpx;
		font-weight: 600;
		color: #059669;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.rg-sms-btn.disabled {
		color: #94a3b8;
	}

	.rg-pwd-toggle {
		font-size: 22rpx;
		color: #94a3b8;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.rg-pwd-input {
		flex: 1 !important;
		min-width: 0 !important;
		width: 100%;
		border: none !important;
		outline: none !important;
		-webkit-appearance: none;
		appearance: none;
	}

	.rg-submit {
		margin-top: 48rpx;
	}

	.rg-login-link {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 40rpx;
	}
</style>
