<template>
	<view class="">
		<view class="flex justify-between mtc-machine">
			<!--({{machineCode}})-->
			<view>{{machineName}}</view>
			<view>
				<charge-car-num-vue ref="chargeCarNumRef"></charge-car-num-vue>
			</view>
		</view>
		<view class="margin-sm">
			<view class="cu-list menu">
				<view class="cu-item">
					<view class="content padding-tb-sm">
						<view class="cu-avatar xl round margin-left bg-green" v-if="curPort.state =='FREE'">
							<text class="avatar-text">{{curPort.stateName}}</text>
						</view>
						<view class="cu-avatar xl round margin-left bg-red" v-else>
							<text class="avatar-text">{{curPort.stateName}}</text>
						</view>
					</view>
					<view class="bg-white padding">
						<view class="flex flex-wrap">
							<view class="margin-xs  radius">桩号：{{curPort.portName}}</view>
							<view class="margin-xs  radius">编号：{{curPort.portId}}</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		
		<view>
			<car-charge-price ref="carChargePriceRef" :prices="prices"></car-charge-price>
		</view>
		<view class=" mtc-hours-title">
			<view>充电金额</view>
		</view>
		<view class="flex justify-start mtc-machine-hours">
			<view v-for="(item,index) in moneys" :key="index" class="hours-item">
				<view class="item-1" :class="{'item-1-active':curMoney == item.amount}"
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
		<view class="margin-sm" v-if="login">
			<account ref="accRef"></account>
		</view>
		<view class="plat-btn-black"></view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="login">
			<button @click="_startCharge" :disabled="!curMoney || !curPort.portId"
				class="cu-btn bg-green shadow-blur round lg">开启充电</button>
		</view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-else>
			<button @click="_toLogin" class="cu-btn bg-green shadow-blur round lg">请先登录</button>
		</view>
		
		<start-car-charge ref="startCarChargeRef"></start-car-charge>
	</view>
</template>

<script>
	import {
		getChargeMachines,
		getChargeMachinePort,
		queryMyChargeOrder
	} from '@/api/machine/machineApi.js';
	import account from '@/components/account/account.vue';
	import charge from '@/components/charge/charge.vue';
	import {
		autoLogin,
		hasLogin
	} from '@/api/user/sessionApi.js';
	import startCarCharge from '@/components/charge/startCarCharge.vue';
	import carChargePrice from '@/components/charge/carChargePrice.vue';
	import chargeCarNumVue from '@/components/charge/chargeCarNum.vue';
	export default {
		data() {
			return {
				machineId: '', 
				modalName: null,
				carNum:'未设置',
				portId: '',
				communityId: '',
				machineCode: '',
				machineName: '',
				amount:'',
				portCount: '',
				prices: [],
				ports: [],
				curPort: {},
				curMoney: 50,
				login: false,
				moneys: [{
					name: '20',
					amount: 20,
				}, {
					name: '30',
					amount: 30,
				}, {
					name: '50',
					amount: 50,
				}, {
					name: '80',
					amount: 80,
				}, {
					name: '100',
					amount: 100,
				}, {
					name: '150',
					amount: 150,
				}]
			}
		},
		components: {
			charge,
			account,
			startCarCharge,
			carChargePrice,
			chargeCarNumVue
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
				if (_that.$refs.chargeCarNumRef) {
					_that.$refs.chargeCarNumRef.initUserChargeCarNum(_that.login);
				}
				_that._checkOrder();
				
			}, 1000);
		},
		onShow: function(options) {
			
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
					_that.machineCode = _data.data[0].machineCode;
					_that.machineName = _data.data[0].machineName;
					_that.portCount = _data.data[0].portCount;
					_that.prices = _data.data[0].prices;
					_that.$refs.carChargePriceRef.initPrice(_that.prices);
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
				if(!this.curMoney){
					uni.showToast({
						icon:'none',
						title:'请选择充电金额'
					});
					return;
				}
				// todo 开启充电
				this.$refs.startCarChargeRef.startCharge({
					communityId:this.communityId,
					machineId:this.machineId,
					portId:this.portId,
					money:this.curMoney
				});
			},
			_checkOrder:function(){
				if(!this.login){
					return;
				}
				queryMyChargeOrder({
					portId:this.portId,
					communityId:this.communityId,
				}).then(_json=>{
					if(_json.code != 0){
						return;
					}
					let _order = _json.data;
					uni.navigateTo({
						url: '/pages/charge/carChargging?orderId=' + _order.orderId +
							'&communityId=' + _order.communityId
					});
				})
			},
			_switchPort: function(_port) {
				if (_port.state != 'FREE') {
					return;
				}
				this.curPort = _port;
			},
			_switchMoney: function(_curMoney) {
				this.curMoney = _curMoney.amount;
				
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