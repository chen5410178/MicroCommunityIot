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
				<text class="lg-sub">请输入手机号验证码登录</text>

				<view class="c-form-input lg-form-row">
					<text style="font-size:24rpx;font-weight:600;color:#94a3b8;">+86</text>
					<text style="width:1rpx;height:32rpx;background:#e2e8f0;"></text>
					<input v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="c-placeholder" />
				</view>
				<view class="c-form-input lg-form-row">
					<input v-model="code" type="number" maxlength="6" placeholder="请输入验证码" placeholder-class="c-placeholder" />
					<text class="lg-sms-btn" :class="{ disabled: btnDisabled }" @click="sendMsg">{{ btnText }}</text>
				</view>

				<view class="lg-submit-btn" :class="{ ready: agreeChk }" @click="smsLogin">登录</view>

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

				<label class="lg-agree" @click="agreeChk2 = !agreeChk2">
					<view class="lg-check" :class="{ on: agreeChk2 }"><text v-if="agreeChk2">✓</text></view>
					<text style="font-size:20rpx;color:#64748b;line-height:1.5;">已阅读并同意
						<text style="color:#059669;" @click.stop="openAgree('用户服务协议')">《用户服务协议》</text>与
						<text style="color:#059669;" @click.stop="openAgree('隐私保护指引')">《隐私保护指引》</text>
					</text>
				</label>
				<text class="lg-register-link">还没有账号？<text style="color:#059669;font-weight:600;" @click="goRegister">立即注册</text></text>
			</view>

			<!-- #ifdef MP-WEIXIN -->
			<wechat-login ref="wechatLoginRef" class="lg-quick-login"></wechat-login>
			<!-- #endif -->
		</view>

		<!-- 协议弹层 -->
		<view class="c-mask" v-if="agreeShow" @click="agreeShow = false"></view>
		<view class="c-sheet" v-if="agreeShow" style="max-height:72%;">
			<view class="lg-agree-head">
				<text class="c-title" style="font-size:28rpx;">{{ agreeTitle }}</text>
				<text class="lg-agree-close" @click="agreeShow = false">关闭</text>
			</view>
			<scroll-view scroll-y class="lg-agree-body">
				<rich-text class="lg-agree-rich" :nodes="agreeNodes"></rich-text>
			</scroll-view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import wechatLogin from '@/components/user/wechat-login.vue'
	import {
		agreeTexts
	} from '@/api/mock/invoiceData.js'
	import {
		getSystemPrivacy
	} from '@/api/system/systemApi.js'
	import {
		getCommunityId,
		saveCurrentCommunity,
		queryCommunitys
	} from '@/api/community/communityApi.js'
	import {
		sendSmsCode,
		ownerLogin,
		appUserSmsLogin
	} from '@/api/user/userApi.js'
	import {
		saveUserLoginInfo,
		removeUserLoginInfo
	} from '@/lib/java110/utils/StorageUtil.js'
	import {
		takeTargetUrl,
		navigateToTargetUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js'
	import {
		toRichNodes
	} from '@/lib/java110/utils/HtmlUtil.js'

	export default {
		components: {
			wechatLogin
		},
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
				agreeHtml: '',
				userProtocol: '',
				merchantProtocol: '',
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			agreeNodes() {
				return toRichNodes(this.agreeHtml || '暂无内容', {
					fontSize: 30,
					lineHeight: 1.9,
					color: this.themeDark ? '#cbd5e1' : '#475569'
				})
			}
		},
		onLoad() {
			// 清除缓存前先取小区ID，用于拉取用户服务协议/隐私保护指引
			const communityId = getCommunityId()
			uni.removeStorageSync("JAVA110_USER_INFO")
			uni.removeStorageSync("currentCommunityInfo")
			uni.removeStorageSync("ownerInfo")
			removeUserLoginInfo()
			this._loadPrivacy(communityId)
			// #ifdef MP-WEIXIN
			setTimeout(() => {
				this.$refs.wechatLoginRef && this.$refs.wechatLoginRef._initWechatLogin()
			}, 1000)
			// #endif
		},
		methods: {
			_loadPrivacy(communityId) {
				getSystemPrivacy(communityId).then(_res => {
					if (_res && _res.data) {
						this.userProtocol = _res.data.userProtocol || ''
						this.merchantProtocol = _res.data.merchantProtocol || ''
					}
				}).catch(() => {})
			},
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
			smsLogin() {
				if (!/^1\d{10}$/.test(this.phone)) {
					this._toast('请输入正确的手机号')
					return
				}
				if (!this.code) {
					this._toast('请输入验证码')
					return
				}
				if (!this.agreeChk) {
					this._toast('请先阅读并同意《用户服务协议》')
					return
				}
				appUserSmsLogin({
					link: this.phone,
					msgCode: this.code
				}).then(_user => {
					saveUserLoginInfo(_user.userId, _user.token, _user.key)
					this._loginSuccess(_user)
				}).catch(err => {
					this._toast(err || '登录失败')
				})
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
					this._loginSuccess(_user)
				}).catch(err => {
					this._toast(err || '登录失败')
				})
			},
			_loginSuccess(_user) {
				this._saveLoginCommunity(_user)
				this._toast('登录成功 · 欢迎回来')
				setTimeout(() => {
					navigateToTargetUrl(takeTargetUrl(), '/pages/index/index')
				}, 800)
			},
			// 登录成功后写入当前小区，避免首页/业务接口 communityId 缺失
			_saveLoginCommunity(_user) {
				const communityId = _user && _user.communityId
				if (!communityId || communityId === '-1') {
					return
				}
				queryCommunitys({
					page: 1,
					row: 50
				}).then(res => {
					const list = (res && res.code == 0 && res.data) || []
					const hit = list.find(c => c.communityId === communityId)
					saveCurrentCommunity(hit || {
						communityId: communityId
					})
				}).catch(() => {
					saveCurrentCommunity({
						communityId: communityId
					})
				})
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
				let text = ''
				if (type === '用户服务协议') {
					text = this.userProtocol
				} else if (type === '隐私保护指引') {
					text = this.merchantProtocol
				}
				this.agreeHtml = text || agreeTexts[type] || ''
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

	.lg-quick-login {
		margin-top: 24rpx;
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
		max-height: 52vh;
	}

	.lg-agree-rich {
		font-size: 30rpx;
		line-height: 1.9;
		color: #475569;
		word-break: break-all;
	}
</style>
