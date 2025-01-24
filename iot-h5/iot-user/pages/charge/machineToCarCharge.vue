<template>
	<view class="">

		<view class="flex justify-between mtc-machine">
			<view>{{machineName}}({{machineCode}})</view>
			<view>充电桩总数:{{portCount}}</view>
		</view>
		<view class="flex justify-start mtc-machine-port">
			<view v-for="(item,index) in  ports" :key="index" class="port-item">
				<view class="item-1" :class="{'item-1-active':curPort.portId == item.portId || item.state=='WORKING'}"
					@click="_switchPort(item)">
					<view class="port-name">
						{{item.portName}}
					</view>
					<view class="port-state">({{item.stateName}})</view>
				</view>
			</view>
		</view>

		<view class="plat-btn-black"></view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="login">
			<button @click="_toCharge" :disabled="!curPort.portId"
				class="cu-btn bg-green shadow-blur round lg">去充电</button>
		</view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-else>
			<button @click="_toLogin" class="cu-btn bg-green shadow-blur round lg">请先登录</button>
		</view>

	</view>
</template>

<script>
	import {
		getChargeMachines,
		getChargeMachinePort
	} from '@/api/machine/machineApi.js';
	import charge from '@/components/charge/charge.vue';
	import {
		autoLogin,
		hasLogin
	} from '@/api/user/sessionApi.js';
	export default {
		data() {
			return {
				machineId: '',
				communityId: '',
				machineCode: '',
				machineName: '',
				portCount: '',
				ports: [],
				curPort: {},
				login: false
			}
		},
		components: {
			charge,
		},
		onLoad(options) {
			autoLogin(options);
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
					url: '/pages/charge/carChargePort?machineId=' + this.machineId +
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
				// #ifdef H5
					uni.setStorageSync('targetUrl',location.hash.substr(1));
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
	.mtc-machine-port {
		background-color: #fff;
		padding-left: 20upx;
		padding-right: 20upx;
		padding-bottom: 20upx;
		margin: 2upx 20upx 0upx 20upx;
		flex-wrap: wrap;
		.port-item {
			width: 49%;
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
		}
	}

</style>