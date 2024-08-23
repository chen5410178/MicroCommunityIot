<template>
	<view class="">
		<view class="flex justify-between mtc-machine">
			<view>{{machineName}}({{machineCode}})</view>
		</view>
		<view class="margin-sm">
			<view class="cu-list menu">
				<view class="cu-item">
					<view class="content padding-tb-sm">
						<view class="cu-avatar xl round margin-left bg-green" v-if="curPort.state =='FREE'">
							<text class="avatar-text">{{curPort.stateName}}</text>
						</view>
						<view class="cu-avatar xl round margin-left bg-red" v-if="curPort.state !='FREE'">
							<text class="avatar-text">{{curPort.stateName}}</text>
						</view>
					</view>
					<view class="bg-white padding">
						<view class="flex flex-wrap">
							<view class="margin-xs padding-sm radius">桩号：{{curPort.portName}}</view>
							<view class="margin-xs padding-sm radius">编号：{{curPort.portId}}</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class=" mtc-hours-title">
			<view>充电服务价格明细</view>
		</view>
		<view class="flex justify-start mtc-machine-hours">
			<view class="grid margin-bottom text-center" v-for="(item,index) in prices" :key="index">
				<view class="flex flex-wrap">
					<view class="margin-xs padding-sm radius">{{index+1}}</view>
					<view class="margin-xs padding-sm radius">{{item.startTime}} ~ {{item.endTime}}</view>
					<view class="margin-xs padding-sm radius">{{item.price}}</view>
					<view class="margin-xs padding-sm radius">{{item.servicePrice}}</view>
				</view>
			</view>

		</view>
		<view class=" mtc-hours-title">
			<view>充电金额</view>
		</view>
		<view class="flex justify-start mtc-machine-hours">
			<view v-for="(item,index) in  moneys" :key="index" class="hours-item">
				<view class="item-1" :class="{'item-1-active':curHours.duration == item.duration}"
					@click="_switchMoney(item)">
					<view class="port-name">
						{{item.name}}
					</view>
					<view>
						元
					</view>
				</view>
			</view>
		</view>

		<!-- 		<view class="cu-list menu margin-sm" @click="_selectCoupons" v-if="curHours.duration && curPort.portId">
			<view class="cu-item arrow">
				<view class="content padding-tb-sm">
					<view>
						<view class="text-cut" style="width:220px">使用充电劵抵扣</view>
					</view>
				</view>
				<view v-if="couponCount == 0">请选择</view>
				<view v-else>{{couponCount+ '张' }}</view>
			</view>
		</view> -->
		<view class="margin-sm" v-if="login">
			<account ref="accRef"></account>
		</view>
		<!-- <view class="margin-sm" v-if="login">
			<charge ref="chargeRef"></charge>
		</view> -->
		<view class="plat-btn-black"></view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="login">
			<button @click="_startCharge" :disabled="!curHours.duration || !curPort.portId"
				class="cu-btn bg-green shadow-blur round lg">开启充电</button>
		</view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-else>
			<button @click="_toLogin" class="cu-btn bg-green shadow-blur round lg">请先登录</button>
		</view>
		<view class="cu-load load-modal" v-if="loadModal">
			<view class="gray-text">{{statusReminder}}</view>
			<view class="red-text" v-show="showReasonFlag" @tap="showReason" data-target="DialogModal1" style="color: red;">查看原因</view>
		</view>
		<view class="cu-modal" :class="modalName=='DialogModal1'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">失败原因</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					{{modalReason}}
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn bg-green margin-left" @tap="hideModal">确定</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getChargeMachines,
		getChargeMachinePort,
		startCarCharge
	} from '@/api/machine/machineApi.js';
	import account from '@/components/account/account.vue';
	import charge from '@/components/charge/charge.vue';
	import {
		autoLogin,
		hasLogin
	} from '@/api/user/sessionApi.js';
	export default {
		data() {
			return {
				statusReminder: '枪头开启中...',
				machineId: '',
				showReasonFlag:false,
				modalReason: '',  
				modalName: null,
				portId: '',
				communityId: '',
				machineCode: '',
				machineName: '',
				duration:'',
				portCount: '',
				prices: [],
				ports: [],
				loadModal: false,
				curPort: {},
				curHours: {},
				couponList: [],
				couponCount: 0,
				login: false,
				moneys: [{
					name: '20',
					duration: 20,
				}, {
					name: '30',
					duration: 30,
				}, {
					name: '50',
					duration: 50,
				}, {
					name: '80',
					duration: 80,
				}, {
					name: '100',
					duration: 100,
				}, {
					name: '150',
					duration: 150,
				}]
			}
		},
		components: {
			charge,
			account,
		},
		onLoad(options) {
			autoLogin(options);
			this.machineId = options.machineId;
			this.communityId = options.communityId;
			this.portId = options.portId;
			this.login = hasLogin();
			this._loadChargeMachines();
			this._loadChargeMachinePorts();
			let _that = this;
			setTimeout(function() {
				if (_that.$refs.accRef) {
					_that.$refs.accRef.loadOwnerAccount(_that.communityId);
				}
				if (_that.$refs.chargeRef) {
					_that.$refs.chargeRef.loadChargeMonthOrder(_that.communityId);
				}
			}, 1000);
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
			showReason(e) {
				this.modalName = e.currentTarget.dataset.target;
				this.loadModal = false;
				this.showReasonFlag = false;
				this.statusReminder = "枪头开启中...";
			},
			hideModal(e) {
				this.modalName = null
			},
			_loadChargeMachines: function() {
				let _that = this;
				getChargeMachines({
					page: 1,
					row: 1,
					machineId: this.machineId,
					communityId: this.communityId
				}).then(_data => {
					_that.machineCode = _data.data[0].machineCode;
					_that.machineName = _data.data[0].machineName;
					_that.portCount = _data.data[0].portCount;
					_that.prices = _data.data[0].prices;
				})
			},
			_loadChargeMachinePorts: function() {
				let _that = this;
				getChargeMachinePort({
					page: 1,
					row: 100,
					portId: this.portId,
					machineId: this.machineId,
					communityId: this.communityId
				}).then(_data => {
					_that.ports = _data.data;
					_that.curPort = _data.data[0];
				})
			},
			_startCharge: function() {
				const childAccounts = this.$refs.accRef.account;
				this.loadModal = true;
				startCarCharge({
					communityId:this.communityId,
					machineId:this.machineId,
					portId:this.portId,
					money:this.curHours.duration
				}).then(_data=>{
				
					if(_data.code != 0){
						this.statusReminder = "开启失败";
						this.showReasonFlag = true;
						this.modalReason =_data.msg;
						return ;
					}else{
						this.statusReminder = "开启成功";
						setTimeout(() => {
							this.loadModal = false;
							uni.navigateTo({
								url:'/pages/charge/chargeCarOrder?orderId='+_data.data +'&communityId='+this.communityId
							})
						}, 3000)
					}
				})
			},
			_switchPort: function(_port) {
				if (_port.state != 'FREE') {
					return;
				}
				this.curPort = _port;
			},
			_switchMoney: function(_hours) {
				this.curHours = _hours;
				
			},
			_selectCoupons: function(_item) {
				let _that = this;
				uni.navigateTo({
					url: '/pages/coupon/chargeCoupon'
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
					uni.setStorageSync("DEFAULT_COMMUNITY_ID", this.communityId);
				}
				// #ifdef H5
				uni.setStorageSync('targetUrl', location.hash.substr(1));
				// #endif
				uni.navigateTo({
					url: '/pages/user/login'
				})
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
				margin-top: 25upx;
				border-radius: 15upx;
				color: #000;

				.port-name {
					font-size: 40upx;
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