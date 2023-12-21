<template>
	<view class="">
		<view class="margin border-radius">
			<view class="cu-list menu">
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">房屋</text>
					</view>
					<view class="action">
						{{roomName || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">表类型</text>
					</view>
					<view class="action">
						{{typeName || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">表名称</text>
					</view>
					<view class="action">
						{{machineName || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">表号</text>
					</view>
					<view class="action">
						{{address || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">充值金额</text>
					</view>
					<view class="action">
						{{chargeMoney || 0.00}}元
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">充值度数</text>
					</view>
					<view class="action">
						{{chargeDegrees || 0.00}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">充值时间</text>
					</view>
					<view class="action">
						{{createTime || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">充值状态</text>
					</view>
					<view class="action">
						{{stateName || '-'}}
					</view>
				</view>
				<view class="cu-item ">
					<view class="content">
						<text class="text-grey">说明</text>
					</view>
					<view class="action">
						{{remark || '-'}}
					</view>
				</view>
			</view>
		</view>

		<view class="plat-btn-black"></view>
		<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="state == '1001'">
			<button @click="_stopCharge" class="cu-btn bg-orange shadow-blur round lg">结束充电</button>
		</view>
	</view>
</template>

<script>
	import {getMeterRechargeRecord} from '../../api/meter/meterApi.js'
	export default {
		data() {
			return {
				chargeId: '',
				machineId:'',
				machineName:'',
				chargeMoney: '',
				chargeDegrees: '',
				state: '',
				stateName: '',
				address:'',
				createTime: '',
				roomName: '',
				typeName: '',
				remark: '',
			}
		},
		onLoad(options) {
			this.chargeId = options.chargeId;
			this.communityId = options.communityId;
			this._loadMeterRechargeRecord();
		},
		methods: {
			_loadMeterRechargeRecord:function(){
				let _that = this;
				getMeterRechargeRecord({
					communityId:this.communityId,
					page:1,
					row:1,
					chargeId:this.chargeId
				}).then(_data =>{
					_that.chargeId = _data.data[0].chargeId;
					_that.machineId = _data.data[0].machineId;
					_that.machineName = _data.data[0].machineName;
					_that.chargeMoney= _data.data[0].chargeMoney;
					_that.chargeDegrees= _data.data[0].chargeDegrees;
					_that.state= _data.data[0].state;
					if(_data.data[0].state == 'C') {
						_that.stateName='充值成功';
					}
					if(_data.data[0].state == 'W') {
						_that.stateName='充值中';
					}
					_that.address= _data.data[0].address;
					_that.createTime= _data.data[0].createTime;
					_that.roomName= _data.data[0].roomName;
					_that.typeName= _data.data[0].typeName;
					_that.remark= _data.data[0].remark;
				})
			},
		}
	}
</script>

<style lang="scss">
	.border-radius{
		border-radius: 20upx;
	}
</style>
