<template>
	<view v-if="state == 'ON'">
		<view class="plate-context">
			<view class="ak_row_black">
			</view>
			<view class="ak_row_title text-center">
				<text>水电充值</text>
			</view>

			<view class="tips_block">
			</view>
			<view class="plate-input-body">
				<view v-if="queryWay == 'ROOM'" class="plate-nums-first cu-form-group" @click="_chooseRoom()"><text>{{roomName || '请选择房屋 >'}}</text></view>
				<input v-else v-model="address" placeholder="请输入表号" class="plate-nums-first cu-form-group"></input>
			</view>

			<view class="cu-bar btn-group" style="margin-top: 30px;">
				<button @click="_queryMeter" :disabled="address == '' && roomId == ''"
					class="cu-btn bg-green shadow-blur round lg">立即查询</button>
			</view>
			<view class="cu-bar btn-group" style="margin-top: 30px;">
				<button @click="_queryMeterRechargeRecords" :disabled="address == '' && roomId == ''"
					class="cu-btn bg-green shadow-blur round lg">充值记录</button>
			</view>

			<view class="temp-remark">
				<view>水电充值提示</view>
				<view>1、水电充值分为房屋/表号两种方式；</view>
				<view>2、房屋方式只需要选择对应房屋即可；表号方式请输入正确表号，前后不要有空格；</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {listMeterQrcode, listMeterMachine} from '../../api/meter/meterApi.js'

	export default {
		data() {
			return {
				communityId: '',
				mqId: '',
				queryWay: '',
				createStaffId: '',
				state: '',
				roomId: '',
				roomName: '',
				address: '',
			}
		},
		components: {

		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.mqId = options.mqId;
			this.queryMeterQrcode();
		},
		methods: {
			queryMeterQrcode: function() {
				let _objData = {
					page: 1,
					row: 1,
					communityId: this.communityId,
					mqId: this.mqId
				}
				listMeterQrcode(_objData).then(_data => {
					if (!_data.data || _data.data.length != 1) {
						uni.showToast({
							icon: 'none',
							title: '二维码已失效'
						});
						return;
					}
					if(_data.data[0].state != 'ON') {
						uni.showToast({
							icon: 'none',
							title: '二维码已停用'
						});
						return;
					}
					this.queryWay = _data.data[0].queryWay;
					this.createStaffId = _data.data[0].createStaffId;
					this.state = _data.data[0].state;
				})
			},
			_chooseRoom: function() {
				uni.navigateTo({
					url: '/pages/room/selectRoom?communityId='+this.communityId
				})
			},
			_queryMeterRechargeRecords: function() {
				let _objData = {
					communityId: this.communityId,
				}
				let _that = this;
				let _url = '/pages/meter/meterRechargeRecords?communityId=' + _that.communityId + 
				"&createStaffId=" + _that.createStaffId;
				if(_that.address) {
					_url += "&address=" + _that.address;
					_objData.row = 1;
					_objData.page = 1;
					_objData.address = _that.address;
				}
				if(_that.roomId) {
					_url += "&roomId=" + _that.roomId + "&roomName=" + _that.roomName;
					_objData.row = 1;
					_objData.page = -1;
					_objData.roomId = _that.roomId;
					_objData.roomName = _that.roomName;
				}
				this._queryMeterMachine(_objData, _url);
			},
			_queryMeterMachine: function(_objData, _url) {
				listMeterMachine(_objData).then(_data => {
					if (!_data.data || _data.data.length < 1) {
						uni.showToast({
							icon: 'none',
							title: '水电表不存在或房屋无水电表'
						});
						return;
					}
					uni.navigateTo({
						url: _url
					})
				})
			},
			_queryMeter: function() {
				let _objData = {
					communityId: this.communityId,
				}
				let _that = this;
				let _url = '';
				if(_that.address) {
					_url += '/pages/meter/preStoreMeter?communityId=' + _that.communityId + 
					"&createStaffId=" + _that.createStaffId + "&address=" + _that.address;
					_objData.row = 1;
					_objData.page = 1;
					_objData.address = _that.address;
				}
				if(_that.roomId) {
					_url += '/pages/meter/selectMeter?communityId=' + _that.communityId + 
					"&createStaffId=" + _that.createStaffId + "&roomId=" + _that.roomId + 
					"&roomName=" + _that.roomName;
					_objData.row = 1;
					_objData.page = -1;
					_objData.roomId = _that.roomId;
					_objData.roomName = _that.roomName;
				}
				this._queryMeterMachine(_objData, _url);
			},
		},
		onShow: function() {
			let _selectRoom = uni.getStorageSync('selectRoom');
			if(_selectRoom){
				uni.removeStorageSync('selectRoom');
				this.roomId = _selectRoom.roomId;
				this.roomName = _selectRoom.roomName;
			}
		},
	}
</script>

<style>
	@import "./meterCharge.css";

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