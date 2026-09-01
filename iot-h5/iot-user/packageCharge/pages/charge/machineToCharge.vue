<template>
	<view class="">

		<view class="flex justify-between mtc-machine">
			<view>{{machineName}}({{machineCode}})</view>
			<view>插槽数:{{portCount}}</view>
		</view>
		<view class="flex justify-start mtc-machine-port">
			<view v-for="(item,index) in  ports" :key="index" class="port-item">
				<view class="item-1"
					:class="{'item-1-active':curPort.portId == item.portId,'item-1-working':item.state=='WORKING'}"
					@click="_switchPort(item)">
					<view class="port-name">
						{{item.portName}}
					</view>
					<view class="port-state">({{item.stateName}})</view>
				</view>
			</view>
		</view>
		<view class=" mtc-hours-title">
			<view>充电时长</view>
		</view>
		<view class="flex justify-start mtc-machine-hours">
			<view v-for="(item,index) in  hours" :key="index" class="hours-item">
				<view class="item-1" :class="{'item-1-active':curHours.duration == item.duration}"
					@click="_switchHours(item)">
					<view class="port-name">
						{{item.name}}
					</view>
				</view>
			</view>
		</view>

		<view class="cu-list menu margin-sm" @click="_selectCoupons" v-if="curHours.duration && curPort.portId">
			<view class="cu-item arrow">
				<view class="content padding-tb-sm">
					<view>
						<view class="text-cut" style="width:220px">使用充电劵抵扣</view>
					</view>
				</view>
				<view v-if="couponCount == 0">请选择</view>
				<view v-else>{{couponCount+ '张' }}</view>
			</view>
		</view>
		<view class="margin-sm" v-show="login">
			<account ref="accRef"></account>
		</view>
		<view class="margin-sm" v-show="login">
			<charge ref="chargeRef"></charge>
		</view>
		<view class="margin-sm" v-show="login">
			<view class="cu-list menu">
				<view class="cu-item arrow" @click="_toChargeOrder()">
					<view class="content padding-tb-sm">
						<view>
							<text class="cuIcon-formfill text-green margin-right-xs"></text> 充电订单
						</view>
					</view>
					<view class="action">
						去查看
					</view>
				</view>
			</view>
		</view>
		<view class="plat-btn-black"></view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="login">
			<button @click="_toCharge" :disabled="!curHours.duration || !curPort.portId"
				class="cu-btn bg-green shadow-blur round lg">去充电</button>
		</view>
		<!-- #ifdef H5 -->
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-else>
			<button @click="_toLogin" class="cu-btn bg-green shadow-blur round lg">请先登录</button>
		</view>
		<!-- #endif -->
		<!-- #ifdef MP-WEIXIN -->
		<view class="padding flex flex-direction" v-else>
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
	export default {
		data() {
			return {
				machineId: '',
				communityId: '',
				machineCode: '',
				machineName: '',
				portCount: '',
				durationPrice: '',
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
			if (isNotNull(options.portId)) {
				this.curPort.portId = options.portId;
			}
			// 进入本页时记录当前路径及参数，供后续成功页返回使用（字段名为 targetUrl）
			try {
				let url = '/packageCharge/pages/charge/machineToCharge';
				const queryArr = [];
				if (this.machineId) {
					queryArr.push('machineId=' + this.machineId);
				}
				if (this.communityId) {
					queryArr.push('communityId=' + this.communityId);
				}
				if (this.curPort && this.curPort.portId) {
					queryArr.push('portId=' + this.curPort.portId);
				} else if (options.portId) {
					queryArr.push('portId=' + options.portId);
				}
				if (queryArr.length > 0) {
					url = url + '?' + queryArr.join('&');
				}
				uni.setStorageSync('targetUrl', url);
			} catch (e) {
				console.error('记录 targetUrl 失败：', e);
			}
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
				// #ifdef H5
				uni.setStorageSync('targetUrl', location.hash.substr(1));
				// #endif
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
	.mtc-machine {
		background-color: #fff;
		font-size: 28upx;
		color: #000;
		padding-left: 20upx;
		padding-right: 20upx;
		margin: 20upx 20upx 0upx 20upx;
		border-top-left-radius: 20upx;
		border-top-right-radius: 20upx;
		height: 60upx;
		line-height: 60upx;
		border-bottom: 2upx;
	}

	.mtc-machine-port {
		background-color: #fff;
		padding-left: 20upx;
		padding-right: 20upx;
		padding-bottom: 20upx;
		margin: 2upx 20upx 0upx 20upx;

		flex-wrap: wrap;

		.port-item {
			width: 33.33%;
			text-align: center;

			.item-1 {
				background-color: #B1E8DA;
				width: 90%;
				margin: 0upx auto;
				margin-top: 20upx;
				border-radius: 10upx;
				color: #fff;

				.port-name {
					font-size: 28upx;
				}

				.port-state {
					font-size: 24upx;
				}
			}

			.item-1-active {
				background-color: #0FBA82;
			}

			.item-1-working {
				background-color: #e54d42;
			}
		}
	}

	.mtc-hours-title {
		background-color: #fff;
		font-size: 28upx;
		color: #000;
		padding-left: 20upx;
		padding-right: 20upx;
		margin: 20upx 20upx 0upx 20upx;
		border-top-left-radius: 20upx;
		border-top-right-radius: 20upx;
		height: 60upx;
		line-height: 60upx;
		border-bottom: 2upx;
	}

	.mtc-machine-hours {
		background-color: #fff;
		padding-left: 20upx;
		padding-right: 20upx;
		padding-bottom: 20upx;
		margin: 2upx 20upx 0upx 20upx;

		flex-wrap: wrap;

		.hours-item {
			width: 33.33%;
			text-align: center;

			.item-1 {
				border: 1px solid #000;
				width: 90%;
				margin: 0upx auto;
				margin-top: 20upx;
				border-radius: 10upx;
				color: #000;

				.port-name {
					font-size: 28upx;
				}
			}

			.item-1-active {
				border: 1px solid #0FBA82;
				background-color: #0FBA82;
				color: #fff;
			}
		}
	}
</style>