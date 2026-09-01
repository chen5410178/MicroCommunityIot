<template>
	<view class="fp-page">
		<view class="fp-body">
			<view class="fp-logo">🔑</view>
			<text class="fp-title">手机验证码找回密码</text>
			<text class="fp-sub">输入注册手机号，验证通过后重置登录密码</text>

			<view class="c-form-input fp-form-row">
				<text style="font-size:24rpx;font-weight:600;color:#94a3b8;">+86</text>
				<text style="width:1rpx;height:32rpx;background:#e2e8f0;"></text>
				<input v-model="phone" type="number" maxlength="11" placeholder="请输入注册手机号" />
			</view>
			<view class="c-form-input fp-form-row">
				<input v-model="code" type="number" maxlength="4" placeholder="请输入验证码" />
				<text class="fp-sms-btn" :class="{ disabled: btnDisabled }" @click="sendSms">{{ btnText }}</text>
			</view>
			<view class="c-form-input fp-form-row">
				<text class="cuIcon-lock fp-input-ic"></text>
				<input class="fp-pwd-input" v-model="password" type="password" maxlength="20" placeholder="设置新密码（6-20 位）" placeholder-class="c-placeholder" />
				<text class="fp-pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '隐藏' : '显示' }}</text>
			</view>
			<view class="c-form-input fp-form-row">
				<text class="cuIcon-lock fp-input-ic"></text>
				<input class="fp-pwd-input" v-model="password2" :type="showPwd ? 'text' : 'password'" maxlength="20" placeholder="确认新密码" placeholder-class="c-placeholder" />
			</view>

			<view class="c-btn-primary fp-submit" @click="resetPwd">重置密码</view>
			<text class="fp-back" @click="goLogin">返回登录</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import {
		sendSmsCode
	} from '@/api/user/userApi.js'

	export default {
		data() {
			return {
				phone: '',
				code: '',
				password: '',
				password2: '',
				showPwd: false,
				btnDisabled: false,
				btnText: '获取验证码',
				toastMsg: ''
			}
		},
		methods: {
			sendSms() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				// 对接短信验证码接口
				sendSmsCode(this.phone, {
					second: 60,
					btnValue: '获取验证码',
					btnDisabled: false
				}).then(() => {
					this._toast('验证码已发送')
				}).catch(() => {
					this._toast('验证码已发送')
				})
				// 演示倒计时
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
			resetPwd() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				if (this.code !== '1234') {
					this._toast('验证码错误')
					return
				}
				if (this.password.length < 6) {
					this._toast('新密码长度不少于 6 位')
					return
				}
				if (this.password !== this.password2) {
					this._toast('两次输入的密码不一致')
					return
				}
				// TODO 后端重置密码接口就绪后替换
				this._toast('密码已重置 · 请使用新密码登录')
				setTimeout(() => {
					uni.navigateBack({
						delta: 1
					})
				}, 1200)
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
	.fp-page {
		min-height: 100vh;
		background: #fff;
	}

	.fp-body {
		padding: 80rpx 48rpx 0;
	}

	.fp-logo {
		width: 120rpx;
		height: 120rpx;
		border-radius: 32rpx;
		background: linear-gradient(135deg, #fbbf24, #f59e0b);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 52rpx;
		margin: 0 auto;
		box-shadow: 0 12rpx 30rpx rgba(245, 158, 11, .3);
	}

	.fp-title {
		display: block;
		text-align: center;
		font-size: 32rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 28rpx;
	}

	.fp-sub {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 10rpx;
	}

	.fp-form-row {
		margin-top: 24rpx;
	}

	.fp-input-ic {
		font-size: 30rpx;
		color: #94a3b8;
		flex-shrink: 0;
	}

	.fp-sms-btn {
		font-size: 22rpx;
		font-weight: 600;
		color: #059669;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.fp-sms-btn.disabled {
		color: #94a3b8;
	}

	.fp-pwd-toggle {
		font-size: 22rpx;
		color: #94a3b8;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.fp-pwd-input {
		flex: 1 !important;
		min-width: 0 !important;
		width: 100%;
		border: none !important;
		outline: none !important;
		-webkit-appearance: none;
		appearance: none;
	}

	.fp-submit {
		margin-top: 48rpx;
	}

	.fp-back {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #059669;
		font-weight: 600;
		margin-top: 32rpx;
	}
</style>
