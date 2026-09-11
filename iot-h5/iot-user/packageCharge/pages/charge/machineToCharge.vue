<template>
	<view class="mtc-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 桩信息卡 -->
		<view class="c-card mtc-mx mtc-mt">
			<view class="mtc-pile-head">
				<view class="mtc-pile-ic">
					<image :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="mtc-pile-ic-img" mode="aspectFit"></image>
				</view>
				<view class="mtc-pile-info">
					<view class="mtc-pile-name-row">
						<text class="c-title mtc-pile-name">{{ machineName }}</text>
						<view class="mtc-online" :class="online === false ? 'off' : 'on'">
							<view class="mtc-dot"></view>
							<text>{{ online === false ? '离线' : '在线' }}</text>
						</view>
					</view>
					<text class="c-text-sub mtc-pile-code">{{ machineCode }}</text>
				</view>
				<view class="mtc-pile-side">
					<text class="c-text-sub">插槽数</text>
					<text class="mtc-port-count">{{ portCount }}</text>
				</view>
			</view>
		</view>

		<!-- 选择插座 -->
		<view class="c-card mtc-mx mtc-mt">
			<view class="mtc-sec-head">
				<text class="c-title">选择插座</text>
				<text class="c-text-sub">{{ slotTip }}</text>
			</view>
			<view class="mtc-slot-grid">
				<view v-for="(item, index) in ports" :key="index" class="c-slot-btn"
					:class="{ busy: item.state == 'WORKING', active: curPort.portId == item.portId }" @click="_switchPort(item)">
					<text class="c-slot-num">{{ item.portCode }}</text>
				</view>
			</view>
		</view>

		<!-- 充电时长 -->
		<view class="c-card mtc-mx mtc-mt">
			<view class="mtc-sec-head">
				<text class="c-title">充电时长</text>
				<text class="c-text-sub">充满自停更省心</text>
			</view>
			<view class="mtc-hours-grid">
				<view v-for="(item, index) in hours" :key="index" class="c-chip"
					:class="{ active: curHours.duration == item.duration }" @click="_switchHours(item)">
					{{ item.name }}
				</view>
			</view>
		</view>

		<!-- 优惠券入口 -->
		<view class="c-card mtc-mx mtc-mt" v-if="curHours.duration && curPort.portId" @click="_selectCoupons">
			<view class="mtc-row">
				<view class="mtc-row-left">
					<image :src="imgUrl + '/h5/images/charge/ic-card.png'" class="mtc-row-ic" mode="aspectFit"></image>
					<text class="c-text-bold mtc-row-name">使用充电劵抵扣</text>
				</view>
				<view class="mtc-row-right">
					<text class="c-text-sub">{{ couponCount == 0 ? '请选择' : couponCount + ' 张' }}</text>
					<text class="mtc-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 余额 / 月卡 -->
		<view class="c-card mtc-mx mtc-mt" v-show="login">
			<account ref="accRef"></account>
		</view>
		<view class="c-card mtc-mx mtc-mt" v-show="login">
			<charge ref="chargeRef"></charge>
		</view>

		<!-- 充电订单入口 -->
		<view class="c-card mtc-mx mtc-mt" v-show="login" @click="_toChargeOrder">
			<view class="mtc-row">
				<view class="mtc-row-left">
					<image :src="imgUrl + '/h5/images/charge/ic-chart.png'" class="mtc-row-ic" mode="aspectFit"></image>
					<text class="c-text-bold mtc-row-name">充电订单</text>
				</view>
				<view class="mtc-row-right">
					<text class="c-text-sub">去查看</text>
					<text class="mtc-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 去充电 -->
		<view class="mtc-mx mtc-mt mtc-foot" v-if="login">
			<view class="c-btn-primary" :class="{ 'mtc-btn-disabled': !curHours.duration || !curPort.portId }"
				@click="_toCharge">去充电</view>
		</view>
		<!-- #ifdef H5 -->
		<view class="mtc-mx mtc-mt mtc-foot" v-else>
			<view class="c-btn-primary" @click="_toLogin">请先登录</view>
		</view>
		<!-- #endif -->
		<!-- #ifdef MP-WEIXIN -->
		<view class="mtc-mx mtc-mt mtc-foot" v-else>
			<wechat-login-charge ref="wechatLoginChargeRel" @getWechatLogin="getWechatLogin"></wechat-login-charge>
		</view>
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		isNotNull
	} from '@/lib/java110/utils/StringUtil.js'
	import {
		getChargeMachines,
		getChargeMachinePort
	} from '@/api/machine/machineApi.js';
	import account from '@/packageCharge/components/account/account.vue';
	import charge from '@/packageCharge/components/charge/charge.vue';
	import {
		autoLogin,
		hasLogin,
		autoLoginMini
	} from '@/api/user/sessionApi.js';
	import wechatLoginCharge from '@/packageCharge/components/user/wechat-login-charge.vue';
	import chargeStore from '@/api/charge/chargeStore.js';
	import {
		saveTargetUrl,
		buildCurrentPageUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js';
	export default {
		data() {
			return {
				machineId: '',
				communityId: '',
				portId: '',
				machineCode: '',
				machineName: '',
				portCount: '',
				durationPrice: '',
				online: true,
				ports: [],
				curPort: {},
				curHours: {},
				couponList: [],
				couponCount: 0,
				login: false,
				hours: [{
					name: '充满自停',
					duration: 999,
				}, {
					name: '2小时',
					duration: 2,
				}, {
					name: '4小时',
					duration: 4,
				}, {
					name: '6小时',
					duration: 6,
				}, {
					name: '8小时',
					duration: 8,
				}, {
					name: '10小时',
					duration: 10,
				}]
			}
		},
		components: {
			charge,
			account,
			wechatLoginCharge
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			slotTip() {
				const free = this.ports.filter(p => p.state == 'FREE').length
				return free + ' 个空闲 · 点击选择'
			}
		},
		async onLoad(options) {
			// #ifdef H5
			autoLogin(options);
			// #endif
			// #ifdef MP-WEIXIN
			await autoLoginMini(options).catch(error => {
				console.log('自动登录失败:', error);
			});
			// #endif
			this.machineId = options.machineId;
			this.communityId = options.communityId;
			this.portId = options.portId || '';
			this._loadChargeMachines();
			this._loadChargeMachinePorts();
			let _that = this;

			setTimeout(function() {
				_that.login = hasLogin();
				if (_that.$refs.accRef) {
					_that.$refs.accRef.loadOwnerAccount(_that.communityId);
				}
				if (_that.$refs.chargeRef) {
					_that.$refs.chargeRef.loadChargeMonthOrder(_that.communityId);
				}
				// #ifdef MP-WEIXIN
				if (_that.$refs.wechatLoginChargeRel) {
					_that.$refs.wechatLoginChargeRel._initWechatLogin(_that.communityId);
				}
				// #endif 

			}, 1500);
		},
		onShow: function(options) {
			this._dealChargeCoupons();

			if (this.$refs.accRef) {
				this.$refs.accRef.loadOwnerAccount(this.communityId);
			}
			if (this.$refs.chargeRef) {
				this.$refs.chargeRef.loadChargeMonthOrder(this.communityId);
			}

		},
		methods: {
			_loadChargeMachines: function() {
				let _that = this;
				getChargeMachines({
					page: 1,
					row: 1,
					machineId: this.machineId,
					communityId: this.communityId
				}).then(_data => {
					//_that.machines = _data.data;
					_that.machineCode = _data.data[0].machineCode;
					_that.machineName = _data.data[0].machineName;
					_that.portCount = _data.data[0].portCount;
					_that.durationPrice = _data.data[0].durationPrice;
					_that.online = _data.data[0].state !== 'OFFLINE';
				})
			},
			_loadChargeMachinePorts: function() {
				let _that = this;
				getChargeMachinePort({
					page: 1,
					row: 100,
					machineId: this.machineId,
					communityId: this.communityId
				}).then(_data => {
					_that.ports = _data.data;
					// 扫码进入时按 portId 自动选中端口（仅空闲端口）
					if (isNotNull(_that.portId)) {
						let target = null;
						for (let i = 0; i < _that.ports.length; i++) {
							if (_that.ports[i].portId == _that.portId) {
								target = _that.ports[i];
								break;
							}
						}
						if (target && target.state === 'FREE') {
							_that.curPort = target;
						} else {
							uni.showToast({
								title: '该插座不可用，请手动选择',
								icon: 'none',
								duration: 2000
							});
						}
					}
				})
			},
			_toCharge: function() {
				saveTargetUrl(buildCurrentPageUrl());
				uni.navigateTo({
					url: '/packageCharge/pages/charge/chargeConfirm?machineId=' + this.machineId +
						"&communityId=" + this.communityId +
						"&portId=" + this.curPort.portId +
						"&duration=" + this.curHours.duration +
						"&couponIds=" + this.couponList.join(",")
				});
			},

			_switchPort: function(_port) {
				if (_port.state != 'FREE') {
					return;
				}
				this.curPort = _port;
			},
			_switchHours: function(_hours) {
				if (this.curPort.state != 'FREE') {
					wx.showToast({
						title: '请先选择空闲插座',
						icon: "none",
						duration: 2000
					});
					return;
				}
				this.curHours = _hours;
			},
			_selectCoupons: function(_item) {
				let _that = this;
				uni.navigateTo({
					url: '/packageCoupon/pages/coupon/chargeCoupon?communityId=' + this.communityId
				})
			},
			_dealChargeCoupons: function() {
				let chargeCoupons = uni.getStorageSync('COUPON_USER_CHARGE');
				if (!chargeCoupons) {
					return;
				}
				uni.removeStorageSync('COUPON_USER_CHARGE');
				this.couponList = chargeCoupons;
				this.couponCount = chargeCoupons.length;
			},
			_toLogin: function() {
				if (this.communityId) {
					uni.setStorageSync("QRCODE_COMMUNITY_ID", this.communityId);
				}
				saveTargetUrl(buildCurrentPageUrl());
				uni.navigateTo({
					url: '/pages/user/login'
				})
			},
			_toChargeOrder: function() {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/chargeMachineOrders?communityId=' + this.communityId
				})
			},
			getWechatLogin: function() {
				let _that = this;
				_that.login = hasLogin();
				setTimeout(function() {
					if (_that.$refs.accRef) {
						_that.$refs.accRef.loadOwnerAccount(_that.communityId);
					}
					if (_that.$refs.chargeRef) {
						_that.$refs.chargeRef.loadChargeMonthOrder(_that.communityId);
					}
				}, 1500)
			}
		}
	}
</script>

<style lang="scss">
	.mtc-page {
		padding-bottom: 60rpx;
	}

	.mtc-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.mtc-mt {
		margin-top: 24rpx;
	}

	.mtc-pile-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 32rpx 28rpx;
	}

	.mtc-pile-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.mtc-pile-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.mtc-pile-info {
		flex: 1;
		min-width: 0;
	}

	.mtc-pile-name-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.mtc-pile-name {
		font-size: 28rpx;
	}

	.mtc-pile-code {
		margin-top: 6rpx;
		display: block;
		font-size: 22rpx;
	}

	.mtc-online {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 18rpx;
		font-weight: 600;
	}

	.mtc-online.on {
		color: #059669;
	}

	.mtc-online.off {
		color: #64748b;
	}

	.mtc-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #10b981;
	}

	.mtc-online.off .mtc-dot {
		background: #cbd5e1;
	}

	.mtc-pile-side {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 6rpx;
	}

	.mtc-port-count {
		font-size: 32rpx;
		font-weight: 700;
		color: #10b981;
		line-height: 1;
	}

	.mtc-sec-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 28rpx 20rpx;
	}

	.mtc-slot-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 16rpx;
		padding: 0 28rpx 28rpx;
	}

	.mtc-slot-grid .c-slot-btn {
		border-radius: 24rpx;
		padding: 32rpx 0;
		gap: 8rpx;
	}

	.mtc-slot-grid .c-slot-num {
		font-size: 32rpx;
	}

	.mtc-slot-grid .c-slot-btn:not(.busy) .c-slot-num {
		color: #10b981;
	}

	.mtc-slot-grid .c-slot-btn.active .c-slot-num {
		color: #fff;
	}

	.mtc-slot-grid .c-slot-btn.busy {
		border-color: #fecaca;
		background: #fef2f2;
	}

	.mtc-slot-grid .c-slot-btn.busy .c-slot-num {
		color: #f87171;
	}

	.mtc-hours-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 16rpx;
		padding: 0 28rpx 28rpx;
	}

	.mtc-hours-grid .c-chip {
		width: 100%;
		padding: 20rpx 0;
	}

	.mtc-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 26rpx 28rpx;
	}

	.mtc-row-left {
		display: flex;
		align-items: center;
		gap: 16rpx;
	}

	.mtc-row-ic {
		width: 36rpx;
		height: 36rpx;
		flex-shrink: 0;
	}

	.mtc-row-name {
		font-size: 24rpx;
		font-weight: 600;
	}

	.mtc-row-right {
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.mtc-arrow {
		font-size: 28rpx;
		color: #cbd5e1;
		line-height: 1;
	}

	.mtc-foot {
		padding-bottom: 40rpx;
	}

	.mtc-foot .c-btn-primary {
		border-radius: 32rpx;
		height: 104rpx;
		line-height: 104rpx;
		font-size: 30rpx;
		box-shadow: 0 12rpx 32rpx rgba(16, 185, 129, .3);
	}

	.mtc-btn-disabled {
		opacity: .4;
		pointer-events: none;
	}

	/* ===== 黑夜主题覆盖（对齐原型 phone.theme-dark） ===== */
	.charge-theme-dark .mtc-pile-ic {
		background: rgba(16, 185, 129, .18) !important;
	}

	.charge-theme-dark .mtc-slot-grid .c-slot-btn.busy {
		background: #182338 !important;
		border-color: #7f1d1d !important;
	}

	.charge-theme-dark .mtc-slot-grid .c-slot-btn.busy .c-slot-num {
		color: #f87171 !important;
	}
</style>
