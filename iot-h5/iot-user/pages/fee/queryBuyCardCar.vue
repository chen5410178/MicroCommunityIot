<template>
	<view>
		<view class="plate-context">
			<view class="ak_row_black">
			</view>
			<view class="ak_row_title text-center">
				<text>停车月卡购买</text>
			</view>

			<view class="tips_block">
			</view>

			<view class='tips'>
				<text>点击方框输入车牌号</text>
			</view>
			<view class="plate-input-body">
				<view class="plate-input-content" @click="showCarNumberKeyboard">
					<view bindtap="showCarNumberKeyboard" data-id="0"
						:class="inputOnFocusIndex=='0'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index0}}</text>
					</view>
					<view bindtap="inputClick" data-id="1"
						:class="inputOnFocusIndex=='1'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index1}}</text>
					</view>
					<view bindtap="inputClick" data-id="2"
						:class="inputOnFocusIndex=='2'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index2}}</text>
					</view>
					<view bindtap="inputClick" data-id="3"
						:class="inputOnFocusIndex=='3'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index3}}</text>
					</view>
					<view bindtap="inputClick" data-id="4"
						:class="inputOnFocusIndex=='4'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index4}}</text>
					</view>
					<view bindtap="inputClick" data-id="5"
						:class="inputOnFocusIndex=='5'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index5}}</text>
					</view>
					<view bindtap="inputClick" data-id="6"
						:class="inputOnFocusIndex=='6'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index6}}</text>
					</view>
					<view bindtap="inputClick" data-id="7" class="green-border"
						:class="inputOnFocusIndex=='7'?'plate-nums-foc':'plate-nums-first'">
						<text class="plate-num-text">{{inputPlates.index7}}</text>
					</view>
				</view>
			</view>


			<view class='plate-input-flag' bindtap='changeplate'>
				<text>{{carNumBtn}}</text>
			</view>

			<view class="plat-btn-black"></view>
			<view class="cu-bar btn-group" style="margin-top: 30px;">
				<button @click="_queryCarNum" :disabled="carNum.length< 7"
					class="cu-btn bg-green shadow-blur round lg">立即查询</button>
			</view>

			<view class="temp-remark">
				<view>停车月卡提示</view>
				<view>1、请确保车辆已经申请过车位，如果还没有申请，请先到前台申请车位；</view>
				<view>2、输入正确车牌，前后不要有空格；</view>
			</view>
		</view>

		<select-car-num ref="popupCarNumber" type="bottom" @confirm="confirmGuaCarNumber" @getCarNum="getCarNum"
			:gua="false"></select-car-num>

	</view>
</template>

<script>
	import selectCarNum from '../../components/select-carnum/select-carnum.vue';

	import {
		isNotNull
	} from '../../lib/java110/utils/StringUtil.js'
	import {
		queryMonthCardByCarNum
	} from '../../api/car/carApi.js';

	export default {
		data() {
			return {
				flag: true,
				inputPlates: {
					index0: "",
					index1: "",
					index2: "",
					index3: "",
					index4: "",
					index5: "",
					index6: "",
					index7: ""
				},
				carNum: '',
				communityId: '',
				communityName: '',
				carNumBtn: '',
				carNums: [],
				inputOnFocusIndex: '',
				createUserId: ''
			}
		},
		components: {
			selectCarNum
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.createUserId = options.createUserId;
		},
		methods: {
			showCarNumberKeyboard() {
				this.$refs.popupCarNumber.open()
			},

			// 车牌号选择键盘
			confirmCarNumber(value) {
				this.car_number = value
				this.$refs.popupCarNumber.close()
			},
			getCarNum: function(_list) {
				this.inputPlates.index0 = _list[0].title;
				this.inputPlates.index1 = _list[1].title;
				let _title = _list[2].title;
				if (_title.length > 0) {
					this.inputPlates.index2 = _title[0];
				} else {
					this.inputPlates.index2 = '';
				}
				if (_title.length > 1) {
					this.inputPlates.index3 = _title[1];
				} else {
					this.inputPlates.index3 = '';
				}
				if (_title.length > 2) {
					this.inputPlates.index4 = _title[2];
				} else {
					this.inputPlates.index4 = '';
				}
				if (_title.length > 3) {
					this.inputPlates.index5 = _title[3];
				} else {
					this.inputPlates.index5 = '';
				}
				if (_title.length > 4) {
					this.inputPlates.index6 = _title[4];
				} else {
					this.inputPlates.index6 = '';
				}
				if (_title.length > 5) {
					this.inputPlates.index7 = _title[5];
				} else {
					this.inputPlates.index7 = '';
				}
				this.carNum = this.inputPlates.index0 + this.inputPlates.index1 + this.inputPlates.index2 + this
					.inputPlates.index3;
				this.carNum += (this.inputPlates.index4 + this.inputPlates.index5 + this.inputPlates.index6 + this
					.inputPlates.index7);
			},
			_queryCarNum: function() {
				let _that = this;
				queryMonthCardByCarNum({
					carNum: this.carNum,
					communityId: this.communityId
				}).then(_data => {

					if (!_data.cards || _data.cards.length < 1) {
						uni.showToast({
							icon: 'none',
							title: '未查到月卡'
						});
						return;
					}

					uni.navigateTo({
						url: '/pages/fee/buyCarMonthCard?carNum=' + _that.carNum + "&communityId=" +
							_that.communityId +"&createUserId="+_that.createUserId
					})
				}, err => {
					uni.showToast({
						icon: 'none',
						title: '未查到车辆'
					})
				})
			},
			_loadTempFee: function(_carNum) {
				this.carNum = _carNum;
				if (_carNum.length > 0) {
					this.inputPlates.index0 = _carNum[0];
				}
				if (_carNum.length > 1) {
					this.inputPlates.index1 = _carNum[1];
				}
				if (_carNum.length > 2) {
					this.inputPlates.index2 = _carNum[2];
				}
				if (_carNum.length > 3) {
					this.inputPlates.index3 = _carNum[3];
				}
				if (_carNum.length > 4) {
					this.inputPlates.index4 = _carNum[4];
				}
				if (_carNum.length > 5) {
					this.inputPlates.index5 = _carNum[5];
				}
				if (_carNum.length > 6) {
					this.inputPlates.index6 = _carNum[6];
				}
				if (_carNum.length > 7) {
					this.inputPlates.index7 = _carNum[7];
				}
				//查询
				this._queryCarNum();
			}
		}
	}
</script>

<style>
	@import "./tempParkingFee.css";

.ak_row_black{
	height: 80upx;
}
.ak_row_title{
	font-size: 42upx;
}
	.input-ui {
		border: 1px solid #ccc;
		padding: 12upx 18upx;
		border-radius: 8upx;
		color: rgb(48, 49, 51);
		width: 100%;
		height: 100%;
	}
</style>