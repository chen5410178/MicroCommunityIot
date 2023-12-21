<template>
	<view v-if="machineId">
		<view class="block__title">充值</view>
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">表名</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{machineName}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">剩余读数</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{prestoreDegrees}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">充值金额</text>
				</view>
				<view class="action">
					<input v-model="amount" placeholder="请输入充值金额" type="number" class="text-right"></input>
				</view>
			</view>
		</view>
		
		<view class=" bg-white  border flex justify-end" style="position: fixed;width: 100%;bottom: 0;">
		
			<view class="action text-orange margin-right line-height">
				合计：{{amount}}元
			</view>
			<view class="btn-group">
				<button class="cu-btn bg-red shadow-blur lgplus sharp" @click="onPayFee()">提交订单</button>
			</view>
		</view>
	</view>
</template>

<script>
	import context from '../../lib/java110/Java110Context.js';
	import {getMeterMachine} from '../../api/machine/machineApi.js';
	import {getCommunityId} from '@/api/community/communityApi.js';
	import {payFeeApp,payFeeWechat} from '@/api/fee/feeApi.js';
	
	import {listMeterMachine} from '../../api/meter/meterApi.js'
	export default {
		data() {
			return {
				machineId:'',
				machineName:'',
				prestoreDegrees:'0',
				communityId:'',
				createStaffId:'',
				address:'',
				amount:''
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.createStaffId = options.createStaffId;
			this.address = options.address;
			this.loadMeterMachine();
		},
		methods: {
			loadMeterMachine: function() {
				let _that = this;
				listMeterMachine({
					page:1,
					row:10,
					communityId:this.communityId,
					address:this.address,
				}).then(_data=>{
					if (!_data.data || _data.data.length != 1) {
						uni.showToast({
							icon: 'none',
							title: '水电表不存在或表号不唯一'
						});
						return;
					}
					_that.machineId=_data.data[0].machineId;
					_that.machineName=_data.data[0].machineName;
					_that.prestoreDegrees=_data.data[0].prestoreDegrees;
				})
			},
			onPayFee: function() {
				if(!this.amount){
					uni.showToast({
						icon:'none',
						title:'未填写金额'
					});
					return ;
				}
				if(this.amount <= 0){
					uni.showToast({
						icon:'none',
						title:'金额必须大于0'
					});
					return ;
				}
				let _receivedAmount = this.amount;
				let _tradeType = 'JSAPI';
				let _objData = {
					business: "meterRecharge",
					machineId: this.machineId,
					feeName: '水电表充值',
					tradeType: _tradeType,
					communityId: this.communityId,
					receivedAmount: _receivedAmount,
					createStaffId: this.createStaffId,
				}
				
				uni.setStorageSync('doing_cashier', _objData);
				uni.navigateTo({
					url: '/pages/fee/cashier?money=' + _receivedAmount +
						"&business=meterRecharge&communityId=" +
						this.communityId + "&cashierUserId=" + this.createStaffId
				})
			}
		}
	}
</script>

<style lang="scss">
	.block__title {
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 40rpx 30rpx 20rpx;
	}
	
	.button_up_blank {
		height: 40rpx;
	}
	
	.cu-btn.lgplus {
		padding: 0 20px;
		font-size: 18px;
		height: 100upx;
	
	}
	
	.cu-btn.sharp {
		border-radius: 0upx;
	}
	
	.line-height {
		line-height: 100upx;
	}
</style>