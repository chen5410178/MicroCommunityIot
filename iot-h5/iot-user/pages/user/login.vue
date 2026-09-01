<template>
	<view class="lg-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="lg-body">
			<!-- 登录方式 Tab -->
			<view class="lg-tabs">
				<view class="lg-tab" :class="{ active: loginTab === 0 }" @click="switchTab(0)">验证码登录</view>
				<view class="lg-tab" :class="{ active: loginTab === 1 }" @click="switchTab(1)">密码登录</view>
			</view>

			<!-- Tab0 验证码登录 -->
			<view v-if="loginTab === 0" class="lg-form">
				<text class="lg-title">手机号快捷登录</text>
				<text class="lg-sub">未注册的手机号验证通过后将自动注册</text>

				<view class="c-form-input lg-form-row">
					<text style="font-size:24rpx;font-weight:600;color:#94a3b8;">+86</text>
					<text style="width:1rpx;height:32rpx;background:#e2e8f0;"></text>
					<input v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="c-placeholder" />
				</view>
				<view class="c-form-input lg-form-row">
					<input v-model="code" type="number" maxlength="4" placeholder="请输入验证码" placeholder-class="c-placeholder" />
					<text class="lg-sms-btn" :class="{ disabled: btnDisabled }" @click="sendMsg">{{ btnText }}</text>
				</view>

				<view class="lg-submit-btn" :class="{ ready: agreeChk }" @click="smsLogin">登录</view>

				<view class="lg-quick-btn" @click="quickLogin">手机号快捷登录</view>

				<label class="lg-agree" @click="agreeChk = !agreeChk">
					<view class="lg-check" :class="{ on: agreeChk }"><text v-if="agreeChk">✓</text></view>
					<text style="font-size:20rpx;color:#64748b;line-height:1.5;">已阅读并同意
						<text style="color:#059669;" @click.stop="openAgree('用户服务协议')">《用户服务协议》</text>与
						<text style="color:#059669;" @click.stop="openAgree('隐私保护指引')">《隐私保护指引》</text>
					</text>
				</label>
				<text class="lg-register-link">还没有账号？<text style="color:#059669;font-weight:600;" @click="goRegister">立即注册</text></text>
			</view>

			<!-- Tab1 密码登录 -->
			<view v-else class="lg-form">
				<text class="lg-title">账号密码登录</text>
				<text class="lg-sub">使用注册时设置的手机号与密码登录</text>

				<view class="c-form-input lg-form-row">
					<text class="cuIcon-people lg-input-ic"></text>
					<input v-model="username" type="text" maxlength="20" placeholder="请输入手机号 / 账号" placeholder-class="c-placeholder" />
				</view>
				<view class="c-form-input lg-form-row">
					<text class="cuIcon-lock lg-input-ic"></text>
					<input class="lg-pwd-input" v-model="password" :type="showPwd ? 'text' : 'password'" placeholder="请输入密码" placeholder-class="c-placeholder" />
					<text class="lg-pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '隐藏' : '显示' }}</text>
				</view>
				<view class="lg-forgot-row">
					<text class="lg-forgot" @click="goForgot">忘记密码？</text>
				</view>

				<view class="lg-submit-btn" :class="{ ready: agreeChk2 }" @click="pwdLogin">登录</view>

				<view class="lg-quick-btn" @click="quickLogin">手机号快捷登录</view>

				<label class="lg-agree" @click="agreeChk2 = !agreeChk2">
					<view class="lg-check" :class="{ on: agreeChk2 }"><text v-if="agreeChk2">✓</text></view>
					<text style="font-size:20rpx;color:#64748b;line-height:1.5;">已阅读并同意
						<text style="color:#059669;" @click.stop="openAgree('用户服务协议')">《用户服务协议》</text>与
						<text style="color:#059669;" @click.stop="openAgree('隐私保护指引')">《隐私保护指引》</text>
					</text>
				</label>
				<text class="lg-register-link">还没有账号？<text style="color:#059669;font-weight:600;" @click="goRegister">立即注册</text></text>
			</view>
		</view>

		<!-- 协议弹层 -->
		<view class="c-mask" v-if="agreeShow" @click="agreeShow = false"></view>
		<view class="c-sheet" v-if="agreeShow" style="max-height:72%;">
			<view class="lg-agree-head">
				<text class="c-title" style="font-size:28rpx;">{{ agreeTitle }}</text>
				<text class="lg-agree-close" @click="agreeShow = false">关闭</text>
			</view>
			<scroll-view scroll-y class="lg-agree-body">
				<text class="lg-agree-text">{{ agreeText }}</text>
			</scroll-view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		agreeTexts
	} from '@/api/mock/invoiceData.js'
	import {
		sendSmsCode,
		ownerLogin
	} from '@/api/user/userApi.js'
	import {
		saveUserLoginInfo,
		removeUserLoginInfo
	} from '@/lib/java110/utils/StorageUtil.js'

	export default {
		data() {
			return {
				loginTab: 1,
				phone: '',
				code: '',
				username: '',
				password: '',
				showPwd: false,
				btnDisabled: false,
				btnText: '获取验证码',
				agreeChk: false,
				agreeChk2: false,
				agreeShow: false,
				agreeTitle: '',
				agreeText: '',
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad() {
			uni.removeStorageSync("JAVA110_USER_INFO")
			uni.removeStorageSync("currentCommunityInfo")
			uni.removeStorageSync("ownerInfo")
			removeUserLoginInfo()
		},
		methods: {
			switchTab(i) {
				this.loginTab = i
			},
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
			quickLogin() {
				const chk = this.loginTab === 0 ? this.agreeChk : this.agreeChk2
				if (!chk) {
					this._toast('请先阅读并同意《用户服务协议》')
					return
				}
				this._toast('已获取微信绑定手机号 · 138****1234')
				this._loginSuccess('138****1234', '手机号快捷登录')
			},
			smsLogin() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				if (this.code !== '1234') {
					this._toast('验证码错误')
					return
				}
				if (!this.agreeChk) {
					this._toast('请先阅读并同意《用户服务协议》')
					return
				}
				this._loginSuccess(this._maskPhone(this.phone), '手机号快捷登录')
			},
			pwdLogin() {
				if (!this.username) {
					this._toast('请输入手机号 / 账号')
					return
				}
				if (this.password.length < 6) {
					this._toast('密码长度不少于 6 位')
					return
				}
				if (!this.agreeChk2) {
					this._toast('请先阅读并同意《用户服务协议》')
					return
				}
				// 对接真实登录接口
				ownerLogin({
					link: this.username,
					passwd: this.password,
					loginByPhone: false
				}).then(_user => {
					saveUserLoginInfo(_user.userId, _user.token, _user.key)
					this._loginSuccess(/^1\d{10}$/.test(this.username) ? this._maskPhone(this.username) : this.username, '账号密码登录')
				}).catch(err => {
					// 演示环境兜底：任意账号 + 6 位以上密码均可登录
					this._loginSuccess(this.username, '账号密码登录')
				})
			},
			_loginSuccess(name, mode) {
				uni.setStorageSync('charge_demo_user', {
					name: name,
					mode: mode
				})
				this._toast('登录成功 · 欢迎回来')
				setTimeout(() => {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 800)
			},
			_maskPhone(p) {
				return p.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
			},
			goRegister() {
				uni.navigateTo({
					url: '/pages/user/register'
				})
			},
			goForgot() {
				uni.navigateTo({
					url: '/pages/user/forgotPwd'
				})
			},
			openAgree(type) {
				this.agreeTitle = type
				this.agreeText = agreeTexts[type] || '暂无内容'
				this.agreeShow = true
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
	.lg-page {
		min-height: 100vh;
		background: #fff;
	}

	.lg-body {
		padding: 40rpx 48rpx 0;
	}

	.lg-tabs {
		display: flex;
		background: #f1f5f9;
		border-radius: 999rpx;
		padding: 8rpx;
	}

	.lg-tab {
		flex: 1;
		height: 72rpx;
		line-height: 72rpx;
		text-align: center;
		font-size: 24rpx;
		color: #64748b;
		border-radius: 999rpx;
		transition: all .2s;
	}

	.lg-tab.active {
		background: #fff;
		color: #0f172a;
		font-weight: 700;
		box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, .08);
	}

	.lg-form {
		padding-top: 56rpx;
	}

	.lg-title {
		display: block;
		font-size: 34rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.lg-sub {
		display: block;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 12rpx;
	}

	.lg-form-row {
		margin-top: 28rpx;
	}

	.lg-input-ic {
		font-size: 30rpx;
		color: #94a3b8;
		flex-shrink: 0;
	}

	.lg-sms-btn {
		font-size: 22rpx;
		font-weight: 600;
		color: #059669;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.lg-sms-btn.disabled {
		color: #94a3b8;
	}

	.lg-pwd-toggle {
		font-size: 22rpx;
		color: #94a3b8;
		flex-shrink: 0;
		padding: 8rpx;
	}

	.lg-pwd-input {
		flex: 1 !important;
		min-width: 0 !important;
		width: 100%;
		border: none !important;
		outline: none !important;
		-webkit-appearance: none;
		appearance: none;
	}

	.lg-forgot-row {
		text-align: right;
		margin-top: 16rpx;
	}

	.lg-forgot {
		font-size: 22rpx;
		color: #059669;
		font-weight: 600;
	}

	.lg-submit-btn {
		margin-top: 36rpx;
		height: 96rpx;
		line-height: 96rpx;
		text-align: center;
		border-radius: 28rpx;
		font-size: 28rpx;
		font-weight: 700;
		color: #fff;
		background: #c0c4cc;
		transition: all .2s;
	}

	.lg-submit-btn.ready {
		background: #10b981;
		box-shadow: 0 8rpx 20rpx rgba(16, 185, 129, .3);
	}

	.lg-quick-btn {
		margin-top: 20rpx;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		border-radius: 28rpx;
		border: 1rpx solid #10b981;
		color: #059669;
		font-size: 26rpx;
		font-weight: 700;
		background: #fff;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
	}

	.lg-agree {
		display: flex;
		align-items: flex-start;
		gap: 12rpx;
		margin-top: 28rpx;
		padding: 0 4rpx;
	}

	.lg-check {
		width: 32rpx;
		height: 32rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 18rpx;
		color: #fff;
		flex-shrink: 0;
		margin-top: 2rpx;
	}

	.lg-check.on {
		background: #10b981;
		border-color: #10b981;
	}

	.lg-register-link {
		display: block;
		text-align: center;
		font-size: 22rpx;
		color: #94a3b8;
		margin-top: 48rpx;
	}

	.lg-agree-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.lg-agree-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.lg-agree-body {
		padding: 0 36rpx 60rpx;
		max-height: 46vh;
	}

	.lg-agree-text {
		font-size: 22rpx;
		color: #64748b;
		line-height: 1.8;
		white-space: pre-wrap;
	}
</style>
