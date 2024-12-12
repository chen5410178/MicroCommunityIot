<template>
	<view>
		<view class="plate-context">
			<view class="ak_row_black">
			</view>
			<view class="ak_row_title">
				<text>{{communityName}}</text>
			</view>

			<view class="tips_block">
			</view>
			<view class="cu-bar btn-group " style="margin-top: 30px;">
				<button @click="_changeFlag('phone')" style="margin-right: 0px;"
					class="cu-btn  shadow-blur lg" :class="{'bg-blue':showFlag == 'phone','line-blue':showFlag != 'phone'}">手机号</button>
				<button @click="_changeFlag('car')" style="margin-left: 0px;"
					class="cu-btn shadow-blur  lg" :class="{'bg-blue':showFlag == 'car','line-blue':showFlag != 'car'}">车牌号</button>
			</view>
			<view v-if="showFlag == 'phone'">
				<view class='tips'>
					<text>请输入手机号</text>
				</view>
				<view class="plate-input-body">
					<view class="plate-input-content" >
						<input type="number" maxlength="11" class="input-ui" v-model="telNum" placeholder="手机号作为车牌号入场"/>
					</view>
				</view>
			</view>
			<view v-if="showFlag == 'car'">
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
					</view>
				</view>
			</view>
			<view class='plate-input-flag' bindtap='changeplate'>
				<text>{{carNumBtn}}</text>
			</view>

			<view class="plat-btn-black"></view>
			<view class="cu-bar btn-group" style="margin-top: 30px;">
				<button @click="_saveCarNum" 
					class="cu-btn bg-green shadow-blur round lg">立即入场</button>
			</view>
		</view>

		<view class="cu-modal" :class="showToastFlag==true?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">温馨提示</view>
					<view class="action">
					</view>
				</view>
				<view class="padding-xl">
					车牌号提交成功!
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action margin-0 flex-sub  solid-left" @tap="_closeToastModal()">关闭</view>
				</view>
			</view>
		</view>

		<select-car-num ref="popupCarNumber" type="bottom" @confirm="confirmGuaCarNumber" @getCarNum="getCarNum"
			:gua="false"></select-car-num>

	</view>
</template>

<script>
	import selectCarNum from '../../components/select-carnum/select-carnum.vue';
	import {
		customCarInOut
	} from '@/api/car/carApi.js'
	import {
		isNotNull
	} from '../../lib/java110/utils/StringUtil.js'
	import {
		refreshUserOpenId
	} from '../../api/user/userApi.js'
	import {
		isWxOrAli
	} from '../../lib/java110/utils/EnvUtil.js'
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
				telNum: '',
				communityId: '',
				communityName:'',
				appId: '',
				machineId: '',
				showToastFlag: false,
				carNumBtn:'',
				showFlag: 'phone',
			}
		},
		components: {
			selectCarNum
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.machineId = options.machineId;
			this.appId = options.appId;
			// this.openId = options.openId;
			//uni.setStorageSync(mapping.W_APP_ID,this.appId)
		},
		methods: {
			showCarNumberKeyboard() {
				this.$refs.popupCarNumber.open()
			},
			_changeFlag:function(_flag){
				
					this.showFlag = _flag;
				
				
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
			confirmGuaCarNumber: function() {

			},
			_saveCarNum: function() {
				let _that = this;
				customCarInOut({
					communityId: _that.communityId,
					carNum: _that.carNum,
					telNum: _that.telNum,
					machineId: _that.machineId,
					type: '1101',
					unlicense:'T'
				}).then(_data => {
					if (_data.data.code != 0) {
						uni.showToast({
							icon: 'none',
							title: '车牌号提交失败！'
						})
						return;
					}
					this.showToastFlag = true;
					return;
				})
			},
			_closeToastModal: function() {
				window.location.reload();
			}
		}
	}
</script>

<style>
	.pages {
		padding: 20rpx 20rpx;
	}
	
	.pages .plate-context {
		background-color: #fff;
		padding: 40rpx 20rpx 80rpx 20rpx;
	}
	
	.ak_row_title {
		padding-left: 20rpx;
		font-size: 32rpx;
		font-weight: 700;
	
	}
	
	.pages_header {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.pages_header_top {
		width: 33.3%;
		height: 60rpx;
		border-left: 5px solid #00AA00;
		border-right: 5px solid #00AA00;
	}
	
	.pages_header_btm {
		width: 70%;
		background: #00AA00;
		height: 120rpx;
		line-height: 120rpx;
		text-align: center;
		color: white;
		border-radius: 10rpx;
		font-weight: normal;
		font-size: 16pt;
	}
	
	.tips_block {
		height: 60rpx;
	}
	
	.tips {
		text-align: center;
		margin: 60rpx 0;
		font-size: 12pt;
		color: #888888;
	}
	
	.plate-input-flag {
		margin: 30rpx 10rpx 30rpx 0;
		color: #00AA00;
		float: right;
	}
	
	.plate-input-body {
		/*border: 1px solid red;*/
		height: 100rpx;
		margin: 0 10rpx;
	}
	
	.plate-input-content {
		display: flex;
		flex-direction: row;
		height: 100rpx;
	}
	
	.plate-nums-foc {
		flex: 1;
		border: 2rpx solid #00bf70;
		margin-right: 10rpx;
		align-items: center;
		justify-content: center;
		text-align: center;
		height: 100%;
		box-sizing: border-box;
		border-radius: 4rpx;
	}
	
	.plate-nums-first {
		flex: 1;
		border: 2rpx solid #ccc;
		margin-right: 10rpx;
		align-items: center;
		justify-content: center;
		text-align: center;
		height: 100%;
		box-sizing: border-box;
		border-radius: 4rpx;
	}
	
	.green-border{
		border: 2rpx solid #39b54a;
	}
	
	.plate-num-text {
		flex: 1;
		line-height: 100rpx;
		height: 100%;
		box-sizing: border-box;
		border-radius: 4rpx;
		font-size: 40rpx;
		font-weight: normal;
	}
	
	.new-plate-input-content {
		display: flex;
		flex-direction: row;
		height: 100rpx;
	}
	
	.plat-btn-black {
		height: 100rpx;
	}
	
	.plat-btn {
		padding-top: 80rpx;
		width: 96%;
		margin: 0 auto;
	}
	
	
	.kb_top {
		align-content: relative;
		width: 100%;
		height: 74rpx;
		background: #fff;
		border-top: solid #ebebeb 2rpx;
		border-bottom: 15rpx solid #d7d8dc;
	}
	
	.keyboard {
		z-index: 9999;
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		height: auto;
		background: #d7d8dc;
		display: flex;
		flex-wrap: wrap;
		border-bottom: 15rpx solid #d7d8dc;
	}
	
	.td {
		font-family: "微软雅黑";
		flex-grow: 1;
		text-align: center;
		font-size: 34rpx;
		height: 86rpx;
		line-height: 80rpx;
		background: #fff;
		margin: 10rpx 5rpx;
		color: #333;
		border-radius: 10rpx 10rpx 10rpx 10rpx;
		box-shadow: 0rpx 2rpx 0rpx #a9a9a9;
	}
	
	.td_nor {
		flex: 1 1 6%;
	}
	
	.td_num {
		flex: 1 1 8%;
	}
	
	.td_spec {
		flex: 1 1 12%;
	}
	
	.board_bg {
		box-shadow: 0 0 0 #e5e5e5;
		background: #e5e5e5;
	}
	
	.del-first {
		position: absolute;
		bottom: 10rpx;
		right: 10rpx;
		width: 137rpx;
		height: 86rpx;
		background-color: #fff;
		box-shadow: 0rpx 2rpx 0rpx #a9a9a9;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 10rpx;
	}
	
	.del-hover {
		position: absolute;
		bottom: 10rpx;
		right: 10rpx;
		width: 137rpx;
		height: 86rpx;
		background-color: #e5e5e5;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 10rpx;
		box-shadow: 0 0 0 #e5e5e5;
	}
	
	.del-img {
		display: block;
		width: 46rpx;
		height: 38rpx;
	}
	
	.temp-history{
		font-size: 38upx;
		color: #888888;
	}
	
	.temp-remark{
		margin-top: 300upx;
		padding:10upx;
		color: #888888;
	}
	.input-ui{
				border: 1px solid #ccc;
				padding: 12upx 18upx;
				border-radius: 8upx;
				color: rgb(48, 49, 51);
				width: 100%;
				height: 100%;
			}
</style>
