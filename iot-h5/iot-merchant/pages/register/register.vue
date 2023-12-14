<template>
	<view>
		<view class="block__title">注册信息</view>
		<view class="cu-form-group ">
			<view class="title">地区</view>
			<pickerAddress @change="_changeAreaCode"></pickerAddress>
		</view>
		<view class="cu-form-group">
			<view class="title">商铺名称</view>
			<input v-model="shopName" required clearable placeholder="请输入商铺名称"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">手机号</view>
			<input v-model="link" required label="手机号" clearable placeholder="请输入手机号"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">密码</view>
			<input v-model="password" required type="password" label="密码" clearable placeholder="请输入密码"></input>
		</view>
		<msg-code :link="link" ref="msgCodeRef"></msg-code>

		<view class="padding flex flex-direction margin-top">
			<button class="cu-btn bg-blue   lg" @click="_doRegister()">开店</button>
		</view>
	</view>
</template>

<script>
	import {registerStoreAndShop} from '@/api/user/userApi.js'
	import pickerAddress from '@/components/pickerAddress/pickerAddress.vue'
	import msgCode from '@/components/msg-code/msg-code.vue'

	export default {
		data() {
			return {
				link: '',
				areaCode: '',
				shopName: '',
				password: '',

			};
		},
		components: {
			pickerAddress,
			msgCode
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			let _that = this;
		},
		methods: {
			_doRegister: function() {
				let _code = this.$refs.msgCodeRef.getMsgCode()
				let obj = {
					"link": this.link,
					"msgCode": _code,
					"password": this.password,
					"shopName": this.shopName,
					"areaCode":this.areaCode
				}
				let msg = "";
				if (this.password == '' ) {
					msg = "请填写密码";
				} else if (obj.link == "") {
					msg = "请填写手机号";
				} else if (obj.code == "") {
					msg = "请填写验证码";
				}else if (obj.shopName == "") {
					msg = "请填写店铺";
				}
				if (msg != "") {
					wx.showToast({
						title: msg,
						icon: 'none',
						duration: 2000
					})
					return;
				}
				console.log("提交数据", obj);
				registerStoreAndShop(obj,this)
				.then(_data=>{
					console.log(_data,123)
					uni.showToast({
						icon:'none',
						title:'注册成功，去登录'
					})
					uni.navigateBack({
						delta:1,
					});
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					})
				})
				
			},
			_changeAreaCode: function(data) {
				let _that = this;
				_that.areaCode = data.data[2].code;
			},
		}
	};
</script>
<style>
	.block__title {
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 40rpx 30rpx 20rpx;
	}

	.button_up_blank {
		height: 40rpx;
		text-align: center;
	}
</style>
