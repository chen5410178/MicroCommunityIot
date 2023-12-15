<template>
	<view>

		<view class="block__title">车辆信息</view>
		<view class="cu-list menu ">
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">车辆</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{carNum }}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">起租时间</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{startTime }}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">结租时间</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{endTime }}</text>
				</view>
			</view>
		</view>
		<view class="block__title">选择月卡</view>
		<view>
			<radio-group class="block" @change="radioChange($event)">
				<view class="cu-form-group" v-for="(item,valueIndex) in cards" :key="valueIndex">
					<view class="title">{{item.cardName}}({{item.cardMonth}}个月)-{{item.cardPrice}}</view>
					<radio :class="cardId ==item.cardId?'checked':''" :checked="cardId==item.cardId?true:false"
						:value="item.cardId">
					</radio>
				</view>
			</radio-group>
		</view>
		<view class="cu-list menu fee-last margin-top" v-if="receivableAmount>0">
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">应缴费用</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{receivableAmount }}</text>
				</view>
			</view>
		</view>

		<view class="" style="margin-top: 80upx;">
			<view class="cu-bar btn-group" v-if="cardId">
				<button @click="onPayFee" class="cu-btn bg-green shadow-blur round lg">确认缴费</button>
			</view>
			<view class="cu-bar btn-group" style="margin-top: 20upx;">
				<button @click="onReQuery()" class="cu-btn bg-grey shadow-blur round lg">重新查询</button>
			</view>
		</view>
	</view>

</template>

<script>
	import {
		queryMonthCardByCarNum
	} from '../../api/car/carApi.js';

	export default {
		data() {
			return {
				paId: '',
				carNum: '',
				startTime: '',
				endTime: '',
				cardId: 0.0,
				cards: [],
				receivableAmount: 0.0, // 抵扣金额
				communityId: '',
				createUserId: '',
				carId: '',
			};
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.carNum = options.carNum;
			this.communityId = options.communityId;
			this._loadMonthCards();
		},
		methods: {
			_loadMonthCards: function() {
				let _that = this;
				queryMonthCardByCarNum({
					carNum: this.carNum,
					communityId: this.communityId
				}).then(_data => {
					_that.paId = _data.paId;
					_that.cards = _data.cards;
					_that.startTime = _data.startTime;
					_that.endTime = _data.endTime;
					_that.carId = _data.carId;
				})
			},
			onPayFee: function() {
				let _receivedAmount = this.receivableAmount;
				let _tradeType = 'JSAPI';
				let _objData = {
					business: "buyCarMonthCard",
					carNum: this.carNum,
					carId: this.carId,
					cardId: this.cardId,
					feeName: '停车月卡费',
					tradeType: _tradeType,
					communityId: this.communityId,
					receivedAmount: this.receivableAmount,
					cashierUserId: this.createUserId,
				};

				uni.setStorageSync('doing_cashier', _objData);
				uni.navigateTo({
					url: '/pages/fee/cashier?money=' + _receivedAmount +
						"&business=buyCarMonthCard&communityId=" +
						this.communityId + "&cashierUserId=" + this.createUserId
				})
			},
			onReQuery: function() {
				uni.navigateBack({
					delta: 1
				})
			},
			radioChange: function(e) {
				console.log(e)
				this.cardId = e.detail.value;
				let _that = this;
				this.cards.forEach(_item => {
					if (_item.cardId == _that.cardId) {
						_that.receivableAmount = _item.cardPrice;
					}
				})
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

	.temp-remark {
		color: #888888;
		padding: 10upx;
	}
</style>