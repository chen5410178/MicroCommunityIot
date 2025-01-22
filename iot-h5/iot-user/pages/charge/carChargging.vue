<template>
	<view class="page-body">
		<view class="charge-title text-center">充电状态</view>
		<view class="charging-status">
			<view class="progress-circle">
				<view class="circle" :style="backgroupProcess">
					<view class="circle-inner">
						<text class="car-icon">{{process}}%</text>
					</view>
				</view>
			</view>
			<view class="flex justify-center margin-top">
				<view class="remaining-time ">预计剩余：{{min}}分钟</view>
			</view>
			<view class="details-one">
				<view class="detail-item-one">
					<view>{{charged}}</view>
					<view class="span">已充电量 (度)</view>
				</view>
				<view class="detail-item-one">
					<view>{{chargeMoney}}</view>
					<view class="span">预计充电费用 (元)</view>
				</view>
			</view>
			<view class="details">
				<view class="detail-item">{{energy}}<br><span>功率 (kW)</span></view>
				<view class="detail-item">{{electric}}<br><span>电流 (A)</span></view>
				<view class="detail-item">{{voltage}}<br><span>电压 (V)</span></view>
				<view class="detail-item">{{temperature}}<br><span>枪温 (C)</span></view>
			</view>
			<button class="stop-charging" @click="_stopCharge">结束充电</button>
		</view>
	</view>
</template>

<script>
	import {stopCarCharge,getChargeMachineOrder,queryCarChargeProcess} from '@/api/machine/machineApi.js';
	export default {
		data() {
			return {
				orderId:'',
				communityId:'',
				process: 0,
				min: 0,
				charged: 0,
				chargeMoney: 0,
				energy: 0,
				electric: 0,
				voltage: 0,
				temperature: 0,
				machineId:'',
				portId:'',
				portName:'',
				machineName:'',
				backgroupProcess: {

				},
				interval:{}
			}
		},
		onLoad(options) {
			this.orderId = options.orderId;
			this.communityId = options.communityId;
			this.updateProgress();
			this._loadChargeMachineOrder();
			this._queryCarChargeState();
			this._heartbeatState();
			
		},
		methods: {
			updateProgress: function() {
				this.backgroupProcess = {
					background: 'conic-gradient(#9acd32 calc(var(--progress, ' + this.process +
						') * 1%),#fff calc(var(--progress, 0) * 1%))'
				}
			},
			_loadChargeMachineOrder:function(){
				let _that = this;
				getChargeMachineOrder({
					communityId:this.communityId,
					page:1,
					row:1,
					orderId:this.orderId
				}).then(_data =>{
					_that.machineId = _data[0].machineId;
					_that.portId= _data[0].portId;
					_that.portName= _data[0].portName;
					_that.machineName= _data[0].machineName;
					// todo 充电完成或充电失败 
					if(_data[0].state == '2002' || _data[0].state == '3003'){
						uni.navigateTo({
							url: '/pages/charge/chargeCarOrder?orderId=' + _that.orderId +
								'&communityId=' + _that.communityId
						});
					}
				})
			},
			_queryCarChargeState:function(){
				let _that = this;
				queryCarChargeProcess({
					orderId:this.orderId,
					communityId:this.communityId
				}).then(_json=>{
					if(_json.code != 0){
						return ;
					}
					let _data = _json.data;
					_that.process= _data.process;
					_that.min= _data.min;
					_that.charged= _data.charged;
					_that.chargeMoney= _data.chargeMoney;
					_that.energy= _data.energy;
					_that.electric= _data.electric;
					_that.voltage= _data.voltage;
					_that.temperature= _data.temperature;
					_that.updateProgress();
				})
			},
			_heartbeatState:function(){
				let _that = this;
				setTimeout(function(){
					_that._queryCarChargeState();
					if(_that.process < 100){
						_that._heartbeatState();
					}else{
						uni.navigateTo({
							url: '/pages/charge/chargeCarOrder?orderId=' + _that.orderId +
								'&communityId=' + _that.communityId
						});
					}
				},10000);
			},
			_stopCharge: function() {
				let _that = this;
				stopCarCharge({
					orderId: this.orderId,
					machineId:this.machineId,
					portId:this.portId,
					communityId:this.communityId
				}).then(_data => {
					if(_data.code != 0){
						uni.showToast({
							icon:'none',
							title:_data.msg
						});
						return ;
					}
					uni.navigateTo({
						url: '/pages/charge/chargeCarOrder?orderId=' + _that.orderId +
							'&communityId=' + _that.communityId
					});
				})
			}
		}
	}
</script>

<style lang="scss">
	.page-body {
		background-color: #000;
		color: #9acd32;
		font-family: Arial, sans-serif;
		text-align: center;
		margin: 0;
		padding: 0;
		height: 100vh;
	}

	.charge-title {
		padding-top: 40upx;
		font-size: 32upx;
	}

	.charging-status {
		padding: 80upx 40upx;
	}

	.progress-circle {
		position: relative;
		width: 480upx;
		height: 480upx;
		margin: 0 auto;
	}

	.circle {
		width: 100%;
		height: 100%;
		border-radius: 50%;

		display: flex;
		justify-content: center;
		align-items: center;
	}

	.circle-inner {
		width: 400upx;
		height: 400upx;
		border-radius: 50%;
		background: #000;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.car-icon {
		font-size: 78upx;

		height: auto;
	}

	.remaining-time {
		padding: 10upx;
		font-size: 28upx;
		color: #9acd32;
		background-color: #212E0E;
		width: 60%;
		border-radius: 20upx;
	}

	.details-one {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20upx;
		margin-top: 120upx;

		.detail-item-one {
			background: #212E0E;
			border-radius: 20upx;
			font-size: 42upx;
			height: 200upx;
			padding-top: 40upx;

			.span {
				font-size: 28upx;
			}
		}
	}

	.details {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 10px;
		margin-top: 60upx;

		.detail-item {
			background: #212E0E;
			border-radius: 20upx;
			padding: 20upx;
			font-size: 28upx;

			span {
				font-size: 24upx;
			}
		}
	}

	.stop-charging {
		background: #212E0E;
		color: #9acd32;
		margin-top: 40upx;
		border: none;
		padding: 10px 20px;
		border-radius: 5px;
		font-size: 16px;
		cursor: pointer;
	}
</style>