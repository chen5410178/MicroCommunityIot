<template>
	<view>
		<scroll-view scroll-y>
			<view class="block__title">停车月卡</view>
			
			<view class="cu-form-group">
				<view class="title">车牌号</view>
				<input v-model="carNum" placeholder="请输入车牌号" @blur="_queryCarCards" class="text-right"></input>
			</view>
			
			<view class="cu-form-group" v-if = "cards.length>0">
				<view class="title">停车月卡</view>
				<picker id="complaintType" bindchange="PickerChange" 
				:value="cardIndex" 
				:range-key="'cardName'" 
				:range="cards" 
				 @change="cardsChange">
					<view class="picker">
						{{cards.length==0 ? "请选择" : cards[cardIndex].cardName}}
					</view>
				</picker>
			</view>
			
			<view class="cu-form-group" v-if="cardId">
				<view class="title">应收金额</view>
				<input disabled="disable" v-model="receivableAmount" class="text-right"></input>
			</view>

				<view class="cu-bar btn-group" style="margin-top: 20upx;">
					<button @click="onPayFee" v-if="receivableAmount > 0"
						class="cu-btn bg-green shadow-blur round lg">确认缴费</button>
				</view>
				
	
			<view class="temp-remark">
				<view>停车月卡提示</view>
				<view>1、请确保车辆已经申请过车位，如果还没有申请，请先到前台申请车位；</view>
				<view>2、输入正确车牌，前后不要有空格；</view>
			</view>
		</scroll-view>
		
	</view>
	
</template>

<script>
	
	export default {
		data() {
			return {
				carNum: '',
				communityId:'',
				paId:'',
				cards: [],
				cardIndex: 0,
				receivableAmount:'',
				receivedAmount:'',
				cardId:'',
			};
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.carNum = options.carNum;
			this.communityId = options.communityId;
			
			//this._loadTempCarFee();
		},
		methods: {
			_queryCarCards:function(){
				
			},
			onPayFee:function(){
				let _receivedAmount = this.receivableAmount;
				wx.showLoading({
					title: '支付中'
				});
				let _tradeType = 'JSAPI';
				let _objData = {
					business: "tempCarFee",
					carNum: this.carNum,
					openId: this.openId,
					paId: this.paId,
					feeName: '停车费',
					tradeType: _tradeType,
					appId: this.appId,
					inoutId: this.inoutId,
					couponList: this.couponList,
					cashierUserId:'-1',
					machineId:this.machineId,
					communityId:this.communityId,
				};
				
			},
			
		}
	};
</script>
<style>
	.ppf_item {
		padding: 0rpx 0rpx 0rpx 0rpx;
	}

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

	.block__bottom {
		height: 180rpx;
	}

	.fee-last {
		margin-bottom: 100upx;
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
	.temp-remark{
		color: #888888;
		padding:10upx;
	}
</style>
