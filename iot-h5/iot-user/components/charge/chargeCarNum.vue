<template>
	<view>
		<view class="margin-xs  radius" v-if="login" >
			车牌：<text @click="_editChargeCarNum()" class="charge-carnum">{{carNum || '未设置'}}</text>
		</view>
		<view class="cu-modal " :class="showModel?'show':''">
			<view class="cu-dialog padding-bottom-lg" style="height: 400upx;">
				<view class="flex justify-between  padding padding-bottom-xs">
					<view>修改充电车牌</view>
					<view @click="_closeEditModel"><text class="cuIcon-roundclose "></text></view>
				</view>
				<view class="cu-form-group">
					<view class="title">车牌</view>
					<input v-model="carNum" placeholder="请输入车牌" class="text-right"></input>
				</view>
				<view class="flex flex-direction padding">
					<button class="cu-btn bg-green margin-tb-sm lg" @click="_doEditCarNum()">提交</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserChargeCar,
		saveUserChargeCar
	} from '@/api/user/userApi.js';
	export default {
		name: "chargeCarNum",
		data() {
			return {
				showModel: false,
				carNum:'',
				login:false,
			};
		},
		methods: {
			initUserChargeCarNum(_login){
				this.login = _login;
				if(!this.login){
					return;
				}
				let _that = this;
				getUserChargeCar({
					page:1,
					row:1
				}).then(_data=>{
					console.log(_data);
					if(_data.code == '0'){
						_that.carNum = _data.data;
					}
				})
			},
			_doEditCarNum:function(){
				let _that = this;
				saveUserChargeCar({
					carNum:this.carNum
				}).then(_data=>{
					_that.initUserChargeCarNum(_that.login);
					_that._closeEditModel();
				})
			},
			_editChargeCarNum:function(){
				this.showModel = true;
			},
			_closeEditModel:function(){
				this.showModel =false;
			}
		}
	}
</script>

<style lang="scss">
	.charge-carnum {
	    color: #0FBA82;
	    text-decoration: underline;
	}
</style>