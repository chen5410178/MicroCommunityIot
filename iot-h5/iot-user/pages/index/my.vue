<template>
	<view class="my-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 用户卡片 -->
		<view class="my-mx my-mt" @click="!isLogin && goLogin()">
			<view class="c-card my-user-card" v-if="isLogin">
				<view class="my-avatar" :style="{ background: avatarBg }">{{ userInitial }}</view>
				<view style="flex:1;">
					<text class="my-name">{{ user.name }}</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ user.mode }} · {{ user.home }}</text>
				</view>
				<text class="my-pwd-btn" @click.stop="goChangePwd">
					<image :src="imgUrl + '/h5/images/common/ic-lock-green.png'" class="my-pwd-btn-ic" mode="aspectFit"></image>
					修改密码
				</text>
			</view>
			<view class="c-card my-user-card" v-else>
				<view class="my-avatar" style="background:#f1f5f9;">
					<image :src="imgUrl + '/h5/images/common/ic-user.png'" class="my-avatar-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;">
					<text class="my-name" style="color:#0f172a;">点击登录</text>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">登录后同步充电记录 / 余额 / 月卡</text>
				</view>
				<text style="color:#cbd5e1;font-size:26rpx;">›</text>
			</view>
		</view>

		<!-- 余额 / 月卡宫格 -->
		<view class="my-grid my-mx my-mt">
			<view class="c-card my-grid-item" @click="goWallet">
				<text class="c-text-sub">余额</text>
				<text class="my-grid-val">¥{{ balance.toFixed(2) }}</text>
			</view>
			<view class="c-card my-grid-item" :class="{ 'my-grid-item-disabled': certifyState === 'D' }" @click="goRoomCertification">
				<text class="c-text-sub">业主认证</text>
				<text class="my-grid-val">{{ certifyState === 'D' ? '认证审核中' : '去认证' }}</text>
			</view>
		</view>

		<!-- 功能入口 -->
		<view class="c-card my-mx my-mt my-menu">
			<view class="my-menu-item" @click="goCarService">
				<image :src="imgUrl + '/h5/images/charge/ic-car-full.png'" class="my-menu-ic" mode="aspectFit"></image>
				<text class="my-menu-text">车辆服务</text>
				<text class="c-text-sub" style="font-size:20rpx;">{{ vehicles.length ? vehicles[0].plate : '去添加' }}</text>
				<text style="color:#cbd5e1;font-size:24rpx;">›</text>
			</view>
			<view class="my-menu-item" @click="goInvoice">
				<image :src="imgUrl + '/h5/images/charge/ic-invoice.png'" class="my-menu-ic" mode="aspectFit"></image>
				<text class="my-menu-text">电子发票</text>
				<text class="c-text-sub" style="font-size:20rpx;">开票记录</text>
				<text style="color:#cbd5e1;font-size:24rpx;">›</text>
			</view>
			<view class="my-menu-item" @click="goService">
				<image :src="imgUrl + '/h5/images/common/ic-phone.png'" class="my-menu-ic" mode="aspectFit"></image>
				<text class="my-menu-text">联系客服</text>
				<text class="my-menu-phone" v-if="serviceTel">{{ serviceTel }}</text>
				<view class="my-call-btn" v-if="serviceTel" @click.stop="callService">拨打</view>
			</view>
			<view class="my-menu-item" @click="goAbout">
				<image :src="imgUrl + '/h5/images/common/ic-info.png'" class="my-menu-ic" mode="aspectFit"></image>
				<text class="my-menu-text">关于充电服务</text>
				<text class="c-text-sub" style="font-size:20rpx;" v-if="appVersion">{{ appVersion }}</text>
				<text style="color:#cbd5e1;font-size:24rpx;">›</text>
			</view>
		</view>

		<!-- 退出登录 -->
		<view class="my-mx my-mt" v-if="isLogin">
			<view class="my-logout-btn" @click="logoutShow = true">退出登录</view>
		</view>

		<!-- 退出确认 -->
		<view class="c-mask" v-if="logoutShow" @click="logoutShow = false"></view>
		<view class="c-dialog" v-if="logoutShow" style="text-align:center;">
			<text class="c-title" style="font-size:30rpx;display:block;">退出登录</text>
			<text class="c-text-sub" style="margin-top:16rpx;display:block;line-height:1.6;">退出后需重新登录<br />充电记录 · 余额与月卡数据将保留</text>
			<view class="my-logout-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="logoutShow = false">取消</view>
				<view class="my-logout-confirm" style="flex:1;" @click="confirmLogout">退出登录</view>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		userLogout
	} from '@/api/user/userApi.js'
	import {
		removeUserLoginInfo
	} from '@/lib/java110/utils/StorageUtil.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'
	import {
		getSystemPrivacy
	} from '@/api/system/systemApi.js'
	import {
		getAppUser
	} from '@/api/user/appUser.js'
	import {
		saveTargetUrl,
		buildCurrentPageUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js'

	export default {
		data() {
			return {
				user: {
					name: '李明',
					mode: '账号密码登录',
					home: '阳光花园 3 栋业主'
				},
				logoutShow: false,
				isLogin: false,
				toastMsg: '',
				appVersion: '',
				certifyState: 'N' // 业主认证状态 N 未认证 / D 认证审核中 / Y 已认证
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			userInitial() {
				return (this.user.name || '李').charAt(0)
			},
			avatarBg() {
				return 'linear-gradient(135deg,#34d399,#059669)'
			},
			balance() {
				return chargeStore.balance
			},
			vehicles() {
				return chargeStore.vehicles
			},
			serviceTel() {
				return chargeStore.serviceTel
			}
		},
		onShow() {
			// 每次进入 Tab 页重新判定登录态（登录/退出/过期后切换回来均生效）
			this.isLogin = hasLogin()
			// 用户名三级数据源：userInfo（真实用户）→ charge_demo_user（演示数据）→ 默认值
			const userInfo = uni.getStorageSync('userInfo')
			const demo = uni.getStorageSync('charge_demo_user')
			if (userInfo && userInfo.name) {
				this.user.name = userInfo.name
				this.user.mode = '账号密码登录'
			} else if (demo) {
				this.user.name = demo.name
				this.user.mode = demo.mode
			}
			// 关于页版本号取系统配置缓存（App.vue 启动时拉取）
			const sysConfig = uni.getStorageSync('java110SystemConfig')
			this.appVersion = (sysConfig && sysConfig.version) || ''
			this._loadService()
			this._loadCertifyState()
		},
		methods: {
			_loadCertifyState() {
				if (!this.isLogin) {
					this.certifyState = 'N'
					return
				}
				getAppUser().then(() => {
					this.certifyState = 'Y'
				}).catch(err => {
					this.certifyState = (err && err.code == -101) ? 'D' : 'N'
				})
			},
			_loadService() {
				getSystemPrivacy(getCommunityId()).then(_res => {
					if (_res && _res.data) {
						chargeStore.setService(_res.data)
					}
				}).catch(() => {})
			},
			callService() {
				if (!chargeStore.serviceTel) return
				uni.makePhoneCall({
					phoneNumber: chargeStore.serviceTel
				})
			},
			goLogin() {
				saveTargetUrl(buildCurrentPageUrl())
				uni.navigateTo({ url: '/pages/user/login' })
			},
			goWallet() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/wallet' })
			},
			goRoomCertification() {
				if (!this.isLogin) {
					this._toast('登录后使用此功能')
					return
				}
				if (this.certifyState === 'D') return
				uni.navigateTo({ url: '/packageProperty/pages/roomCertification/roomCertification' })
			},
			goCarService() {
				if (!this.isLogin) {
					this._toast('登录后使用此功能')
					return
				}
				uni.navigateTo({ url: '/packageCharge/pages/charge/carService' })
			},
			goInvoice() {
				if (!this.isLogin) {
					this._toast('登录后使用此功能')
					return
				}
				uni.navigateTo({ url: '/packageCharge/pages/charge/invoice' })
			},
			goService() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/customerService' })
			},
			goAbout() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/about' })
			},
			confirmLogout() {
				this.logoutShow = false
				userLogout({}).catch(() => {})
				removeUserLoginInfo()
				uni.removeStorageSync('charge_demo_user')
				this.isLogin = false
				this._toast('已退出登录')
				setTimeout(() => {
					uni.navigateTo({ url: '/pages/user/login' })
				}, 500)
			},
			goChangePwd() {
				uni.navigateTo({ url: '/pages/user/changePwd' })
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
	.my-page {
		padding-bottom: 60rpx;
	}

	.my-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.my-mt {
		margin-top: 24rpx;
	}

	.my-user-card {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
	}

	.my-avatar {
		width: 104rpx;
		height: 104rpx;
		border-radius: 50%;
		color: #fff;
		font-size: 40rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.my-avatar-img {
		width: 48rpx;
		height: 48rpx;
	}

	.my-name {
		font-size: 28rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.my-pwd-btn {
		font-size: 26rpx;
		color: #059669;
		font-weight: 600;
		display: flex;
		align-items: center;
		gap: 6rpx;
	}

	.my-pwd-btn-ic {
		width: 26rpx;
		height: 26rpx;
	}

	.my-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.my-grid-item {
		padding: 26rpx;
	}

	.my-grid-item-disabled {
		opacity: .6;
	}

	.my-grid-val {
		display: block;
		font-size: 32rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 8rpx;
	}

	.my-menu {
		overflow: hidden;
	}

	.my-menu-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.my-menu-ic {
		width: 34rpx;
		height: 34rpx;
		flex-shrink: 0;
	}

	.my-menu-item:last-child {
		border-bottom: none;
	}

	.my-menu-text {
		flex: 1;
		font-size: 24rpx;
		font-weight: 500;
		color: #334155;
	}

	.my-menu-phone {
		font-size: 22rpx;
		font-weight: 700;
		color: #059669;
		letter-spacing: 2rpx;
	}

	.my-call-btn {
		background: #10b981;
		color: #fff;
		font-size: 20rpx;
		font-weight: 700;
		border-radius: 999rpx;
		padding: 8rpx 24rpx;
	}

	.my-logout-btn {
		background: #fff;
		border-radius: 28rpx;
		height: 96rpx;
		line-height: 96rpx;
		text-align: center;
		font-size: 26rpx;
		font-weight: 600;
		color: #ef4444;
		box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, .05);
	}

	.my-logout-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 32rpx;
	}

	.my-logout-confirm {
		background: #ef4444;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		border-radius: 24rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
	}
</style>
