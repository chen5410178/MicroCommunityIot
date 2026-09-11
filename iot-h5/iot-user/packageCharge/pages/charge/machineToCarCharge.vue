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
					<text class="c-text-sub">充电桩总数</text>
					<text class="mtc-port-count">{{ portCount }}</text>
				</view>
			</view>
		</view>

		<!-- 选择充电枪 -->
		<view class="c-card mtc-mx mtc-mt">
			<view class="mtc-sec-head">
				<text class="c-title">选择充电枪</text>
				<text class="c-text-sub">{{ gunTip }}</text>
			</view>
			<view class="mtc-gun-grid">
				<view v-for="(item, index) in ports" :key="index" class="c-gun-btn"
					:class="{ active: curPort.portId == item.portId, disabled: item.state != 'FREE' }" @click="_switchPort(item)">
					<text style="font-size:28rpx;font-weight:700;">{{ item.portName || item.portCode }}</text>
					<text style="font-size:20rpx;" :style="{ color: item.state != 'FREE' ? '#f87171' : '#10b981' }">{{ item.state != 'FREE' ? '使用中' : '空闲' }}</text>
				</view>
			</view>
		</view>

		<!-- 去充电 -->
		<view class="mtc-mx mtc-mt mtc-foot" v-if="login">
			<view class="c-btn-primary" :class="{ 'mtc-btn-disabled': !curPort.portId }" @click="_toCharge">去充电</view>
		</view>
		<view class="mtc-mx mtc-mt mtc-foot" v-else>
			<view class="c-btn-primary" @click="_toLogin">请先登录</view>
		</view>
	</view>
</template>

<script>
	import {
		getChargeMachines,
		getChargeMachinePort
	} from '@/api/machine/machineApi.js';
	import charge from '@/packageCharge/components/charge/charge.vue';
	import {
		autoLogin,
		hasLogin,
		autoLoginMini
	} from '@/api/user/sessionApi.js';
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
				machineCode: '',
				machineName: '',
				portCount: '',
				online: true,
				ports: [],
				curPort: {},
				login: false
			}
		},
		components: {
			charge,
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			gunTip() {
				const free = this.ports.filter(p => p.state == 'FREE').length
				return free + ' 枪空闲 · 点击选择'
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
			this.login = hasLogin();
			this._loadChargeMachines();
			this._loadChargeMachinePorts();
		},
		onShow: function(options) {

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
				})
			},
			_toCharge: function() {
				saveTargetUrl(buildCurrentPageUrl());
				uni.navigateTo({
					url: '/packageCharge/pages/charge/carChargePort?machineId=' + this.machineId +
						"&communityId=" + this.communityId +
						"&portId=" + this.curPort.portId
				});
			},
			_switchPort: function(_port) {
				if (_port.state != 'FREE') {
					return;
				}
				this.curPort = _port;
			},
			_toLogin: function() {
				if (this.communityId) {
					uni.setStorageSync("QRCODE_COMMUNITY_ID", this.communityId);
				}
				saveTargetUrl(buildCurrentPageUrl());
				uni.navigateTo({
					url: '/pages/user/login'
				})
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

	.mtc-gun-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
		padding: 0 28rpx 28rpx;
	}

	.mtc-gun-grid .c-gun-btn {
		border-radius: 28rpx;
		padding: 28rpx 0;
		gap: 8rpx;
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
</style>
