<template>
	<view>
		<view class="block__title">申请通行码</view>
		<view class="cu-form-group">
			<view class="title">手机号</view>
			<input v-model="personTel" placeholder="请输入手机号" class="text-right"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">验证码</view>
			<input v-model="msgCode" placeholder="请输入短信验证码" name="input"></input>
			<button class='cu-btn bg-blue shadow' :disabled="btnDisabled" @click="_sendMsgCode()">{{btnValue}}</button>
		</view>
		<view class="flex flex-direction margin-top">
			<button class="cu-btn bg-blue margin-tb-sm lg" @click="submitApplyFace()">申请</button>
		</view>
	</view>
</template>

<script>
	import {
		sendSmsCode
	} from '@/api/user/userApi.js';

	export default {
		name: "applyPassQrcode",
		data() {
			return {
				personTel: '',
				msgCode: '',
				btnValue: '验证码',
				btnDisabled: false,
			};
		},
		props: {
			communityId: {
				type: String,
			},
			pqsId: {
				type: String,
			},
			openId: {
				type: String,
			}
		},
		methods: {
			_sendMsgCode: function() {
				sendSmsCode(this.personTel, this);
			},
		}
	}
</script>

<style lang="scss">
	.block__title {
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 0rpx 30rpx 20rpx;
	}
</style>